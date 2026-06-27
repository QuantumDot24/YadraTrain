package com.complexsoft.yadratrain.data

enum class EnginePreset(val ordinal_: Int, val displayName: String,val epochs:Int) {
    MNIST         (0, "MLP · MNIST",10),
    FASHION_MNIST (1, "CNN · Fashion-MNIST",5),
    CIFAR10       (2, "CNN · CIFAR-10",23),
    CONV_AE       (3, "Convolutional Autoencoder",10);

    val isClassifier get() = this != CONV_AE
    val isAutoencoder get() = this == CONV_AE

    companion object {
        fun fromKey(key: String) = entries.first { it.name == key }
    }
}