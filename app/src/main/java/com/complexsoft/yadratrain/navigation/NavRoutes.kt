package com.complexsoft.yadratrain.navigation

object NavRoutes {
    const val SPLASH         = "splash"
    const val SELECT_ENGINE  = "select_engine"
    const val TRAINING       = "training/{preset}"
    const val RESULTS        = "results/{preset}/{resultsEncoded}"
    const val RESULTS_AE     = "results_ae/{resultsEncoded}"
    const val SUMMARY        = "summary/{preset}/{correct}/{total}/{finalAccuracy}"
}