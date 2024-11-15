package ru.netology.kotlin_for_android_hw_1.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.kotlin_for_android_hw_1.dto.Post

class PostRepositoryInMemory : PostRepository {
    private val data = MutableLiveData<Post>(
        Post(
            id = 1,
            author = "Нетология. Университет интернет-профессий будущего",
            published = "21 мая в 18:36",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb"
        )
    )

    override fun getPost(): LiveData<Post> {
        return data
    }

    override fun like() {
        val currentPost = data.value ?: return
        val updatedPost = currentPost.copy(
            likedByMe = !currentPost.likedByMe,
            likesNum = if (currentPost.likedByMe) currentPost.likesNum - 1 else currentPost.likesNum + 1
        )
        data.value = updatedPost
    }

    override fun share() {
        val currentPost = data.value ?: return
        val updatedPost = currentPost.copy(
            sharesNum = currentPost.sharesNum + 1
        )
        data.value = updatedPost
    }
}