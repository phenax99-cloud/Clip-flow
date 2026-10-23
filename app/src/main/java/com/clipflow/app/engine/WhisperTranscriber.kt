package com.clipflow.app.engine

import android.content.Context
import io.github.givimad.whisperjni.WhisperContext
import io.github.givimad.whisperjni.WhisperFullParams
import io.github.givimad.whisperjni.WhisperJNI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File

object WhisperTranscriber {

    private val mutex = Mutex()
    private var jni: WhisperJNI? = null
    private var ctx: WhisperContext? = null

    suspend fun loadModel(context: Context, modelFile: File): Boolean =
        withContext(Dispatchers.IO) {
            mutex.withLock {
                if (ctx != null) return@withLock true
                return@withLock try {
                    if (!modelFile.exists()) return@withLock false
                    WhisperJNI.loadLibrary()
                    val localJni = WhisperJNI()
                    // Public init() takes a java.nio.file.Path, not a String.
                    val localCtx = localJni.init(modelFile.toPath())
                        ?: return@withLock false
                    jni = localJni
                    ctx = localCtx
                    true
                } catch (_: Throwable) {
                    false
                }
            }
        }

    suspend fun transcribe(pcmSamples: FloatArray): String? =
        withContext(Dispatchers.IO) {
            mutex.withLock {
                val localJni = jni ?: return@withLock null
                val localCtx = ctx ?: return@withLock null
                if (pcmSamples.isEmpty()) return@withLock null
                try {
                    val params = WhisperFullParams()
                    val ok = localJni.full(localCtx, params, pcmSamples, pcmSamples.size)
                    if (ok != 0) return@withLock null
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
        }

    suspend fun release() = withContext(Dispatchers.IO) {
        mutex.withLock {
            try { ctx?.close() } catch (_: Throwable) {}
            ctx = null
            jni = null
        }
    }
}
