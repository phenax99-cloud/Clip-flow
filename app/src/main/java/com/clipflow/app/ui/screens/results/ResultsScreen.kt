package com.clipflow.app.ui.screens.results

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.clipflow.app.data.model.Clip
import com.clipflow.app.data.model.ClipStore
import com.clipflow.app.ui.components.AnimatedBackground
import com.clipflow.app.ui.components.GlassCard
import com.clipflow.app.ui.components.GradientButton
import com.clipflow.app.ui.theme.FlowCyan
import com.clipflow.app.ui.theme.FlowPurple

@Composable
fun ResultsScreen(onHome: () -> Unit) {
    val clips = ClipStore.clips

    AnimatedBackground {
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            Text("Your Clips", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(
                if (clips.isEmpty()) "No clips were generated" else "${clips.size} clips ready",
                color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp
            )
            Spacer(Modifier.height(16.dp))

            LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(clips, key = { it.uri.toString() }) { ClipPlayerCard(it) }
            }
            Spacer(Modifier.height(12.dp))
            GradientButton("Back to Home", Modifier.fillMaxWidth(), onClick = onHome)
        }
    }
}

@Composable
private fun ClipPlayerCard(clip: Clip) {
    val ctx = LocalContext.current
    val player = remember {
        ExoPlayer.Builder(ctx).build().apply {
            setMediaItem(MediaItem.fromUri(clip.uri))
            prepare()
            playWhenReady = false
        }
    }
    DisposableEffect(Unit) { onDispose { player.release() } }

    GlassCard(Modifier.fillMaxWidth()) {
        Box(
            Modifier.fillMaxWidth().height(220.dp).background(
                Brush.linearGradient(listOf(FlowPurple.copy(alpha = 0.4f), FlowCyan.copy(alpha = 0.4f))),
                RoundedCornerShape(16.dp)
            ),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                factory = { c ->
                    PlayerView(c).apply {
                        this.player = player
                        useController = true
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(Modifier.height(10.dp))
        Text("Clip #${clip.index}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(
            "${clip.durationSec}s · ${formatTime(clip.startMs)}",
            color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp
        )
    }
}

private fun formatTime(ms: Long): String {
    val s = ms / 1000; val m = s / 60; val r = s % 60
    return "%d:%02d".format(m, r)
}
