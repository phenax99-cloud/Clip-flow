package com.clipflow.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.clipflow.app.ui.nav.NavGraph
import com.clipflow.app.ui.theme.ClipFlowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClipFlowTheme {
                NavGraph()
            }
        }
    }
}
