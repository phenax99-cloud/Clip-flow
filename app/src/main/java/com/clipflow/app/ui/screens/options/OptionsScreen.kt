package com.clipflow.app.ui.screens.options

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clipflow.app.data.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionsScreen(onBack: () -> Unit, onStart: () -> Unit) {
    var options by remember { mutableStateOf(ClipOptions()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Clip Options") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Surface {
                Button(
                    onClick = onStart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text("Generate Clips", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Section("Highlight Detection") {
                HighlightMethod.entries.forEach { method ->
                    val selected = method in options.highlightMethods
                    FilterChip(
                        selected = selected,
                        onClick = {
                            val s = options.highlightMethods.toMutableSet()
                            if (selected) s.remove(method) else s.add(method)
                            options = options.copy(highlightMethods = s)
                        },
                        label = { Text(method.name.replace("_", " ")) }
                    )
                }
            }

            Section("Clip Length") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ClipLength.entries.forEach { len ->
                        FilterChip(
                            selected = options.clipLength == len,
                            onClick = { options = options.copy(clipLength = len) },
                            label = { Text(len.label) }
                        )
                    }
                }
            }

            Section("Number of Clips") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(3, 5, 10).forEach { count ->
                        FilterChip(
                            selected = options.clipCount == count,
                            onClick = { options = options.copy(clipCount = count) },
                            label = { Text("$count") }
                        )
                    }
                }
            }

            Section("Reframe") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ReframeMode.entries.forEach { mode ->
                        FilterChip(
                            selected = options.reframeMode == mode,
                            onClick = { options = options.copy(reframeMode = mode) },
                            label = { Text(mode.name.replace("_", " ")) }
                        )
                    }
                }
            }

            Section("Captions") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CaptionStyle.entries.forEach { style ->
                        FilterChip(
                            selected = options.captionStyle == style,
                            onClick = { options = options.copy(captionStyle = style) },
                            label = { Text(style.name) }
                        )
                    }
                }
            }

            Section("Engine") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Engine.entries.forEach { engine ->
                        FilterChip(
                            selected = options.engine == engine,
                            onClick = { options = options.copy(engine = engine) },
                            label = { Text(engine.name.replace("_", " ")) }
                        )
                    }
                }
            }

            SwitchRow("Title overlay", options.addTitleOverlay) {
                options = options.copy(addTitleOverlay = it)
            }
            SwitchRow("Emoji + hashtags", options.addEmojiHashtags) {
                options = options.copy(addEmojiHashtags = it)
            }
            SwitchRow("Overlap clips", options.overlapClips) {
                options = options.copy(overlapClips = it)
            }
        }
    }
}

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        content()
    }
}

@Composable
private fun SwitchRow(label: String, checked: Boolean, onCheck: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 15.sp)
        Switch(checked = checked, onCheckedChange = onCheck)
    }
}
