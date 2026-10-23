package com.clipflow.app.ui.screens.results

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(onHome: () -> Unit) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Your Clips") }) },
        bottomBar = {
            Surface {
                Button(
                    onClick = onHome,
                    modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp),
                    shape = MaterialTheme.shapes.large
                ) { Text("Back to Home", fontWeight = FontWeight.Bold) }
            }
        }
    ) { pad ->
        LazyColumn(
            modifier = Modifier.padding(pad).fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items((1..3).toList()) { i ->
                Card(
                    modifier = Modifier.fillMaxWidth().height(160.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Clip #$i", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(Modifier.height(4.dp))
                        Text("Highlight · 30s · 9:16", fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
