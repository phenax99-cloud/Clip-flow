package com.clipflow.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clipflow.app.data.model.ClipOptions
import com.clipflow.app.ui.theme.FlowCyan
import com.clipflow.app.ui.theme.FlowPurple

@Composable
fun SummaryCard(options: ClipOptions, modifier: Modifier = Modifier) {
    val parts = buildList {
        add("${options.clipCount} clips")
        add(options.clipLength.label)
        add("9:16")
        if (options.captionStyle.name != "NONE") add("captions")
        add(options.engine.name.replace("_", " ").lowercase())
    }
    Row(
        modifier = modifier
            .background(Brush.horizontalGradient(listOf(FlowPurple.copy(alpha = 0.35f), FlowCyan.copy(alpha = 0.35f))), RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(parts.joinToString(" · "), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}
