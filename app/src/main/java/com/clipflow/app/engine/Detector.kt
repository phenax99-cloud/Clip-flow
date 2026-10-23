package com.clipflow.app.engine

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import kotlin.math.abs

object Detector {

    fun detectSceneChanges(context: Context, uri: Uri, durationMs: Long): List<Long> {
        val boundaries = mutableListOf<Long>()
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(context, uri)
            var prev: Bitmap? = null
            var t = 0L
            val stepMs = 1000L
            while (t < durationMs) {
                val frame = try {
                    retriever.getFrameAtTime(t * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                } catch (_: Exception) { null }
                if (frame != null) {
                    if (prev != null && frameDiff(prev, frame) > 0.35f) {
                        boundaries.add(t)
                    }
                    prev?.recycle()
                    prev = frame
                }
                t += stepMs
            }
            prev?.recycle()
        } catch (_: Exception) {
        } finally {
            try { retriever.release() } catch (_: Exception) {}
        }
        return boundaries
    }

    private fun frameDiff(a: Bitmap, b: Bitmap): Float {
        val w = 32
        val h = 32
        val sa = Bitmap.createScaledBitmap(a, w, h, false)
        val sb = Bitmap.createScaledBitmap(b, w, h, false)
        var diff = 0L
        var total = 0L
        for (y in 0 until h step 4) {
            for (x in 0 until w step 4) {
                val pa = sa.getPixel(x, y)
                val pb = sb.getPixel(x, y)
                diff += (abs((pa and 0xFF) - (pb and 0xFF)) +
                        abs((pa shr 8 and 0xFF) - (pb shr 8 and 0xFF)) +
                        abs((pa shr 16 and 0xFF) - (pb shr 16 and 0xFF))).toLong()
                total += 3 * 255L
            }
        }
        if (sa !== a) sa.recycle()
        if (sb !== b) sb.recycle()
        return if (total == 0L) 0f else diff.toFloat() / total
    }
}
