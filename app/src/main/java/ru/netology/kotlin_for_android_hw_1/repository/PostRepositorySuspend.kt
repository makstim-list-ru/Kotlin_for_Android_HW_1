package ru.netology.kotlin_for_android_hw_1.repository

import androidx.lifecycle.LiveData
import ru.netology.kotlin_for_android_hw_1.dto.Post

interface PostRepositorySuspend {
//    fun getPostsAll(): LiveData<List<Post>>
    suspend fun likeByID(id: Long)
    suspend fun shareByID(id: Long)
    suspend fun removeByID(id: Long)
    suspend fun save(post: Post)
    suspend fun edit(post: Post)

}