package ru.netology.kotlin_for_android_hw_1.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.netology.kotlin_for_android_hw_1.dto.Post
import ru.netology.kotlin_for_android_hw_1.dto.postEmpty
import ru.netology.kotlin_for_android_hw_1.repository.PostRepositoryInMemory

class PostViewModel : ViewModel() {

    private val repository = PostRepositoryInMemory()

    val data = repository.getPostsAll()
    val editedPostLD = MutableLiveData(postEmpty)

    fun likeVM(id: Long) {
        repository.likeByID(id)
    }

    fun shareVM(id: Long) {
        repository.shareByID(id)
    }

    fun removeVM(id: Long) {
        repository.removeByID(id)
    }

    fun saveVM(content: String) {
        val editedPost = editedPostLD.value?.copy()!!
        if (editedPost.id == 0L) {
            repository.save(Post(content = content))
        } else {
            repository.edit(editedPost.copy(content = content))
            editedPostLD.value= postEmpty
        }
    }

    fun editVM(post: Post) {
        editedPostLD.value = post
    }

    fun cancelVM() {
        editedPostLD.value = postEmpty
    }
}