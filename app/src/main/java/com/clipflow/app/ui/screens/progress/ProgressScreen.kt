package com.clipflow.app.ui.screens.progress

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clipflow.app.ui.components.AnimatedBackground
import kotlinx.coroutines.delay

@Composable
fun ProgressScreen(onDone: () -> Unit) {
    var progress by remember { mutableStateOf(0f) }
    val steps = listOf("Downloading…", "Transcribing…", "Finding highlights…", "Cutting clips…", "Exporting…")
    var stepIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        repeat(100) {
            delay(40)
            progress = (it + 1) / 100f
            stepIndex = ((progress * steps.size).toInt()).coerceAtMost(steps.size - 1)
        }
        delay(400)
        onDone()
    }

    val infinite = rememberInfiniteTransition(label = "orb")
    val pulse by infinite.animateFloat(1f, 1.15f, infiniteRepeatable(tween(1200, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "orbPulse")

    AnimatedBackground {
        Column(
            Modifier.fillMaxSize().padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(Modifier.size(140.dp).scale(pulse), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF06D6E8),
                    trackColor = Color.White.copy(alpha = 0.08f),
                    strokeWidth = 8.dp
                )
                Text("${(progress * 100).toInt()}%", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(32.dp))
            Text(steps[stepIndex], color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}
