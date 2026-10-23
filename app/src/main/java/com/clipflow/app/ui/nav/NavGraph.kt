package com.clipflow.app.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.clipflow.app.ui.screens.home.HomeScreen
import com.clipflow.app.ui.screens.options.OptionsScreen
import com.clipflow.app.ui.screens.progress.ProgressScreen
import com.clipflow.app.ui.screens.results.ResultsScreen

object Routes {
    const val HOME = "home"
    const val OPTIONS = "options"
    const val PROGRESS = "progress"
    const val RESULTS = "results"
}

@Composable
fun NavGraph() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(onNext = { nav.navigate(Routes.OPTIONS) })
        }
        composable(Routes.OPTIONS) {
            OptionsScreen(
                onBack = { nav.popBackStack() },
                onStart = { nav.navigate(Routes.PROGRESS) }
            )
        }
        composable(Routes.PROGRESS) {
            ProgressScreen(onDone = {
                nav.navigate(Routes.RESULTS) {
                    popUpTo(Routes.HOME)
                }
            })
        }
        composable(Routes.RESULTS) {
            ResultsScreen(onHome = {
                nav.navigate(Routes.HOME) { popUpTo(Routes.HOME) { inclusive = true } }
            })
        }
    }
}
