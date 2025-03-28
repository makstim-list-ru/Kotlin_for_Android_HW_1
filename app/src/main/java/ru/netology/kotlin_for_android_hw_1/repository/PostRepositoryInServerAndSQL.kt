package ru.netology.kotlin_for_android_hw_1.repository

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import ru.netology.kotlin_for_android_hw_1.dto.Attachment
import ru.netology.kotlin_for_android_hw_1.dto.Post
import ru.netology.kotlin_for_android_hw_1.entity.PostEntity
import ru.netology.kotlin_for_android_hw_1.media.AttachmentType
import ru.netology.kotlin_for_android_hw_1.media.MediaID
import ru.netology.kotlin_for_android_hw_1.media.PhotoModel
import ru.netology.kotlin_for_android_hw_1.model.FeedModel
import ru.netology.kotlin_for_android_hw_1.retrofit.PostsRetrofitSuspend
import ru.netology.kotlin_for_android_hw_1.roomdb.RoomDBSuspend
import java.io.File

class PostRepositoryInServerAndSQL(context: Context) : PostRepositorySuspend {


    private val servStat = MutableLiveData(FeedModel())
    private val db =
        Room.databaseBuilder(context, RoomDBSuspend::class.java, "database.db")
            .build()
    private val dao = db.getPostDao()
    private val dataFlow = dao.getPostsAll().map { it -> it.map { it.toPostFromEntity() } }
    private val dataLive: LiveData<List<Post>> = dataFlow.asLiveData(Dispatchers.Default)
    private val newerCountLive = dataLive.switchMap {
        getPostsNewer().asLiveData(Dispatchers.Default)
    }
    private val photoLive = MutableLiveData<PhotoModel?>(null)

    @Volatile
    private var flagLoad = false

    override fun getServStat(): LiveData<FeedModel> = servStat
    override fun getData(): LiveData<List<Post>> = dataLive
    override fun getNewerCount() = newerCountLive
    override fun getPhoto(): LiveData<PhotoModel?> = photoLive

    override suspend fun getPostsAllAsync() {
        servStat.value = serverStatus(ServerStatus.LOADING)

        try {
            supervisorScope {
                dao.getUnsaved().forEach { launch { save(it.toPostFromEntity()) } }
            }
        } catch (e: Exception) {
            println("getPostsAllAsync()->dao.getUnsaved().forEach FAULT: $e")
        }

        try {
            val response = PostsRetrofitSuspend.retrofitService.getAll()
            val posts = retrofitErrorHandler(response) ?: return

            dao.deleteAndInsert(posts.map { PostEntity.fromPostToEntity(it) })

            if (posts.isEmpty()) servStat.postValue(serverStatus(ServerStatus.EMPTY))
            else servStat.postValue(serverStatus(ServerStatus.OK))
        } catch (e: Exception) {
            servStat.postValue(serverStatus(ServerStatus.ERROR))
            println("getPostsAllAsync()->PostsRetrofitSuspend.retrofitService.getAll() ERROR: $e")
        }
    }

    override suspend fun shareByID(id: Long) = dao.shareByID(id)

    override suspend fun removeByID(id: Long) {
        dao.removeByID(id)
        try {
            PostsRetrofitSuspend.retrofitService.removeById(id)
        } catch (e: Exception) {
            servStat.postValue(serverStatus(ServerStatus.ERROR))
            println("removeByID(id: Long)->PostsRetrofitSuspend.retrofitService.removeById(id) ERROR: $e")
        }
    }

    override suspend fun edit(post: Post) {
        dao.edit(PostEntity.fromPostToEntity(post))

        try {
            PostsRetrofitSuspend.retrofitService.save(post)
        } catch (e: Exception) {
            servStat.postValue(serverStatus(ServerStatus.ERROR))
            println("edit(post: Post)->PostsRetrofitSuspend.retrofitService.save(post) ERROR: $e")
        }
    }

    override suspend fun save(post: Post, file: File?) {
        if (post.id > 0) throw Exception("ERROR in fun SAVE, calls with zero id or less are allowed only")
        var mediaId: MediaID? = null
        if (file != null && post.id == 0L)
            try {
                mediaId = upload(file) ?: let {
                    println("save(post: Post, file: File)->FAULT upload file failure, next time will be tried")
                    return
                }
            } catch (e: Exception) {
                println("save(post: Post)->PostsRetrofitSuspend.retrofitService.save(myPost) ERROR: $e")
                return
            }

        val tempId =
            if (post.id == 0L) dao.getMinId()?.coerceAtMost(0)?.dec() ?: -1 else post.id

        val tempPost = post.copy(
            id = tempId,
            author = "Me",
            content = post.content,
            authorAvatar = "sber.jpg",
            attachment = mediaId?.let { Attachment(it.id, AttachmentType.IMAGE) }
        )

        if (post.id == 0L) dao.save(    // если сохраняется свежий пост с присвоением нового (-)id в ЛБД
            PostEntity.fromPostToEntity(tempPost)
        )

        try {
            val serverPost = PostsRetrofitSuspend.retrofitService.save(tempPost.copy(id = 0L))
            dao.save(PostEntity.fromPostToEntity(serverPost))
            dao.removeByID(tempId)
        } catch (e: Exception) {
            servStat.postValue(serverStatus(ServerStatus.ERROR))
            println("save(post: Post)->PostsRetrofitSuspend.retrofitService.save(myPost) ERROR: $e")
        }
    }

    override suspend fun likeByID(id: Long) {
        dao.likeByID(id)
        val post = dao.getPostById(id).toPostFromEntity()

        try {
            if (post.likedByMe) PostsRetrofitSuspend.retrofitService.likeById(id)
            else PostsRetrofitSuspend.retrofitService.dislikeById(id)
        } catch (e: Exception) {
            dao.likeByID(id)
            servStat.postValue(serverStatus(ServerStatus.ERROR))
            println("likeByID(id: Long)->PostsRetrofitSuspend.retrofitService.likeById(id) ERROR: $e")
        }
    }

    override suspend fun loadNewer() {
        println("Button <loadNewer> pressed")
        flagLoad = true

        try {
            val response = PostsRetrofitSuspend.retrofitService.getPostsNewer(
                dao.getMaxId() ?: 0L
            )
            if (response.isSuccessful) {
                servStat.postValue(serverStatus(ServerStatus.OK))
                val posts = response.body()
                if (!posts.isNullOrEmpty()) {
                    dao.insert(posts.map { PostEntity.fromPostToEntity(it) })
                } else {
                    println("loadNewer()->!posts.isNullOrEmpty() FAULT: if-else")
                }
            } else {
                servStat.postValue(serverStatus(ServerStatus.ERROR))
                println("loadNewer()->response.isSuccessful ERROR: if-else")
            }
        } catch (e: Exception) {
            servStat.postValue(serverStatus(ServerStatus.ERROR))
            println("loadNewer()->PostsRetrofitSuspend.retrofitService.getPostsNewer ERROR: $e")
        }

        flagLoad = false
    }

    private fun getPostsNewer(): Flow<Int> = flow {
        while (true) {
            delay(10_000)
            val response =
                PostsRetrofitSuspend.retrofitService.getPostsNewer(dao.getMaxId() ?: 0L)
            if (response.isSuccessful) {
                servStat.postValue(serverStatus(ServerStatus.OK))
                val posts = response.body()
                if (!posts.isNullOrEmpty()) {
                    if (flagLoad) {
                        println("flagLoad is ON")
                    } else {
                        emit(posts.size)
                    }
                } else
                    emit(0)
            } else {
                servStat.postValue(serverStatus(ServerStatus.ERROR))
                println("getPostsNewer()->response.isSuccessful ERROR: if-else")
            }
        }
    }.catch {
        servStat.postValue(serverStatus(ServerStatus.ERROR))
        println("getPostsNewer()->catch ERROR: CATCH")
    }


    override suspend fun upload(file: File): MediaID? {
        try {
            val media = MultipartBody.Part.createFormData(
                "file_id", file.name, file.asRequestBody()
            )

            val response = PostsRetrofitSuspend.retrofitService.upload(media)
            if (!response.isSuccessful) {
                println("upload->response.isSuccessful ERROR: if-else")
                return null
            }

            return response.body()
        } catch (e: Exception) {
            println("upload->CATCH ERROR: $e")
            return null
        }
    }

    private fun <T> retrofitErrorHandler(res: Response<T>): T? {
        if (res.isSuccessful) {
            servStat.postValue(serverStatus(ServerStatus.OK))
            return res.body()
        } else {
            servStat.postValue(serverStatus(ServerStatus.ERROR))
            println("retrofitErrorHandler(res: Response<T>)->res.isSuccessful ERROR: if-else")
        }
        return null
    }

    private fun serverStatus(status: ServerStatus): FeedModel {
        return when (status) {
            ServerStatus.LOADING -> FeedModel(loading = true)
            ServerStatus.ERROR -> FeedModel(error = true)
            ServerStatus.EMPTY -> FeedModel(empty = true)
            ServerStatus.REFRESHING -> FeedModel(refreshing = true)
            else -> FeedModel()
        }
    }

    private enum class ServerStatus {
        LOADING, ERROR, EMPTY, REFRESHING, OK
    }

    private fun changePhoto(uri: Uri?, file: File?) {
        photoLive.value = PhotoModel(uri, file)
    }

}

