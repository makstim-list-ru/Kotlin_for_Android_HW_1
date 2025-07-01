package ru.netology.kotlin_for_android_hw_1.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.kotlin_for_android_hw_1.dto.FeedItem
import ru.netology.kotlin_for_android_hw_1.dto.Post
import ru.netology.kotlin_for_android_hw_1.media.MediaUploadResponse
import ru.netology.kotlin_for_android_hw_1.model.FeedModel
import java.io.File

interface PostRepositorySuspend {
    suspend fun likeByID(id: Long)
    suspend fun shareByID(id: Long)
    suspend fun removeByID(id: Long)
    suspend fun save(post: Post, uploadFile: File? = null)
    suspend fun edit(post: Post, uploadFile: File? = null)
    suspend fun getPostsAllAsync()
    suspend fun loadNewer()
    suspend fun upload(file: File): MediaUploadResponse?
    fun getServStat(): LiveData<FeedModel>
    fun getData(): LiveData<PagingData<FeedItem>>
    fun getDataFlow(): Flow<PagingData<FeedItem>>
    fun getNewerCount(): LiveData<Int>
}