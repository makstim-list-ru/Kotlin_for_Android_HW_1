package ru.netology.kotlin_for_android_hw_1.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.room.Room
import retrofit2.Response
import ru.netology.kotlin_for_android_hw_1.dto.Post
import ru.netology.kotlin_for_android_hw_1.entity.PostEntity
import ru.netology.kotlin_for_android_hw_1.model.FeedModel
import ru.netology.kotlin_for_android_hw_1.retrofit.PostsRetrofitSuspend
import ru.netology.kotlin_for_android_hw_1.roomdb.RoomDBSuspend

class PostRepositoryInServerAndSQL(context: Context) : PostRepositorySuspend {

    private enum class ServerStatusFlag {
        LOADING, ERROR, EMPTY, REFRESHING, OK
    }

    private fun serverStatusChange(status: ServerStatusFlag): FeedModel {
        return when (status) {
            ServerStatusFlag.LOADING -> FeedModel(loading = true)
            ServerStatusFlag.ERROR -> FeedModel(error = true)
            ServerStatusFlag.EMPTY -> FeedModel(empty = true)
            ServerStatusFlag.REFRESHING -> FeedModel(refreshing = true)
            else -> FeedModel()
        }
    }

    private val db =
        Room.databaseBuilder(context, RoomDBSuspend::class.java, "database.db")
//            .allowMainThreadQueries()
            .build()

    private val dao = db.getPostDao()

    private val data = dao.getPostsAll().map { it -> it.map { it.toPostFromEntity() } }
    fun getData() = data

    private val servStat = MutableLiveData(FeedModel())
    fun getServerStatus() = servStat

    override fun getPostsAll(): LiveData<List<Post>> {
        //TODO - deleted - obsolete function
        return data
    }

    suspend fun getPostsAllAsync(): LiveData<List<Post>> {

        servStat.value = serverStatusChange(ServerStatusFlag.LOADING)
        try {
            val response = PostsRetrofitSuspend.retrofitService.getAll()
            val posts = retrofitErrorHandler(response) ?: return data

            dao.insert(posts.map { PostEntity.fromPostToEntity(it) })

            val postsToDelete = data.value?.filter { !posts.contains(it) }
            postsToDelete?.forEach { dao.removeByID(it.id) }

            if (posts.isEmpty()) servStat.postValue(serverStatusChange(ServerStatusFlag.EMPTY))
            else servStat.postValue(serverStatusChange(ServerStatusFlag.OK))

            return data
        } catch (e: Exception) {
            servStat.postValue(serverStatusChange(ServerStatusFlag.ERROR))
        }
        return data
    }

    override suspend fun shareByID(id: Long) = dao.shareByID(id)

    override suspend fun removeByID(id: Long) {
        dao.removeByID(id)
        try {
            PostsRetrofitSuspend.retrofitService.removeById(id)
        } catch (e: Exception) {
            servStat.postValue(serverStatusChange(ServerStatusFlag.ERROR))
        }
    }

    override suspend fun edit(post: Post) {
        dao.edit(PostEntity.fromPostToEntity(post))

        try {
            PostsRetrofitSuspend.retrofitService.save(post)
        } catch (e: Exception) {
            servStat.postValue(serverStatusChange(ServerStatusFlag.ERROR))
        }
    }

    override suspend fun save(post: Post) {

        val myPost = post.copy(author = "Me", authorAvatar = "sber.jpg")

        dao.save(
            PostEntity.fromPostToEntity(
                post.copy(
                    author = "Me",
                    published = "Now",
                    content = post.content,
                    authorAvatar = "sber.jpg"
                )
            )
        )

        try {
            PostsRetrofitSuspend.retrofitService.save(myPost)
        } catch (e: Exception) {
            servStat.postValue(serverStatusChange(ServerStatusFlag.ERROR))
        }
    }

    override suspend fun likeByID(id: Long) {
        dao.likeByID(id)
        val post = dao.getPostById(id).toPostFromEntity()

        try {
            if (post.likedByMe) PostsRetrofitSuspend.retrofitService.likeById(id)
            else PostsRetrofitSuspend.retrofitService.dislikeById(id)
        } catch (e: Exception) {
            servStat.postValue(serverStatusChange(ServerStatusFlag.ERROR))
        }
    }

    private fun <T> retrofitErrorHandler(res: Response<T>): T? {
        if (res.isSuccessful) {
            servStat.postValue(serverStatusChange(ServerStatusFlag.OK))
            return res.body()
        } else {
            servStat.postValue(serverStatusChange(ServerStatusFlag.ERROR))
        }
        return null
    }

}

