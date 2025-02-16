package ru.netology.kotlin_for_android_hw_1.repository

import androidx.lifecycle.LiveData
import ru.netology.kotlin_for_android_hw_1.dto.Post

interface PostRepository {
    fun getPostsAll(): LiveData<List<Post>>
    fun likeByID(id: Long)
    fun shareByID(id: Long)
    fun removeByID(id: Long)
    fun save(post: Post)
    fun edit (post: Post)

}