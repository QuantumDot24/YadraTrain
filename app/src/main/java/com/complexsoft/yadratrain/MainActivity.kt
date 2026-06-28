package com.complexsoft.yadratrain

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.complexsoft.yadratrain.data.EnginePreset
import com.complexsoft.yadratrain.navigation.NavRoutes
import com.complexsoft.yadratrain.ui.screens.*
import com.complexsoft.yadratrain.ui.theme.YadraBg
import com.complexsoft.yadratrain.ui.theme.YadraTrainTheme
import com.complexsoft.yadratrain.ui.viewmodel.TrainingViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        enableEdgeToEdge()
        setContent {
            YadraTrainTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = YadraBg
                ) { innerPadding ->
                    YadraNavGraph(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .consumeWindowInsets(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
private fun YadraNavGraph(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    // ViewModel único compartido por toda la sesión de entrenamiento
    val trainingViewModel: TrainingViewModel = viewModel()

    NavHost(
        navController  = navController,
        startDestination = NavRoutes.SPLASH,
        modifier       = modifier
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
                navController.navigate("training/${preset.name}")
            })
        }

        composable(
            route = "training/{preset}",
            arguments = listOf(navArgument("preset") { type = NavType.StringType })
        ) { back ->
            val preset = EnginePreset.fromKey(back.arguments?.getString("preset") ?: "MNIST")
            TrainingScreen(
                preset      = preset,
                viewModel   = trainingViewModel,
                onTrainingFinished = { _, _, _, _ ->
                    if (preset.isAutoencoder)
                        navController.navigate("results_ae")
                    else
                        navController.navigate("results/${preset.name}")
                }
            )
        }

        // Resultados clasificadores
        composable(
            route = "results/{preset}",
            arguments = listOf(navArgument("preset") { type = NavType.StringType })
        ) { back ->
            val preset = EnginePreset.fromKey(back.arguments?.getString("preset") ?: "MNIST")
            ResultsScreen(
                preset    = preset,
                viewModel = trainingViewModel,
                onNavigateToSummary = { correct, total, acc ->
                    navController.navigate("summary/${preset.name}/$correct/$total/$acc")
                }
            )
        }

        // Resultados autoencoder
        composable("results_ae") {
            AutoencoderResultsScreen(
                viewModel = trainingViewModel,
                onNavigateToSummary = {
                    val state = trainingViewModel.state.value
                    navController.navigate(
                        "summary/${EnginePreset.CONV_AE.name}/0/8/${state.loss}"
                    )
                }
            )
        }

        composable(
            route = "summary/{preset}/{correct}/{total}/{finalAccuracy}",
            arguments = listOf(
                navArgument("preset")        { type = NavType.StringType  },
                navArgument("correct")       { type = NavType.IntType     },
                navArgument("total")         { type = NavType.IntType     },
                navArgument("finalAccuracy") { type = NavType.FloatType   }
            )
        ) { back ->
            val preset = EnginePreset.fromKey(back.arguments?.getString("preset") ?: "MNIST")
            SummaryScreen(
                preset        = preset,
                correct       = back.arguments?.getInt("correct")       ?: 0,
                total         = back.arguments?.getInt("total")         ?: 0,
                finalAccuracy = back.arguments?.getFloat("finalAccuracy") ?: -1f,
                viewModel     = trainingViewModel
            )
        }
    }
}