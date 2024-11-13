package ru.netology.kotlin_for_android_hw_1.dto

data class Post(
    val id: Long = 0,
    val author: String = "",
    val authorAvatar: String = "",
    val published: String = "",
    val content: String = "",
    var likedByMe: Boolean = false,
    var likesNum: Long =999,
    var sharesNum: Long = 99,
    var seenNum: Long = 9_999

)
