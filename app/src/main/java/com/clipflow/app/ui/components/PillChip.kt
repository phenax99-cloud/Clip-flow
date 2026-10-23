package com.clipflow.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clipflow.app.ui.theme.FlowCyan
import com.clipflow.app.ui.theme.FlowPurple

@Composable
fun PillChip(
    label: String,
    selected: Boolean,
    comingSoon: Boolean = false,
    onClick: () -> Unit
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.92f else if (selected) 1.04f else 1f, spring(dampingRatio = 0.5f, stiffness = 600f), label = "chipScale")
    val bg = if (selected) {
        Brush.horizontalGradient(listOf(FlowPurple, FlowCyan))
    } else {
        Brush.horizontalGradient(listOf(Color.White.copy(alpha = 0.06f), Color.White.copy(alpha = 0.06f)))
    }

    Row(
        modifier = Modifier
            .scale(scale)
            .background(bg, RoundedCornerShape(50))
            .clickable(interactionSource = interaction, indication = null, enabled = !comingSoon, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (selected) Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(14.dp))
        Text(
            label,
            color = when { comingSoon -> Color.White.copy(alpha = 0.4f); selected -> Color.White; else -> Color.White.copy(alpha = 0.85f) },
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
        if (comingSoon) Text("soon", color = Color.White.copy(alpha = 0.5f), fontSize = 9.sp)
    }
}
