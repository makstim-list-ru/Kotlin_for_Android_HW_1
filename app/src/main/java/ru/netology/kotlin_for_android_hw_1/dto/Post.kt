package ru.netology.kotlin_for_android_hw_1.dto

data class Post(
    val id: Long = 0,
    val author: String = "",
    val authorAvatar: String = "",
    val published: String = "",
    val content: String = "",
    val likedByMe: Boolean = false,
    val likesNum: Long = 999,
    val sharesNum: Long = 99,
    val seenNum: Long = 9_999

)
