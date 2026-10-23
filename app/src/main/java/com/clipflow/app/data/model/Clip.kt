package com.clipflow.app.data.model

import android.net.Uri

data class Clip(
    val uri: Uri,
    val index: Int,
    val startMs: Long,
    val endMs: Long,
    val viralityScore: Int? = null
) {
    val durationSec: Int get() = ((endMs - startMs) / 1000).toInt()
}

object ClipStore {
    @Volatile var clips: List<Clip> = emptyList()
}

object OptionsStore {
    @Volatile var options: ClipOptions = ClipOptions()
}
