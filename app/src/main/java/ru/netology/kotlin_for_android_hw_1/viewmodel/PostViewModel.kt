package ru.netology.kotlin_for_android_hw_1.viewmodel

import androidx.lifecycle.ViewModel
import ru.netology.kotlin_for_android_hw_1.repository.PostRepositoryInMemory

class PostViewModel : ViewModel() {

    private val repository = PostRepositoryInMemory()

    val data = repository.getPostsAll()

    fun likeVM(id:Long) {
        repository.likeByID(id)
    }

    fun shareVM(id:Long) {
        repository.shareByID(id)
    }
}