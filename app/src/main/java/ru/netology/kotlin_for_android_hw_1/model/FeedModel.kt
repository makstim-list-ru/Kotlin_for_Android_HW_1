package ru.netology.kotlin_for_android_hw_1.model

import ru.netology.kotlin_for_android_hw_1.dto.Post

data class FeedModel(
    val posts: List<Post> = emptyList(),
    val loading: Boolean = false,
    val error: Boolean = false,
    val empty: Boolean = false,
    val refreshing: Boolean = false,
)
