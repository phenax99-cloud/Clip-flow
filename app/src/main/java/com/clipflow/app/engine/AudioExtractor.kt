package com.clipflow.app.engine

import android.content.Context
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

object AudioExtractor {

    private const val TARGET_RATE = 16_000

    /**
     * Decode the video's audio track and return mono 16 kHz Float samples in [-1..1].
     * Returns null if the video has no audio track or decoding fails.
     */
    suspend fun extractPcm16kMono(context: Context, videoUri: Uri): FloatArray? =
        withContext(Dispatchers.IO) {
            var extractor: MediaExtractor? = null
            var codec: MediaCodec? = null
            try {
                extractor = MediaExtractor()
                context.contentResolver.openFileDescriptor(videoUri, "r")?.use { pfd ->
                    extractor.setDataSource(pfd.fileDescriptor)
                } ?: return@withContext null

                var trackIndex = -1
                var format: MediaFormat? = null
                for (i in 0 until extractor.trackCount) {
                    val f = extractor.getTrackFormat(i)
                    val mime = f.getString(MediaFormat.KEY_MIME) ?: continue
                    if (mime.startsWith("audio/")) {
                        trackIndex = i
                        format = f
                        break
                    }
                }
                if (trackIndex < 0 || format == null) return@withContext null
                extractor.selectTrack(trackIndex)

                val mime = format.getString(MediaFormat.KEY_MIME)!!
                val srcRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE)
                val channels = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT)

                codec = MediaCodec.createDecoderByType(mime)
                codec.configure(format, null, null, 0)
                codec.start()

                val pcm = ByteArrayOutputStream()
                val info = MediaCodec.BufferInfo()
                var inputDone = false
                var outputDone = false

                while (!outputDone) {
                    if (!inputDone) {
                        val inIdx = codec.dequeueInputBuffer(10_000)
                        if (inIdx >= 0) {
                            val inBuf = codec.getInputBuffer(inIdx)!!
                            val size = extractor.readSampleData(inBuf, 0)
                            if (size < 0) {
                                codec.queueInputBuffer(
                                    inIdx, 0, 0, 0,
                                    MediaCodec.BUFFER_FLAG_END_OF_STREAM
                                )
                                inputDone = true
                            } else {
                                codec.queueInputBuffer(inIdx, 0, size, extractor.sampleTime, 0)
                                extractor.advance()
                            }
                        }
                    }
                    val outIdx = codec.dequeueOutputBuffer(info, 10_000)
                    if (outIdx >= 0) {
                        val outBuf = codec.getOutputBuffer(outIdx)!!
                        if (info.size > 0) {
                            val chunk = ByteArray(info.size)
                            outBuf.position(info.offset)
                            outBuf.get(chunk, 0, info.size)
                            pcm.write(chunk)
                        }
                        codec.releaseOutputBuffer(outIdx, false)
                        if (info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                            outputDone = true
                        }
                    }
                }

                val raw = pcm.toByteArray()
                val sampleCount = raw.size / 2
                if (sampleCount == 0) return@withContext null

                val shortSamples = ShortArray(sampleCount)
                for (i in 0 until sampleCount) {
                    val lo = raw[i * 2].toInt() and 0xFF
                    val hi = raw[i * 2 + 1].toInt()
                    shortSamples[i] = (((hi shl 8) or lo).toShort())
                }

                val mono = if (channels > 1) {
                    val monoLen = sampleCount / channels
                    FloatArray(monoLen) { idx ->
                        var sum = 0f
                        for (c in 0 until channels) {
                            sum += shortSamples[idx * channels + c] / 32768f
                        }
                        sum / channels
                    }
                } else {
                    FloatArray(sampleCount) { shortSamples[it] / 32768f }
                }

                resample(mono, srcRate, TARGET_RATE)
            } catch (_: Throwable) {
                null
            } finally {
                try { codec?.stop() } catch (_: Throwable) {}
                try { codec?.release() } catch (_: Throwable) {}
                try { extractor?.release() } catch (_: Throwable) {}
            }
        }

    private fun resample(input: FloatArray, srcRate: Int, dstRate: Int): FloatArray {
        if (srcRate == dstRate || input.isEmpty()) return input
        val ratio = srcRate.toDouble() / dstRate.toDouble()
        val outLen = (input.size / ratio).toInt()
        if (outLen <= 0) return FloatArray(0)
        val out = FloatArray(outLen)
        for (i in 0 until outLen) {
            val pos = i * ratio
            val idx = pos.toInt()
            val frac = (pos - idx).toFloat()
            val next = if (idx + 1 < input.size) idx + 1 else idx
            out[i] = input[idx] * (1f - frac) + input[next] * frac
        }
        return out
    }
}
