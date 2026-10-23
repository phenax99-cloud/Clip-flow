package com.clipflow.app.ui.screens.options

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clipflow.app.data.model.*
import com.clipflow.app.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionsScreen(onBack: () -> Unit, onStart: () -> Unit) {
    var options by remember { mutableStateOf(ClipOptions()) }

    AnimatedBackground {
        Column(Modifier.fillMaxSize()) {
            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
                Text("Clip Options", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            Column(
                Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SummaryCard(options, Modifier.fillMaxWidth())

                GlassCard(Modifier.fillMaxWidth()) {
                    SectionHeader("🎯", "Highlight Detection")
                    Spacer(Modifier.height(10.dp))
                    FlowRowChips {
                        HighlightMethod.entries.forEach { m ->
                            val soon = m == HighlightMethod.AI_TRANSCRIPT || m == HighlightMethod.VIRALITY_SCORE
                            PillChip(m.name.replace("_", " "), m in options.highlightMethods, comingSoon = soon) {
                                val s = options.highlightMethods.toMutableSet()
                                if (m in s) s.remove(m) else s.add(m)
                                options = options.copy(highlightMethods = s)
                            }
                        }
                    }
                }

                GlassCard(Modifier.fillMaxWidth()) {
                    SectionHeader("⏱️", "Clip Length")
                    Spacer(Modifier.height(10.dp))
                    FlowRowChips {
                        ClipLength.entries.forEach { l ->
                            PillChip(l.label, options.clipLength == l) { options = options.copy(clipLength = l) }
                        }
                    }
                }

                GlassCard(Modifier.fillMaxWidth()) {
                    SectionHeader("🔢", "Number of Clips")
                    Spacer(Modifier.height(10.dp))
                    FlowRowChips {
                        listOf(3, 5, 10).forEach { c ->
                            PillChip("$c", options.clipCount == c) { options = options.copy(clipCount = c) }
                        }
                    }
                }

                GlassCard(Modifier.fillMaxWidth()) {
                    SectionHeader("🔄", "Reframe")
                    Spacer(Modifier.height(10.dp))
                    FlowRowChips {
                        ReframeMode.entries.forEach { r ->
                            PillChip(r.name.replace("_", " "), options.reframeMode == r) { options = options.copy(reframeMode = r) }
                        }
                    }
                }

                GlassCard(Modifier.fillMaxWidth()) {
                    SectionHeader("💬", "Captions")
                    Spacer(Modifier.height(10.dp))
                    FlowRowChips {
                        CaptionStyle.entries.forEach { c ->
                            PillChip(c.name, options.captionStyle == c) { options = options.copy(captionStyle = c) }
                        }
                    }
                }

                GlassCard(Modifier.fillMaxWidth()) {
                    SectionHeader("⚡", "Engine")
                    Spacer(Modifier.height(10.dp))
                    FlowRowChips {
                        Engine.entries.forEach { e ->
                            PillChip(e.name.replace("_", " "), options.engine == e) { options = options.copy(engine = e) }
                        }
                    }
                }

                GlassCard(Modifier.fillMaxWidth()) {
                    SwitchRow("Title overlay", options.addTitleOverlay) { options = options.copy(addTitleOverlay = it) }
                    SwitchRow("Emoji + hashtags", options.addEmojiHashtags) { options = options.copy(addEmojiHashtags = it) }
                    SwitchRow("Overlap clips", options.overlapClips) { options = options.copy(overlapClips = it) }
                }

                Spacer(Modifier.height(100.dp))
            }

            Box(Modifier.fillMaxWidth().padding(16.dp)) {
                GradientButton("Generate Clips", Modifier.fillMaxWidth(), onClick = onStart)
            }
        }
    }
}

@Composable
private fun FlowRowChips(content: @Composable () -> Unit) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) { content() }
}

@Composable
private fun SwitchRow(label: String, checked: Boolean, onCheck: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color.White, fontSize = 14.sp)
        Switch(checked = checked, onCheckedChange = onCheck)
    }
}
