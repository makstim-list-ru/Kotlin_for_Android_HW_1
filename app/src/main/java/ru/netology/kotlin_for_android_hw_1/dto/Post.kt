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
    val seenNum: Long = 9_999,
    val video: String = ""

)

val postEmpty = Post(
    id = 0,
    author = "",
    authorAvatar = "",
    published = "",
    content = "",
    likedByMe = false,
    likesNum = 0,
    sharesNum = 0,
    seenNum = 0,
    video = ""
)


