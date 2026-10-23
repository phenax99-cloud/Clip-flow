package com.clipflow.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.clipflow.app.ui.theme.FlowBg
import com.clipflow.app.ui.theme.FlowCyan
import com.clipflow.app.ui.theme.FlowPink
import com.clipflow.app.ui.theme.FlowPurple

@Composable
fun AnimatedBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val infinite = rememberInfiniteTransition(label = "bg")
    val t1 by infinite.animateFloat(0f, 1f, infiniteRepeatable(tween(14000, easing = LinearEasing), RepeatMode.Reverse), label = "t1")
    val t2 by infinite.animateFloat(0f, 1f, infiniteRepeatable(tween(18000, easing = LinearEasing), RepeatMode.Reverse), label = "t2")

    Box(modifier = modifier.fillMaxSize().background(FlowBg)) {
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            drawCircle(
                brush = Brush.radialGradient(listOf(FlowPurple.copy(alpha = 0.35f), Color.Transparent), center = Offset(w * (0.2f + 0.3f * t1), h * (0.2f + 0.2f * t2)), radius = w * 0.9f),
                radius = w * 0.9f,
                center = Offset(w * (0.2f + 0.3f * t1), h * (0.2f + 0.2f * t2))
            )
            drawCircle(
                brush = Brush.radialGradient(listOf(FlowCyan.copy(alpha = 0.22f), Color.Transparent), center = Offset(w * (0.8f - 0.25f * t2), h * (0.7f + 0.15f * t1)), radius = w * 0.8f),
                radius = w * 0.8f,
                center = Offset(w * (0.8f - 0.25f * t2), h * (0.7f + 0.15f * t1))
            )
            drawCircle(
                brush = Brush.radialGradient(listOf(FlowPink.copy(alpha = 0.18f), Color.Transparent), center = Offset(w * (0.5f + 0.2f * t1), h * (0.4f - 0.15f * t2)), radius = w * 0.6f),
                radius = w * 0.6f,
                center = Offset(w * (0.5f + 0.2f * t1), h * (0.4f - 0.15f * t2))
            )
        }
        content()
    }
}
