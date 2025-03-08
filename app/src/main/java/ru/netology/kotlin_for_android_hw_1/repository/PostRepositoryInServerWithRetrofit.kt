package ru.netology.kotlin_for_android_hw_1.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.kotlin_for_android_hw_1.apputils.NetologyUtilities
import ru.netology.kotlin_for_android_hw_1.dto.Post
import ru.netology.kotlin_for_android_hw_1.model.FeedModel
import ru.netology.kotlin_for_android_hw_1.retrofit.PostsRetrofit

class PostRepositoryInServerWithRetrofit(context: Context) : PostRepository {

//    companion object {
//
//        private var nextPostID = 1L
//        val posts = NetologyUtilities.samplePosts(nextPostID)
//
//        init {
//            nextPostID += posts.size
//        }
//    }

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
    fun getServerStatus() = servStat

    override fun getPostsAll(): LiveData<List<Post>> {
        //TODO - deleted - obsolete function
        return data
    }

    fun getPostsAllAsync(): LiveData<List<Post>> {

        servStat.value = serverStatusChange(ServerStatusFlag.LOADING)

        PostsRetrofit.retrofitService.getAll().enqueue(object : retrofit2.Callback<List<Post>> {

            override fun onResponse(
                call: retrofit2.Call<List<Post>>,
                response: retrofit2.Response<List<Post>>
            ) {
                if (response.isSuccessful) {
                    val posts = response.body() ?: throw RuntimeException("body is null")

                    data.postValue(posts)
                    if (posts.isEmpty()) servStat.postValue(serverStatusChange(ServerStatusFlag.EMPTY))
                    else servStat.postValue(serverStatusChange(ServerStatusFlag.OK))
                } else {
                    servStat.postValue(serverStatusChange(ServerStatusFlag.ERROR))
                }
            }

            override fun onFailure(call: retrofit2.Call<List<Post>>, e: Throwable) {
                servStat.postValue(serverStatusChange(ServerStatusFlag.ERROR))
            }
        })
        return data
    }


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

        if (likedByMeFlag) {
            PostsRetrofit.retrofitService.dislikeById(id)
                .enqueue(object : retrofit2.Callback<Post> {
                    override fun onResponse(
                        call: retrofit2.Call<Post>,
                        response: retrofit2.Response<Post>
                    ) {
                        if (response.isSuccessful) {
                            servStat.postValue(serverStatusChange(ServerStatusFlag.OK))
                        } else {
                            servStat.postValue(serverStatusChange(ServerStatusFlag.ERROR))
                        }
                    }

                    override fun onFailure(call: retrofit2.Call<Post>, e: Throwable) {
                        servStat.postValue(serverStatusChange(ServerStatusFlag.ERROR))
                    }
                })
        } else {
            PostsRetrofit.retrofitService.likeById(id)
                .enqueue(object : retrofit2.Callback<Post> {
                    override fun onResponse(
                        call: retrofit2.Call<Post>,
                        response: retrofit2.Response<Post>
                    ) {
                        if (response.isSuccessful) {
                            servStat.postValue(serverStatusChange(ServerStatusFlag.OK))
                        } else {
                            servStat.postValue(serverStatusChange(ServerStatusFlag.ERROR))
                        }
                    }

                    override fun onFailure(call: retrofit2.Call<Post>, e: Throwable) {
                        servStat.postValue(serverStatusChange(ServerStatusFlag.ERROR))
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

        PostsRetrofit.retrofitService.removeById(id)
            .enqueue(object : retrofit2.Callback<Unit> {
                override fun onResponse(
                    call: retrofit2.Call<Unit>,
                    response: retrofit2.Response<Unit>
                ) {
                    if (response.isSuccessful) {
                        servStat.postValue(serverStatusChange(ServerStatusFlag.OK))
                    } else {
                        servStat.postValue(serverStatusChange(ServerStatusFlag.ERROR))
                    }
                }

                override fun onFailure(call: retrofit2.Call<Unit>, e: Throwable) {
                    servStat.postValue(serverStatusChange(ServerStatusFlag.ERROR))
                }
            })

        var posts = data.value
        posts = posts?.filter { it.id != id }
        data.value = posts
    }

    override fun save(post: Post) {

        val myPost = post.copy(author = "Me", authorAvatar = "sber.jpg")

        PostsRetrofit.retrofitService.save(myPost)
            .enqueue(object : retrofit2.Callback<Post> {
                override fun onResponse(
                    call: retrofit2.Call<Post>,
                    response: retrofit2.Response<Post>
                ) {
                    if (response.isSuccessful) {
                        servStat.postValue(serverStatusChange(ServerStatusFlag.OK))
                    } else {
                        servStat.postValue(serverStatusChange(ServerStatusFlag.ERROR))
                    }
                }

                override fun onFailure(call: retrofit2.Call<Post>, e: Throwable) {
                    servStat.postValue(serverStatusChange(ServerStatusFlag.ERROR))
                }
            })

        var posts = data.value
        posts = posts?.plus(
            post.copy(
                id = NetologyUtilities.nextPostIdCalc(posts),
                author = "Me",
                published = "Now",
                authorAvatar = "sber.jpg"
            )
        )
        data.value = posts
    }

    override fun edit(post: Post) {

        PostsRetrofit.retrofitService.save(post)
            .enqueue(object : retrofit2.Callback<Post> {
                override fun onResponse(
                    call: retrofit2.Call<Post>,
                    response: retrofit2.Response<Post>
                ) {
                    if (response.isSuccessful) {
                        servStat.postValue(serverStatusChange(ServerStatusFlag.OK))
                    } else {
                        servStat.postValue(serverStatusChange(ServerStatusFlag.ERROR))
                    }
                }

                override fun onFailure(call: retrofit2.Call<Post>, e: Throwable) {
                    servStat.postValue(serverStatusChange(ServerStatusFlag.ERROR))
                }
            })

        var posts = data.value
        posts = posts?.map {
            if (it.id != post.id) it else it.copy(content = post.content)
        }
        data.value = posts
    }
}

