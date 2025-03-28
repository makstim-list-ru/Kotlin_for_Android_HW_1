package ru.netology.kotlin_for_android_hw_1.model

data class FeedModel(
    val loading: Boolean = false,
    val error: Boolean = false,
    val empty: Boolean = false,
    val refreshing: Boolean = false,
)
