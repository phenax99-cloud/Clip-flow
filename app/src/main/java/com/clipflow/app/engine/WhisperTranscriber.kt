package com.clipflow.app.engine

import android.content.Context
import io.github.givimad.whisperjni.WhisperContext
import io.github.givimad.whisperjni.WhisperFullParams
import io.github.givimad.whisperjni.WhisperJNI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object WhisperTranscriber {

    private var jni: WhisperJNI? = null
    private var ctx: WhisperContext? = null

    @Synchronized
    suspend fun loadModel(context: Context, modelFile: File): Boolean =
        withContext(Dispatchers.IO) {
            if (ctx != null) return@withContext true
            return@withContext try {
                if (!modelFile.exists()) return@withContext false
                WhisperJNI.loadLibrary()
                val localJni = WhisperJNI()
                val localCtx = localJni.initContext(modelFile.absolutePath)
                jni = localJni
                ctx = localCtx
                true
            } catch (_: Throwable) {
                false
            }
        }

    /**
     * pcmSamples must be 16 kHz, mono, Float [-1..1].
     * Returns the full transcript or null on failure.
     */
    @Synchronized
    suspend fun transcribe(pcmSamples: FloatArray): String? =
        withContext(Dispatchers.IO) {
            val localJni = jni ?: return@withContext null
            val localCtx = ctx ?: return@withContext null
            if (pcmSamples.isEmpty()) return@withContext null
            try {
                val params = WhisperFullParams().apply {
                    // defaults are fine on mobile; set threads if you want
                }
                val ok = localJni.fullTranscribe(localCtx, params, pcmSamples)
                if (ok != 0) return@withContext null
                val n = localJni.fullNSegments(localCtx)
                val sb = StringBuilder()
                for (i in 0 until n) {
                    sb.append(localJni.fullGetSegmentText(localCtx, i)).append(' ')
                }
                sb.toString().trim().ifEmpty { null }
            } catch (_: Throwable) {
                null
            }
        }

    @Synchronized
    fun release() {
        try { ctx?.let { jni?.freeContext(it) } } catch (_: Throwable) {}
        ctx = null
        jni = null
    }
}
