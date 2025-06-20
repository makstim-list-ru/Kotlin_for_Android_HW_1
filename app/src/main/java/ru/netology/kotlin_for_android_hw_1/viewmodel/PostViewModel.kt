package ru.netology.kotlin_for_android_hw_1.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.kotlin_for_android_hw_1.dto.Post
import ru.netology.kotlin_for_android_hw_1.dto.postEmpty
import ru.netology.kotlin_for_android_hw_1.media.PhotoModel
import ru.netology.kotlin_for_android_hw_1.model.FeedModel
import ru.netology.kotlin_for_android_hw_1.repository.PostRepositoryInServerAndSQL
import ru.netology.kotlin_for_android_hw_1.repository.PostRepositorySuspend
import java.io.File

class PostViewModel(application: Application) : AndroidViewModel(application) {

//    private val repository = PostRepositoryInMemory()
//    private val repository = PostRepositoryInFile(application)
//    private val repository = PostRepositoryInSQL(application)
//    private val repository = PostRepositoryInSQLwithRoom(application)
//    private val repository = PostRepositoryInServer(application)
//    private val repository = PostRepositoryInServerWithRetrofit(application)

    private val repository: PostRepositorySuspend = PostRepositoryInServerAndSQL(application)
    private val _photoLive = MutableLiveData<PhotoModel?>(null)

    val photoLive: LiveData<PhotoModel?>
        get() = _photoLive
    val dataServerStatus: LiveData<FeedModel> = repository.getServStat()
    val data: LiveData<List<Post>> = repository.getData()
    val newerCount: LiveData<Int> = repository.getNewerCount()


    init {
        viewModelScope.launch {
            repository.getPostsAllAsync()
        }
    }

    private val editedPostTmp = MutableLiveData(postEmpty)

    fun likeVM(id: Long) {
        viewModelScope.launch { repository.likeByID(id) }
    }

    fun shareVM(id: Long) {
        viewModelScope.launch { repository.shareByID(id) }
    }

    fun removeVM(id: Long) {
        viewModelScope.launch { repository.removeByID(id) }
    }

    fun saveVM(content: String) {
//        val editedPost = editedPostTmp.value?.copy()!!
        val editedPost = requireNotNull(editedPostTmp.value) { println("ERROR in <saveViewModel>") }

        if (editedPost.id == 0L) { //SAVE NEW
            viewModelScope.launch { repository.save(Post(content = content), photoLive.value?.file) }
        } else { //EDIT
            viewModelScope.launch { repository.edit(editedPost.copy(content = content), photoLive.value?.file) }
        }
        cancelEditVM()
        removePhotoVM()
    }

    fun editVM(post: Post) {
        editedPostTmp.value = post
    }

    fun cancelEditVM() {
        editedPostTmp.value = postEmpty
    }

    fun loadAllPostsVM() {
        viewModelScope.launch { repository.getPostsAllAsync() }
    }

    fun loadNewerVM() {
        viewModelScope.launch { repository.loadNewer() }
    }

    fun changePhotoVM(uri: Uri?, file: File?) {
        _photoLive.value = PhotoModel(uri, file)
    }

    fun removePhotoVM() {
        _photoLive.value = null
    }
}