package com.clipflow.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val ClipFlowColors = darkColorScheme(
    primary = FlowPurple,
    onPrimary = FlowOnBg,
    secondary = FlowCyan,
    onSecondary = FlowBg,
    tertiary = FlowPink,
    background = FlowBg,
    onBackground = FlowOnBg,
    surface = FlowSurface,
    onSurface = FlowOnBg,
    surfaceVariant = FlowSurface,
    onSurfaceVariant = FlowMuted
)

@Composable
fun ClipFlowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ClipFlowColors,
        typography = Typography,
        content = content
    )
}
