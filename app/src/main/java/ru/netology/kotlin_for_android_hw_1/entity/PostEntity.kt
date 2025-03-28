package ru.netology.kotlin_for_android_hw_1.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.kotlin_for_android_hw_1.dto.Attachment
import ru.netology.kotlin_for_android_hw_1.dto.Post
import ru.netology.kotlin_for_android_hw_1.enumeration.AttachmentType

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
    val video: String = "",
    @Embedded
    var attachment: AttachmentEmbeddable? = null,
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
        video,
        attachment?.toPostFromEntity(),
    )

    companion object {
        fun fromPostToEntity(post: Post) = PostEntity(
            post.id,
            post.author,
            post.authorAvatar,
            post.published,
            post.content,
            post.likedByMe,
            post.likes,
            post.sharesNum,
            post.seenNum,
            post.video,
            AttachmentEmbeddable.fromPostToEntity(post.attachment)
        )
    }
}

data class AttachmentEmbeddable(
    var url: String,
    var type: AttachmentType,
) {
    fun toPostFromEntity() = Attachment(url, type)

    companion object {
        fun fromPostToEntity(postAtt: Attachment?) = postAtt?.let {
            AttachmentEmbeddable(it.url, it.type)
        }
    }
}