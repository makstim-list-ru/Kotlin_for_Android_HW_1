package ru.netology.kotlin_for_android_hw_1.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.insertSeparators
import androidx.paging.map
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
import ru.netology.kotlin_for_android_hw_1.dao.PostDaoSuspend
import ru.netology.kotlin_for_android_hw_1.dao.PostRemoteKeyDao
import ru.netology.kotlin_for_android_hw_1.dto.Ad
import ru.netology.kotlin_for_android_hw_1.dto.Attachment
import ru.netology.kotlin_for_android_hw_1.dto.FeedItem
import ru.netology.kotlin_for_android_hw_1.dto.Post
import ru.netology.kotlin_for_android_hw_1.entity.PostEntity
import ru.netology.kotlin_for_android_hw_1.media.AttachmentType
import ru.netology.kotlin_for_android_hw_1.media.MediaUploadResponse
import ru.netology.kotlin_for_android_hw_1.model.FeedModel
import ru.netology.kotlin_for_android_hw_1.retrofit.PostsRetrofitSuspendInterface
import ru.netology.kotlin_for_android_hw_1.roomdb.RoomDBSuspend
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class PostRepositoryInServerAndSQL @Inject constructor(
    private val db: RoomDBSuspend,
    private val dao: PostDaoSuspend,
    private val postsRetrofitSuspendInterface: PostsRetrofitSuspendInterface,
    private val postRemoteKeyDao: PostRemoteKeyDao,
) : PostRepositorySuspend {


    private val servStat = MutableLiveData(FeedModel())

    //    private val db = Room.databaseBuilder(context, RoomDBSuspend::class.java, "database.db").build()
    //    private val dao = db.getPostDao()

    @OptIn(ExperimentalPagingApi::class)
    private val dataFlow: Flow<PagingData<FeedItem>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = true),
        remoteMediator = PostRemoteMediator(
            db,
            dao,
            postsRetrofitSuspendInterface,
            postRemoteKeyDao
        ),
        pagingSourceFactory = { dao.pagingSource() },
    ).flow.map { pagingData ->
        pagingData.map { postEntity -> postEntity.toPostFromEntity() }
            .insertSeparators { previous, next ->
                if (previous?.id?.rem(5) == 0L) Ad(
                    Random.nextLong(),
                    "figma.jpg"
                ) else null
            }
    }


    //    private val dataFlow = dao.getPostsAll().map { it -> it.map { it.toPostFromEntity() } }
    private val dataLive: LiveData<PagingData<FeedItem>> = dataFlow.asLiveData(Dispatchers.Default)
    private val newerCountLive = dataLive.switchMap {
        getPostsNewer().asLiveData(Dispatchers.Default)
    }


    @Volatile
    private var flagLoad = false

    override fun getServStat(): LiveData<FeedModel> = servStat
    override fun getData(): LiveData<PagingData<FeedItem>> = dataLive
    override fun getNewerCount() = newerCountLive

    override fun getDataFlow(): Flow<PagingData<FeedItem>> = dataFlow


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
//            val response = PostsRetrofitSuspend.retrofitService.getAll()
            val response = postsRetrofitSuspendInterface.getAll()
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
            postsRetrofitSuspendInterface.removeById(id)
        } catch (e: Exception) {
            servStat.postValue(serverStatus(ServerStatus.ERROR))
            println("removeByID(id: Long)->PostsRetrofitSuspend.retrofitService.removeById(id) ERROR: $e")
        }
    }

    override suspend fun edit(post: Post, uploadFile: File?) {

        var responseUpload: MediaUploadResponse? = null

        if (uploadFile != null) try {
            responseUpload = upload(uploadFile) ?: let {
                println("save(post: Post, file: File)->FAULT upload file failure")
                return
            }
        } catch (e: Exception) {
            println("save(post: Post)->retrofitService.save(myPost) ERROR: $e")
            return
        }

        val post =
            post.copy(attachment = responseUpload?.let { Attachment(it.id, AttachmentType.IMAGE) })

        dao.edit(PostEntity.fromPostToEntity(post))

        try {
            postsRetrofitSuspendInterface.save(post)
        } catch (e: Exception) {
            servStat.postValue(serverStatus(ServerStatus.ERROR))
            println("edit(post: Post)->retrofitService.save(postWithAtt) ERROR: $e")
        }
    }

    override suspend fun save(post: Post, uploadFile: File?) {

        var responseUpload: MediaUploadResponse? = null
        val tempId: Long

        when {
            post.id > 0L -> {
                throw Exception("ERROR in save(post: Post, uploadFile: File?)")
            }

            post.id == 0L -> {
                if (uploadFile != null) try {
                    responseUpload = upload(uploadFile) ?: let {
                        println("save(post: Post, file: File)->FAULT upload file failure")
                        return
                    }
                } catch (e: Exception) {
                    println("save(post: Post)->retrofitService.save(myPost) ERROR: $e")
                    return
                }
                tempId = dao.getMinId()?.coerceAtMost(0)?.dec() ?: -1

            }

            else -> { // post.id<0L
                tempId = post.id
            }
        }

//        ++++++++++++++++++++++
//        tempId = if (post.id == 0L) dao.getMinId()?.coerceAtMost(0)?.dec() ?: -1 else post.id

        val tempPost = post.copy(
            id = tempId,
            author = "Me",
            authorAvatar = "sber.jpg",
            attachment = responseUpload?.let { Attachment(it.id, AttachmentType.IMAGE) })

        if (post.id == 0L) dao.save(    // если сохраняется свежий пост с присвоением нового (-)id в ЛБД
            PostEntity.fromPostToEntity(tempPost)
        )

        try {
            val serverPost = postsRetrofitSuspendInterface.save(tempPost.copy(id = 0L))
            dao.save(PostEntity.fromPostToEntity(serverPost))
            dao.removeByID(tempId)
        } catch (e: Exception) {
            servStat.postValue(serverStatus(ServerStatus.ERROR))
            println("save(post: Post)->retrofitService.save(myPost) ERROR: $e")
        }
    }

    override suspend fun likeByID(id: Long) {
        dao.likeByID(id)
        val post = dao.getPostById(id).toPostFromEntity()

        try {
            if (post.likedByMe) postsRetrofitSuspendInterface.likeById(id)
            else postsRetrofitSuspendInterface.dislikeById(id)
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
            val response = postsRetrofitSuspendInterface.getPostsNewer(
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
            val response = postsRetrofitSuspendInterface.getPostsNewer(dao.getMaxId() ?: 0L)
            if (response.isSuccessful) {
                servStat.postValue(serverStatus(ServerStatus.OK))
                val posts = response.body()
                if (!posts.isNullOrEmpty()) {
                    if (flagLoad) {
                        println("flagLoad is ON")
                    } else {
                        emit(posts.size)
                    }
                } else emit(0)
            } else {
                servStat.postValue(serverStatus(ServerStatus.ERROR))
                println("getPostsNewer()->response.isSuccessful ERROR: if-else")
            }
        }
    }.catch {
        servStat.postValue(serverStatus(ServerStatus.ERROR))
        println("getPostsNewer()->catch ERROR: CATCH")
    }


    override suspend fun upload(file: File): MediaUploadResponse? {
        try {
            val media = MultipartBody.Part.createFormData(
                "file", file.name, file.asRequestBody()
            )

            val response = postsRetrofitSuspendInterface.upload(media)
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


}

