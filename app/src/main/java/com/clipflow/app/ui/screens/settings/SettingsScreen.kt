package com.clipflow.app.ui.screens.settings

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clipflow.app.engine.ModelManager
import com.clipflow.app.ui.components.AnimatedBackground
import com.clipflow.app.ui.components.GlassCard
import com.clipflow.app.ui.components.GradientButton
import com.clipflow.app.ui.theme.FlowCyan
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    var downloaded by remember { mutableStateOf(ModelManager.isDownloaded(ctx)) }
    var downloading by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0f) }

    AnimatedBackground {
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
                Text("Settings", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(24.dp))

            GlassCard(Modifier.fillMaxWidth()) {
                Text("Whisper Model", color = Color.White, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Text(
                    "Tiny model (~75 MB) for on-device transcription. No API key needed. Fully offline.",
                    color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp
                )
                Spacer(Modifier.height(14.dp))

                when {
                    downloaded -> {
                        Text(
                            "✅ Model ready",
                            color = Color(0xFF22C55E),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    downloading -> {
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth(),
                            color = FlowCyan,
                            trackColor = Color.White.copy(alpha = 0.1f)
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Downloading… ${(progress * 100).toInt()}%",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 12.sp
                        )
                    }
                    else -> {
                        GradientButton("Download Model", Modifier.fillMaxWidth()) {
                            scope.launch {
                                downloading = true
                                val ok = ModelManager.download(ctx) { progress = it }
                                downloading = false
                                downloaded = ok
                                Toast.makeText(
                                    ctx,
                                    if (ok) "Model ready!" else "Download failed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        }
    }
}
