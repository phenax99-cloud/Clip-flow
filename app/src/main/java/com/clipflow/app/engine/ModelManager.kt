package com.clipflow.app.engine

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.util.concurrent.TimeUnit

object ModelManager {

    private const val MODEL_URL =
        "https://huggingface.co/ggerganov/whisper.cpp/resolve/main/ggml-tiny.bin"
    private const val MODEL_FILENAME = "ggml-tiny.bin"
    private const val MIN_VALID_BYTES = 10_000_000L

    fun modelFile(context: Context): File = File(context.filesDir, MODEL_FILENAME)

    fun isDownloaded(context: Context): Boolean {
        val f = modelFile(context)
        return f.exists() && f.length() > MIN_VALID_BYTES
    }

    suspend fun download(
        context: Context,
        onProgress: (Float) -> Unit
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(600, TimeUnit.SECONDS)
                .build()
            val req = Request.Builder().url(MODEL_URL).build()
            client.newCall(req).execute().use { resp ->
                if (!resp.isSuccessful) return@withContext false
                val body = resp.body ?: return@withContext false
                val total = body.contentLength()
                val target = modelFile(context)
                val tmp = File(target.absolutePath + ".tmp")
                body.byteStream().use { input ->
                    tmp.outputStream().use { output ->
                        val buf = ByteArray(8192)
                        var read: Int
                        var written = 0L
                        while (input.read(buf).also { read = it } >= 0) {
                            output.write(buf, 0, read)
                            written += read
                            if (total > 0) onProgress((written.toFloat() / total).coerceIn(0f, 1f))
                        }
                    }
                }
                if (tmp.length() > MIN_VALID_BYTES) {
                    tmp.renameTo(target)
                    true
                } else {
                    tmp.delete()
                    false
                }
            }
        } catch (_: Exception) {
            false
        }
    }
}
