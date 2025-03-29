package ru.netology.kotlin_for_android_hw_1.repository

import android.net.Uri
import androidx.lifecycle.LiveData
import ru.netology.kotlin_for_android_hw_1.dto.Post
import ru.netology.kotlin_for_android_hw_1.media.MediaUploadResponse
import ru.netology.kotlin_for_android_hw_1.media.PhotoModel
import ru.netology.kotlin_for_android_hw_1.model.FeedModel
import java.io.File

interface PostRepositorySuspend {
    suspend fun likeByID(id: Long)
    suspend fun shareByID(id: Long)
    suspend fun removeByID(id: Long)
//    suspend fun save(post: Post)
    suspend fun save(post: Post)
    suspend fun edit(post: Post)
    suspend fun getPostsAllAsync()
    suspend fun loadNewer()
    suspend fun upload(file: File): MediaUploadResponse?
    suspend fun changePhoto(uri: Uri?, file: File?)
    fun removePhoto()
    fun getServStat(): LiveData<FeedModel>
    fun getData(): LiveData<List<Post>>
    fun getNewerCount(): LiveData<Int>
    fun getPhoto(): LiveData<PhotoModel?>
}