package com.clipflow.app.data.model

enum class InputType { YOUTUBE_URL, GALLERY, RECORD }

enum class HighlightMethod { AI_TRANSCRIPT, SILENCE_REMOVAL, SCENE_CHANGE, MANUAL, VIRALITY_SCORE }

enum class ClipLength(val seconds: Int, val label: String) {
    SHORT(15, "15s"), MEDIUM(30, "30s"), LONG(60, "60s"), CUSTOM(0, "Custom")
}

enum class ReframeMode { FACE_TRACK, CENTER_CROP, BLUR_BG, NONE }

enum class CaptionStyle { NONE, CLEAN, BOLD, KARAOKE }

enum class Engine { ON_DEVICE, CLOUD }

data class ClipOptions(
    val inputType: InputType = InputType.YOUTUBE_URL,
    val youtubeUrl: String = "",
    val highlightMethods: Set<HighlightMethod> = setOf(HighlightMethod.AI_TRANSCRIPT),
    val clipLength: ClipLength = ClipLength.MEDIUM,
    val customLengthSeconds: Int = 30,
    val clipCount: Int = 3,
    val overlapClips: Boolean = false,
    val reframeMode: ReframeMode = ReframeMode.FACE_TRACK,
    val captionStyle: CaptionStyle = CaptionStyle.BOLD,
    val addTitleOverlay: Boolean = true,
    val addEmojiHashtags: Boolean = false,
    val engine: Engine = Engine.ON_DEVICE
)
