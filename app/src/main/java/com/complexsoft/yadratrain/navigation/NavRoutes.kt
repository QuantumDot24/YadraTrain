package com.complexsoft.yadratrain.navigation

object NavRoutes {
    const val SPLASH = "splash"
    const val SELECT_ENGINE = "select_engine"
    const val TRAINING = "training/{preset}"  // preset = "MNIST" o "CIFAR10"
    const val RESULTS = "results/{preset}"     // después de entrenar
    const val SUMMARY = "summary/{preset}/{correct}/{total}"
}