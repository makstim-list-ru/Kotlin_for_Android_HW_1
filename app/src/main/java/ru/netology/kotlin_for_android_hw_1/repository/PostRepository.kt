package ru.netology.kotlin_for_android_hw_1.repository

import androidx.lifecycle.LiveData
import ru.netology.kotlin_for_android_hw_1.dto.Post

interface PostRepository {
    fun getPost(): LiveData<Post>
    fun like()
    fun share()
}