package ru.netology.kotlin_for_android_hw_1.repository

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import ru.netology.kotlin_for_android_hw_1.dto.Media
import ru.netology.kotlin_for_android_hw_1.dto.MediaUpload
import ru.netology.kotlin_for_android_hw_1.dto.Post
import ru.netology.kotlin_for_android_hw_1.model.FeedModel
import ru.netology.kotlin_for_android_hw_1.model.PhotoModel

interface PostRepositorySuspend {
    //    fun getPostsAll(): LiveData<List<Post>>
    suspend fun likeByID(id: Long)
    suspend fun shareByID(id: Long)
    suspend fun removeByID(id: Long)
    suspend fun save(post: Post)
//    suspend fun save(post: Post, uploadMedia: MediaUpload)
    suspend fun edit(post: Post)
    suspend fun getPostsAllAsync()
    suspend fun loadNewer()
//    suspend fun upload(upload: MediaUpload): Media?
    fun getServStat(): LiveData<FeedModel>
    fun getData(): LiveData<List<Post>>
    fun getNewerCount(): LiveData<Int>
    fun getPhoto(): LiveData<PhotoModel?>

}