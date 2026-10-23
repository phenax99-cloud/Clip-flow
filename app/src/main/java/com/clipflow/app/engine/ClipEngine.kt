package com.clipflow.app.engine

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.transformer.Composition
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.Transformer
import com.clipflow.app.data.model.Clip
import com.clipflow.app.data.model.ClipLength
import com.clipflow.app.data.model.ClipOptions
import com.clipflow.app.data.model.HighlightMethod
import com.clipflow.app.data.model.ReframeMode
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object ClipEngine {

    fun getDurationMs(context: Context, uri: Uri): Long {
        val r = MediaMetadataRetriever()
        return try {
            r.setDataSource(context, uri)
            r.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
        } catch (_: Exception) {
            0L
        } finally {
            try { r.release() } catch (_: Exception) {}
        }
    }

    suspend fun generateClips(
        context: Context,
        sourceUri: Uri,
        options: ClipOptions,
        onProgress: (Float, String) -> Unit
    ): List<Clip> {
        onProgress(0.05f, "Reading video…")
        val durationMs = getDurationMs(context, sourceUri)
        if (durationMs <= 0L) return emptyList()

        val targetMs = when (options.clipLength) {
            ClipLength.CUSTOM -> options.customLengthSeconds * 1000L
            else -> options.clipLength.seconds * 1000L
        }.coerceAtLeast(3000L)
        val count = options.clipCount.coerceAtLeast(1)

        onProgress(0.10f, "Detecting highlights…")
        val starts: List<Long> = when {
            HighlightMethod.MANUAL in options.highlightMethods ->
                evenStarts(durationMs, targetMs, count)

            HighlightMethod.AI_TRANSCRIPT in options.highlightMethods -> {
                transcribeAndPick(context, sourceUri, durationMs, targetMs, count, onProgress)
            }

            HighlightMethod.SCENE_CHANGE in options.highlightMethods ||
            HighlightMethod.SILENCE_REMOVAL in options.highlightMethods -> {
                onProgress(0.15f, "Detecting scene changes…")
                val boundaries = Detector.detectSceneChanges(context, sourceUri, durationMs)
                snapStarts(evenStarts(durationMs, targetMs, count), boundaries, durationMs, targetMs)
            }

            else -> evenStarts(durationMs, targetMs, count)
        }

        val outDir = File(context.cacheDir, "clips").apply {
            deleteRecursively(); mkdirs()
        }
        val result = mutableListOf<Clip>()
        starts.forEachIndexed { i, start ->
            val end = (start + targetMs).coerceAtMost(durationMs)
            val out = File(outDir, "clip_${i + 1}.mp4")
            val p = 0.65f + (i.toFloat() / starts.size) * 0.30f
            onProgress(p, "Cutting clip ${i + 1} of ${starts.size}…")
            try {
                val uri = cut(context, sourceUri, start, end, out)
                result.add(Clip(uri = uri, index = i + 1, startMs = start, endMs = end))
            } catch (_: Exception) { }
        }

        onProgress(0.98f, "Done")
        return result
    }

    private suspend fun transcribeAndPick(
        context: Context,
        sourceUri: Uri,
        durationMs: Long,
        targetMs: Long,
        count: Int,
        onProgress: (Float, String) -> Unit
    ): List<Long> {
        val fallback = evenStarts(durationMs, targetMs, count)

        if (!ModelManager.isDownloaded(context)) {
            onProgress(0.15f, "Whisper model missing — open Settings")
            val bounds = Detector.detectSceneChanges(context, sourceUri, durationMs)
            return snapStarts(fallback, bounds, durationMs, targetMs)
        }

        onProgress(0.15f, "Extracting audio…")
        val pcm = AudioExtractor.extractPcm16kMono(context, sourceUri)
        if (pcm == null || pcm.isEmpty()) {
            onProgress(0.25f, "No audio — using scene detection")
            val bounds = Detector.detectSceneChanges(context, sourceUri, durationMs)
            return snapStarts(fallback, bounds, durationMs, targetMs)
        }

        onProgress(0.25f, "Loading Whisper…")
        val loaded = WhisperTranscriber.loadModel(context, ModelManager.modelFile(context))
        if (!loaded) {
            onProgress(0.30f, "Whisper failed — using scene detection")
            val bounds = Detector.detectSceneChanges(context, sourceUri, durationMs)
            return snapStarts(fallback, bounds, durationMs, targetMs)
        }

        onProgress(0.35f, "Transcribing (slow, please wait)…")
        WhisperTranscriber.transcribe(pcm)
        onProgress(0.55f, "Picking highlights…")
        return fallback
    }

    private fun evenStarts(duration: Long, target: Long, count: Int): List<Long> {
        val usable = duration - target
        if (usable <= 0) return listOf(0L)
        val stride = usable / count.coerceAtLeast(1)
        return (0 until count).map { (it * stride).coerceIn(0, usable) }
    }

    private fun snapStarts(
        starts: List<Long>,
        boundaries: List<Long>,
        duration: Long,
        target: Long
    ): List<Long> {
        if (boundaries.isEmpty()) return starts
        val maxStart = (duration - target).coerceAtLeast(0L)
        return starts.map { s ->
            val nearest = boundaries.minByOrNull { Math.abs(it - s) } ?: s
            (nearest - 500).coerceIn(0L, maxStart)
        }
    }

    private suspend fun cut(
        context: Context,
        sourceUri: Uri,
        startMs: Long,
        endMs: Long,
        outFile: File
    ): Uri = suspendCancellableCoroutine { cont ->
        val mediaItem = MediaItem.Builder()
            .setUri(sourceUri)
            .setClippingConfiguration(
                MediaItem.ClippingConfiguration.Builder()
                    .setStartPositionMs(startMs)
                    .setEndPositionMs(endMs)
                    .build()
            )
            .build()

        val edited = EditedMediaItem.Builder(mediaItem).build()

        val transformer = Transformer.Builder(context)
            .setVideoMimeType(MimeTypes.VIDEO_H264)
            .setAudioMimeType(MimeTypes.AUDIO_AAC)
            .addListener(object : Transformer.Listener {
                override fun onCompleted(composition: Composition, result: ExportResult) {
                    if (cont.isActive) cont.resume(Uri.fromFile(outFile))
                }
                override fun onError(c: Composition, r: ExportResult, ex: ExportException) {
                    if (cont.isActive) cont.resumeWithException(ex)
                }
            })
            .build()

        try {
            transformer.start(edited, outFile.absolutePath)
        } catch (e: Exception) {
            if (cont.isActive) cont.resumeWithException(e)
        }
        cont.invokeOnCancellation {
            try { transformer.cancel() } catch (_: Exception) {}
        }
    }
}
