package com.clipflow.app.ui.screens.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clipflow.app.ui.components.*
import com.clipflow.app.ui.theme.FlowCyan
import com.clipflow.app.ui.theme.FlowPink
import com.clipflow.app.ui.theme.FlowPurple

@Composable
fun HomeScreen(onNext: () -> Unit) {
    var url by remember { mutableStateOf("") }
    val infinite = rememberInfiniteTransition(label = "logo")
    val pulse by infinite.animateFloat(1f, 1.04f, infiniteRepeatable(tween(2200, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "pulse")

    AnimatedBackground {
        Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "Clip Flow",
                fontSize = 48.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.scale(pulse),
                style = TextStyle(brush = Brush.horizontalGradient(listOf(FlowPurple, FlowCyan, FlowPink)))
            )
            Spacer(Modifier.height(6.dp))
            Text("Long videos in. Shorts out.", color = Color.White.copy(alpha = 0.75f), fontSize = 15.sp)

            Spacer(Modifier.height(48.dp))

            GlassCard(Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("Paste YouTube URL", color = Color.White.copy(alpha = 0.7f)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = FlowCyan,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
                    )
                )
            }

            Spacer(Modifier.height(20.dp))

            GradientButton("Continue", Modifier.fillMaxWidth(), onClick = onNext)

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text("Pick from Gallery", fontWeight = FontWeight.SemiBold, color = Color.White)
            }
        }
    }
}
