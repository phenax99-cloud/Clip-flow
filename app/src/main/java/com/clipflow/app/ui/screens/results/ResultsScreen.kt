package com.clipflow.app.ui.screens.results

import androidx.compose.animation.core.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clipflow.app.ui.components.AnimatedBackground
import com.clipflow.app.ui.components.GlassCard
import com.clipflow.app.ui.components.GradientButton
import com.clipflow.app.ui.theme.FlowCyan
import com.clipflow.app.ui.theme.FlowPurple

@Composable
fun ResultsScreen(onHome: () -> Unit) {
    AnimatedBackground {
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            Text("Your Clips", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))

            LazyColumn(
                Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items((1..3).toList()) { i ->
                    val scale by animateFloatAsState(
                        targetValue = 1f,
                        animationSpec = spring(),
                        label = "cardScale"
                    )
                    GlassCard(Modifier.fillMaxWidth()) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .background(
                                    Brush.linearGradient(listOf(FlowPurple.copy(alpha = 0.4f), FlowCyan.copy(alpha = 0.4f))),
                                    RoundedCornerShape(16.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("▶", color = Color.White, fontSize = 42.sp)
                        }
                        Spacer(Modifier.height(10.dp))
                        Text("Clip #$i", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Highlight · 30s · 9:16", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            GradientButton("Back to Home", Modifier.fillMaxWidth(), onClick = onHome)
        }
    }
}
