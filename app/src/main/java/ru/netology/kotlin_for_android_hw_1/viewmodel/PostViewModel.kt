package ru.netology.kotlin_for_android_hw_1.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.kotlin_for_android_hw_1.dto.Post
import ru.netology.kotlin_for_android_hw_1.dto.postEmpty
import ru.netology.kotlin_for_android_hw_1.model.FeedModel
import ru.netology.kotlin_for_android_hw_1.repository.PostRepositoryInServer
import ru.netology.kotlin_for_android_hw_1.repository.PostRepositoryInServerWithRetrofit

class PostViewModel(application: Application) : AndroidViewModel(application) {

//    private val repository = PostRepositoryInMemory()
//    private val repository = PostRepositoryInFile(application)
//    private val repository = PostRepositoryInSQL(application)
//    private val repository = PostRepositoryInSQLwithRoom(application)
//    private val repository = PostRepositoryInServer(application)

    private val repository = PostRepositoryInServerWithRetrofit(application)

    val dataServerStatus: LiveData<FeedModel> = repository.getServerStatus()

    //    val data = repository.getPostsAll()
//    val data = repository.getPostsAllAsync()
    val data = repository.getData()

    init {
        repository.getPostsAllAsync()
    }


    private val editedPostTmp = MutableLiveData(postEmpty)


    fun likeViewModel(id: Long) {
        repository.likeByID(id)
    }

    fun shareViewModel(id: Long) {
        repository.shareByID(id)
    }

    fun removeViewModel(id: Long) {
        repository.removeByID(id)
    }

    fun saveViewModel(content: String) {
//        val editedPost = editedPostTmp.value?.copy()!!
        val editedPost = requireNotNull(editedPostTmp.value) {
            println("ERROR_VIEW_MODEL in fun <saveViewModel>, developer's attention is required")
        }

        if (editedPost.id == 0L) {
            repository.save(Post(content = content))
        } else {
            repository.edit(editedPost.copy(content = content))
            editedPostTmp.value = postEmpty
        }
    }

    fun editViewModel(post: Post) {
        editedPostTmp.value = post
    }

    fun cancelViewModel() {
        editedPostTmp.value = postEmpty
    }

    fun loadAllPostsViewModel() {
        repository.getPostsAllAsync()
    }
}