package com.clipflow.app.ui.screens.progress

import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clipflow.app.data.model.ClipStore
import com.clipflow.app.data.model.OptionsStore
import com.clipflow.app.engine.ClipEngine
import com.clipflow.app.ui.components.AnimatedBackground
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Composable
fun ProgressScreen(videoUri: Uri?, onDone: () -> Unit) {
    val ctx = LocalContext.current
    var progress by remember { mutableStateOf(0f) }
    var status by remember { mutableStateOf("Starting…") }

    LaunchedEffect(videoUri) {
        if (videoUri == null) { delay(300); onDone(); return@LaunchedEffect }
        try {
            val clips = withContext(Dispatchers.IO) {
                ClipEngine.generateClips(
                    context = ctx,
                    sourceUri = videoUri,
                    options = OptionsStore.options,
                    onProgress = { p, s -> progress = p; status = s }
                )
            }
            ClipStore.clips = clips
            progress = 1f
            status = "Done — ${clips.size} clips"
            delay(500)
            onDone()
        } catch (e: Exception) {
            status = "Failed: ${e.message}"
            delay(1500)
            onDone()
        }
    }

    val infinite = rememberInfiniteTransition(label = "orb")
    val pulse by infinite.animateFloat(
        1f, 1.15f,
        infiniteRepeatable(tween(1200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "orbPulse"
    )

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
            Text(status, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}
