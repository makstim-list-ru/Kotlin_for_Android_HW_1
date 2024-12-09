package ru.netology.kotlin_for_android_hw_1.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.kotlin_for_android_hw_1.dto.Post

class PostRepositoryInMemory : PostRepository {
    private var nextPostID = 1L
    private var posts = listOf<Post>(
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

    private val data = MutableLiveData(posts)

    override fun getPostsAll(): LiveData<List<Post>> {
        return data
    }

    override fun likeByID(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(
                likesNum = if (it.likedByMe) it.likesNum - 1
                else it.likesNum + 1,
                likedByMe = !it.likedByMe
            )
        }
        data.value = posts
    }

    override fun shareByID(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(
                sharesNum = it.sharesNum + 1
            )
        }
        data.value = posts
    }

    override fun removeByID(id: Long) {
        posts = posts.filter { it.id != id }
        data.value = posts
    }

    override fun save(post: Post) {
        posts = posts.plus(post.copy(id = nextPostID++, author = "Me", published = "Now"))
        data.value = posts
    }

    override fun edit(post: Post) {
        posts = posts.map {
            if (it.id != post.id) it else it.copy(content = post.content)
        }
        data.value = posts
    }
}