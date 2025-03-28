package ru.netology.kotlin_for_android_hw_1.repository

import androidx.lifecycle.LiveData
import ru.netology.kotlin_for_android_hw_1.dto.Post
import ru.netology.kotlin_for_android_hw_1.media.MediaID
import ru.netology.kotlin_for_android_hw_1.media.PhotoModel
import ru.netology.kotlin_for_android_hw_1.model.FeedModel
import java.io.File

interface PostRepositorySuspend {
    suspend fun likeByID(id: Long)
    suspend fun shareByID(id: Long)
    suspend fun removeByID(id: Long)
//    suspend fun save(post: Post)
    suspend fun save(post: Post, file: File? = null)
    suspend fun edit(post: Post)
    suspend fun getPostsAllAsync()
    suspend fun loadNewer()
    suspend fun upload(file: File): MediaID?
    fun getServStat(): LiveData<FeedModel>
    fun getData(): LiveData<List<Post>>
    fun getNewerCount(): LiveData<Int>
    fun getPhoto(): LiveData<PhotoModel?>
}