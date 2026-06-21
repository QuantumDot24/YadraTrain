package com.complexsoft.yadratrain

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.complexsoft.yadratrain.navigation.NavRoutes
import com.complexsoft.yadratrain.ui.screens.ResultsScreen
import com.complexsoft.yadratrain.ui.screens.SelectEngineScreen
import com.complexsoft.yadratrain.ui.screens.SplashScreen
import com.complexsoft.yadratrain.ui.screens.SummaryScreen
import com.complexsoft.yadratrain.ui.screens.TrainingScreen
import com.complexsoft.yadratrain.ui.theme.YadraBg
import com.complexsoft.yadratrain.ui.theme.YadraTrainTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YadraTrainTheme {
                // Scaffold consume los insets de status/navigation bar UNA SOLA VEZ
                // y los reparte como innerPadding a todo lo de adentro.
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = YadraBg
                ) { innerPadding ->
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = NavRoutes.SPLASH,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            // evita que un hijo vuelva a "comerse" los insets dos veces
                            .consumeWindowInsets(innerPadding)
                    ) {
                        composable(NavRoutes.SPLASH) {
                            SplashScreen(onSplashFinished = {
                                navController.navigate(NavRoutes.SELECT_ENGINE) {
                                    popUpTo(NavRoutes.SPLASH) { inclusive = true }
                                }
                            })
                        }
                        composable(NavRoutes.SELECT_ENGINE) {
                            SelectEngineScreen(onEngineSelected = { preset ->
                                navController.navigate("training/$preset")
                            })
                        }
                        composable("training/{preset}") { backStackEntry ->
                            val preset = backStackEntry.arguments?.getString("preset") ?: "MNIST"
                            TrainingScreen(
                                preset = preset,
                                onTrainingFinished = { resultsString, correct, total, finalAccuracy ->
                                    val encodedResults = java.net.URLEncoder.encode(resultsString, "UTF-8")
                                    navController.navigate("results/$preset/$encodedResults/$correct/$total/$finalAccuracy") {
                                        popUpTo("select_engine") { inclusive = false }
                                    }
                                }
                            )
                        }
                        composable("results/{preset}/{resultsString}/{correct}/{total}/{finalAccuracy}") { backStackEntry ->
                            val preset = backStackEntry.arguments?.getString("preset") ?: "MNIST"
                            val rawResultsString = backStackEntry.arguments?.getString("resultsString") ?: ""
                            val resultsString = java.net.URLDecoder.decode(rawResultsString, "UTF-8")
                            val correct = backStackEntry.arguments?.getString("correct")?.toIntOrNull() ?: 0
                            val total = backStackEntry.arguments?.getString("total")?.toIntOrNull() ?: 10
                            val finalAccuracy = backStackEntry.arguments?.getString("finalAccuracy")?.toFloatOrNull() ?: -1f
                            ResultsScreen(
                                preset = preset,
                                resultsString = resultsString,
                                correct = correct,
                                total = total,
                                finalAccuracy = finalAccuracy,
                                onNavigateToSummary = { correct, total, finalAccuracy ->
                                    navController.navigate("summary/$preset/$correct/$total/$finalAccuracy") {
                                        popUpTo("select_engine") { inclusive = false }
                                    }
                                }
                            )
                        }
                        composable("summary/{preset}/{correct}/{total}/{finalAccuracy}") { backStackEntry ->
                            val preset = backStackEntry.arguments?.getString("preset") ?: "MNIST"
                            val correct = backStackEntry.arguments?.getString("correct")?.toIntOrNull() ?: 0
                            val total = backStackEntry.arguments?.getString("total")?.toIntOrNull() ?: 10
                            val finalAccuracy = backStackEntry.arguments?.getString("finalAccuracy")?.toFloatOrNull() ?: -1f
                            SummaryScreen(preset = preset, correct = correct, total = total, finalAccuracy = finalAccuracy)
                        }
                    }
                }
            }
        }
    }
}