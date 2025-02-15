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
        val posts = listOf(
            Post(
                id = nextPostID++,
                author = "Нетология. Университет интернет-профессий будущего",
                published = "21 мая в 18:36",
                content = "Много лет размышлял я над жизнью земной.\n" +
                        "Непонятного нет для меня под луной.\n" +
                        "Мне известно, что мне ничего не известно!\n" +
                        "Вот последняя правда, открытая мной. → https://library.vladimir.ru/spravochnyj-material/omar2.html"
            ),
            Post(
                id = nextPostID++,
                author = "Нетология. Университет интернет-профессий будущего",
                published = "21 мая в 18:36",
                content = "Я — школяр в этом лучшем из лучших миров.\n" +
                        "Труд мой тяжек: учитель уж больно суров!\n" +
                        "До седин я у жизни хожу в подмастерьях,\n" +
                        "Все еще не зачислен в разряд мастеров… → https://library.vladimir.ru/spravochnyj-material/omar2.html"
            ),
            Post(
                id = nextPostID++,
                author = "Нетология. Университет интернет-профессий будущего",
                published = "21 мая в 18:36",
                content = "Лучше впасть в нищету, голодать или красть,\n" +
                        "Чем в число блюдолизов презренных попасть.\n" +
                        "Лучше кости глодать, чем прельститься сластями\n" +
                        "За столом у мерзавцев, имеющих власть. → https://library.vladimir.ru/spravochnyj-material/omar2.html"
            ),
            Post(
                id = nextPostID++,
                author = "Нетология. Университет интернет-профессий будущего",
                published = "21 мая в 18:36",
                content = "Не оплакивай, смертный, вчерашних потерь,\n" +
                        "Дел сегодняшних завтрашней меркой не мерь,\n" +
                        "Ни былой, ни грядущей минуте не верь,\n" +
                        "Верь минуте текущей — будь счастлив теперь! → https://library.vladimir.ru/spravochnyj-material/omar2.html"
            ),
            Post(
                id = nextPostID++,
                author = "Нетология. Университет интернет-профессий будущего",
                published = "21 мая в 18:36",
                content = "Если все государства, вблизи и вдали,\n" +
                        "Покоренные, будут валяться в пыли —\n" +
                        "Ты не станешь, великий владыка, бессмертным.\n" +
                        "Твой удел невелик: три аршина земли. → https://library.vladimir.ru/spravochnyj-material/omar2.html"
            ),
            Post(
                id = nextPostID++,
                author = "Нетология. Университет интернет-профессий будущего",
                published = "21 мая в 18:36",
                content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
                video = "https://www.youtube.com/watch?v=WhWc3b3KhnY"
            )
        )

        private const val BASE_URL = "http://10.0.2.2:9999"
        private val jsonType = "application/json".toMediaType()
    }

    private val data = MutableLiveData(posts)
    private val servStat = MutableLiveData(FeedModel())

    override fun getPostsAll(): LiveData<List<Post>> {

        thread {
            servStat.postValue(serverStatusChange("loading"))

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
            if (posts.isEmpty()) servStat.postValue(serverStatusChange("empty"))
            else servStat.postValue(serverStatusChange("OK"))

            data.postValue(posts)
        }
        return data
    }

//    fun getAllAsync(callback: PostRepository.GetAllCallback) {
//        val request: Request = Request.Builder()
//            .url("${BASE_URL}/api/slow/posts")
//            .build()
//
//        client.newCall(request)
//            .enqueue(object : Callback {
//                override fun onResponse(call: Call, response: Response) {
//                    val body = response.body?.string() ?: throw RuntimeException("body is null")
//                    try {
//                        callback.onSuccess(gson.fromJson(body, typeToken.type))
//                    } catch (e: Exception) {
//                        callback.onError(e)
//                    }
//                }
//
//                override fun onFailure(call: Call, e: IOException) {
//                    callback.onError(e)
//                }
//            })
//    }

    private fun serverStatusChange(status: String): FeedModel {
        return when (status) {
            "loading" -> FeedModel(loading = true)
            "error" -> FeedModel(error = true)
            "empty" -> FeedModel(empty = true)
            "refreshing" -> FeedModel(refreshing = true)
            else -> FeedModel()
        }
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
                    likesNum = if (it.likedByMe) it.likesNum - 1 else it.likesNum + 1
                )
            }
        }
        data.value = posts

        if (likedByMeFlag)
            thread {
                val dislike: Request = Request.Builder()
                    .delete()
                    .url("${BASE_URL}/api/slow/posts/$id/likes")
                    .build()

                client.newCall(dislike)
                    .execute()
                    .close()
            }
        else
            thread {
                val like: Request = Request.Builder()
                    .post(EMPTY_REQUEST)
                    .url("${BASE_URL}/api/slow/posts/$id/likes")
                    .build()

                client.newCall(like)
                    .execute()
                    .close()
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
        thread {
            val request: Request = Request.Builder()
                .delete()
                .url("${BASE_URL}/api/slow/posts/$id")
                .build()

            client.newCall(request)
                .execute()
                .close()
        }

        var posts = data.value
        posts = posts?.filter { it.id != id }
        data.value = posts
    }

    override fun save(post: Post) {
        thread {

            val myPost = post.copy(author = "Me", published = "Now")
            val request: Request = Request.Builder()
                .post(gson.toJson(myPost)
                    .toRequestBody(jsonType))
                .url("${BASE_URL}/api/slow/posts")
                .build()

            client.newCall(request)
                .execute()
                .close()
        }

        var posts = data.value
        posts = posts?.plus(post.copy(id = nextPostID++, author = "Me", published = "Now"))
        data.value = posts
    }

    override fun edit(post: Post) {

        thread {
            val request: Request = Request.Builder()
                .post(gson.toJson(post).toRequestBody(jsonType))
                .url("${BASE_URL}/api/slow/posts")
                .build()

            client.newCall(request)
                .execute()
                .close()
        }

        var posts = data.value
        posts = posts?.map {
            if (it.id != post.id) it else it.copy(content = post.content)
        }
        data.value = posts
    }
}

