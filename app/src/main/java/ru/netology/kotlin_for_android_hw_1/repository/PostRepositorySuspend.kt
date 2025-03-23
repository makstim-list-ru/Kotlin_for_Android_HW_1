package ru.netology.kotlin_for_android_hw_1.repository

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import ru.netology.kotlin_for_android_hw_1.dto.Post
import ru.netology.kotlin_for_android_hw_1.model.FeedModel

interface PostRepositorySuspend {
    //    fun getPostsAll(): LiveData<List<Post>>
    suspend fun likeByID(id: Long)
    suspend fun shareByID(id: Long)
    suspend fun removeByID(id: Long)
    suspend fun save(post: Post)
    suspend fun edit(post: Post)
    suspend fun getPostsAllAsync(): Flow<List<Post>>
    suspend fun loadNewer()
    fun getServStat(): LiveData<FeedModel>
    fun getData(): LiveData<List<Post>>
    fun getNewerCount(): LiveData<Int>
}