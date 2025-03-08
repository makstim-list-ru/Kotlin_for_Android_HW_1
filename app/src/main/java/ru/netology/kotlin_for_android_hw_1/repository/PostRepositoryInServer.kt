package ru.netology.kotlin_for_android_hw_1.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.internal.EMPTY_REQUEST
import ru.netology.kotlin_for_android_hw_1.apputils.NetologyUtilities
import ru.netology.kotlin_for_android_hw_1.dto.Post
import ru.netology.kotlin_for_android_hw_1.model.FeedModel
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class PostRepositoryInServer(context: Context) : PostRepository {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}

    companion object {

        private var nextPostID = 1L
        val posts = NetologyUtilities.samplePosts(nextPostID)

        init {
            nextPostID += posts.size
        }

        private const val BASE_URL = "http://10.0.2.2:9999"
        private val jsonType = "application/json".toMediaType()
    }

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


    //    private val data = MutableLiveData(posts)
    private val data = MutableLiveData(emptyList<Post>())
    fun getData() = data

    private val servStat = MutableLiveData(FeedModel())

    override fun getPostsAll(): LiveData<List<Post>> {

        thread {
            servStat.postValue(serverStatusChange(ServerStatusFlag.LOADING))

            val posts: MutableList<Post>

            val request: Request = Request.Builder()
                .url("${BASE_URL}/api/slow/posts")
                .build()

            posts = client.newCall(request)
                .execute()
                .let { it.body?.string() ?: throw RuntimeException("body is null") }
                .let {
                    gson.fromJson(it, typeToken.type)
                }
            if (posts.isEmpty()) servStat.postValue(serverStatusChange(ServerStatusFlag.EMPTY))
            else servStat.postValue(serverStatusChange(ServerStatusFlag.OK))

            data.postValue(posts)
        }
        return data
    }

    fun getPostsAllAsync(): LiveData<List<Post>> {

        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()

        servStat.value = serverStatusChange(ServerStatusFlag.LOADING)

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string() ?: throw RuntimeException("body is null")
                    try {
                        val posts: List<Post> = gson.fromJson(body, typeToken.type)
                        data.postValue(posts)
                        if (posts.isEmpty()) servStat.postValue(serverStatusChange(ServerStatusFlag.EMPTY))
                        else servStat.postValue(serverStatusChange(ServerStatusFlag.OK))
                    } catch (e: Exception) {
                        servStat.postValue(serverStatusChange(ServerStatusFlag.ERROR))
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    servStat.postValue(serverStatusChange(ServerStatusFlag.ERROR))
                }
            })
        return data
    }

    fun getServerStatus() = servStat

    override fun likeByID(id: Long) {

        var likedByMeFlag = false

        var posts = data.value
        posts = posts?.map {
            if (it.id != id) it else {
                likedByMeFlag = it.likedByMe
                it.copy(
                    likedByMe = !it.likedByMe,
                    likes = if (it.likedByMe) it.likes - 1 else it.likes + 1
                )
            }
        }
        data.value = posts

//        if (likedByMeFlag)
//            thread {
//                val dislike: Request = Request.Builder()
//                    .delete()
//                    .url("${BASE_URL}/api/slow/posts/$id/likes")
//                    .build()
//
//                client.newCall(dislike)
//                    .execute()
//                    .close()
//            }
//        else
//            thread {
//                val like: Request = Request.Builder()
//                    .post(EMPTY_REQUEST)
//                    .url("${BASE_URL}/api/slow/posts/$id/likes")
//                    .build()
//
//                client.newCall(like)
//                    .execute()
//                    .close()
//            }


        if (likedByMeFlag) {
            val dislike: Request = Request.Builder()
                .delete()
                .url("${BASE_URL}/api/slow/posts/$id/likes")
                .build()

            client.newCall(dislike)
                .enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        servStat.postValue(serverStatusChange(ServerStatusFlag.ERROR))
                    }

                    override fun onResponse(call: Call, response: Response) {
                        servStat.postValue(serverStatusChange(ServerStatusFlag.OK))
                    }

                })
        } else {
            val like: Request = Request.Builder()
                .post(EMPTY_REQUEST)
                .url("${BASE_URL}/api/slow/posts/$id/likes")
                .build()

            client.newCall(like)
                .enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        servStat.postValue(serverStatusChange(ServerStatusFlag.ERROR))
                    }

                    override fun onResponse(call: Call, response: Response) {
                        servStat.postValue(serverStatusChange(ServerStatusFlag.OK))
                    }
                })
        }
    }

    override fun shareByID(id: Long) {
        //TODO server share respond
        var posts = data.value
        posts = posts?.map {
            if (it.id != id) it else it.copy(
                sharesNum = it.sharesNum + 1
            )
        }
        data.value = posts
    }

    override fun removeByID(id: Long) {
//        thread {
//            val request: Request = Request.Builder()
//                .delete()
//                .url("${BASE_URL}/api/slow/posts/$id")
//                .build()
//
//            client.newCall(request)
//                .execute()
//                .close()
//        }


        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    servStat.postValue(serverStatusChange(ServerStatusFlag.ERROR))
                }

                override fun onResponse(call: Call, response: Response) {
                    servStat.postValue(serverStatusChange(ServerStatusFlag.OK))
                }
            })

        var posts = data.value
        posts = posts?.filter { it.id != id }
        data.value = posts
    }

    override fun save(post: Post) {
//        thread {
//
//            val myPost = post.copy(author = "Me")
//            val request: Request = Request.Builder()
//                .post(
//                    gson.toJson(myPost)
//                        .toRequestBody(jsonType)
//                )
//                .url("${BASE_URL}/api/slow/posts")
//                .build()
//
//            client.newCall(request)
//                .execute()
//                .close()
//        }

        val myPost = post.copy(author = "Me")
        val request: Request = Request.Builder()
            .post(
                gson.toJson(myPost)
                    .toRequestBody(jsonType)
            )
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    servStat.postValue(serverStatusChange(ServerStatusFlag.ERROR))
                }

                override fun onResponse(call: Call, response: Response) {
                    servStat.postValue(serverStatusChange(ServerStatusFlag.OK))
                }
            })

        var posts = data.value
        posts = posts?.plus(post.copy(id = nextPostID++, author = "Me", published = "Now"))
        data.value = posts
    }

    override fun edit(post: Post) {

//        thread {
//            val request: Request = Request.Builder()
//                .post(gson.toJson(post).toRequestBody(jsonType))
//                .url("${BASE_URL}/api/slow/posts")
//                .build()
//
//            client.newCall(request)
//                .execute()
//                .close()
//        }


        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    servStat.postValue(serverStatusChange(ServerStatusFlag.ERROR))
                }

                override fun onResponse(call: Call, response: Response) {
                    servStat.postValue(serverStatusChange(ServerStatusFlag.OK))
                }
            })

        var posts = data.value
        posts = posts?.map {
            if (it.id != post.id) it else it.copy(content = post.content)
        }
        data.value = posts
    }
}

