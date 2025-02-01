package ru.netology.kotlin_for_android_hw_1.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.kotlin_for_android_hw_1.dto.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
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
) {
    fun toPostFromEntity() = Post(
        id,
        author,
        authorAvatar,
        published,
        content,
        likedByMe,
        likesNum,
        sharesNum,
        seenNum,
        video
    )

    companion object {
        fun fromPostToEntity(post: Post) = PostEntity(
            post.id,
            post.author,
            post.authorAvatar,
            post.published,
            post.content,
            post.likedByMe,
            post.likesNum,
            post.sharesNum,
            post.seenNum,
            post.video
        )
    }
}