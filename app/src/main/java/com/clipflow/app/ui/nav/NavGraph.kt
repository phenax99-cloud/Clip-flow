package com.clipflow.app.ui.nav

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.clipflow.app.ui.screens.home.HomeScreen
import com.clipflow.app.ui.screens.options.OptionsScreen
import com.clipflow.app.ui.screens.progress.ProgressScreen
import com.clipflow.app.ui.screens.results.ResultsScreen
import com.clipflow.app.ui.screens.settings.SettingsScreen

object Routes {
    const val HOME = "home"
    const val SETTINGS = "settings"
    const val OPTIONS = "options?uri={uri}"
    const val PROGRESS = "progress?uri={uri}"
    const val RESULTS = "results"
    fun options(uri: Uri) = "options?uri=${Uri.encode(uri.toString())}"
    fun progress(uri: Uri) = "progress?uri=${Uri.encode(uri.toString())}"
}

@Composable
fun NavGraph() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                onVideoPicked = { uri -> nav.navigate(Routes.options(uri)) },
                onSettings = { nav.navigate(Routes.SETTINGS) }
            )
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(onBack = { nav.popBackStack() })
        }
        composable(Routes.OPTIONS, arguments = listOf(navArgument("uri") { defaultValue = "" })) { entry ->
            val uri = entry.arguments?.getString("uri").orEmpty()
                .let { if (it.isEmpty()) null else Uri.parse(it) }
            OptionsScreen(
                onBack = { nav.popBackStack() },
                onStart = { if (uri != null) nav.navigate(Routes.progress(uri)) }
            )
        }
        composable(Routes.PROGRESS, arguments = listOf(navArgument("uri") { defaultValue = "" })) { entry ->
            val uri = entry.arguments?.getString("uri").orEmpty()
                .let { if (it.isEmpty()) null else Uri.parse(it) }
            ProgressScreen(
                videoUri = uri,
                onDone = { nav.navigate(Routes.RESULTS) { popUpTo(Routes.HOME) } }
            )
        }
        composable(Routes.RESULTS) {
            ResultsScreen(onHome = {
                nav.navigate(Routes.HOME) { popUpTo(Routes.HOME) { inclusive = true } }
            })
        }
    }
}
