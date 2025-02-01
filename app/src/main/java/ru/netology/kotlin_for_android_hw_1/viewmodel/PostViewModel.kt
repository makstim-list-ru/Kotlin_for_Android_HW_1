package ru.netology.kotlin_for_android_hw_1.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.netology.kotlin_for_android_hw_1.dto.Post
import ru.netology.kotlin_for_android_hw_1.dto.postEmpty
import ru.netology.kotlin_for_android_hw_1.repository.PostRepositoryInFile
import ru.netology.kotlin_for_android_hw_1.repository.PostRepositoryInSQL
import ru.netology.kotlin_for_android_hw_1.repository.PostRepositoryInSQLwithRoom

class PostViewModel(application: Application) : AndroidViewModel(application) {

    //    private val repository = PostRepositoryInMemory()
//    private val repository = PostRepositoryInFile(application)
//    private val repository = PostRepositoryInSQL(application)
    private val repository = PostRepositoryInSQLwithRoom(application)

    val data = repository.getPostsAll()
    private val editedPostLD = MutableLiveData(postEmpty)

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
            editedPostLD.value = postEmpty
        }
    }

    fun editVM(post: Post) {
        editedPostLD.value = post
    }

    fun cancelVM() {
        editedPostLD.value = postEmpty
    }
}