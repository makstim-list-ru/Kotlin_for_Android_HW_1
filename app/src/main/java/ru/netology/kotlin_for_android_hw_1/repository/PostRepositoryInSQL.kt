package ru.netology.kotlin_for_android_hw_1.repository

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.kotlin_for_android_hw_1.dto.Post

class PostRepositoryInSQL(context: Context) : PostRepository {

    object PostColumns {
        const val COLUMN_ID = "id"
        const val COLUMN_AUTHOR = "author"
        const val COLUMN_CONTENT = "content"
        const val COLUMN_PUBLISHED = "published"
        const val COLUMN_LIKED_BY_ME = "likedByMe"
        const val COLUMN_LIKES = "likes"
        const val COLUMN_SHARES = "shares"
        const val COLUMN_SEEN = "seen"
        const val COLUMN_VIDEO = "video"
        val ALL_COLUMNS = arrayOf(
            COLUMN_ID,
            COLUMN_AUTHOR,
            COLUMN_CONTENT,
            COLUMN_PUBLISHED,
            COLUMN_LIKED_BY_ME,
            COLUMN_LIKES,
            COLUMN_SHARES,
            COLUMN_SEEN,
            COLUMN_VIDEO
        )
    }

    companion object {
        const val SQLNAME = "posts"
        val CREATE_TABLE = """
        CREATE TABLE $SQLNAME (
            ${PostColumns.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
            ${PostColumns.COLUMN_AUTHOR} TEXT NOT NULL,
            ${PostColumns.COLUMN_CONTENT} TEXT NOT NULL,
            ${PostColumns.COLUMN_PUBLISHED} TEXT NOT NULL,
            ${PostColumns.COLUMN_LIKED_BY_ME} BOOLEAN NOT NULL DEFAULT 0,
            ${PostColumns.COLUMN_LIKES} INTEGER NOT NULL DEFAULT 0,
            ${PostColumns.COLUMN_SHARES} INTEGER NOT NULL DEFAULT 0,
            ${PostColumns.COLUMN_SEEN} INTEGER NOT NULL DEFAULT 0,
            ${PostColumns.COLUMN_VIDEO} TEXT NOT NULL DEFAULT ""
        );
        """.trimIndent()

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
    }


    class postsSQLdbHelper(context: Context) :
        SQLiteOpenHelper(context, SQLNAME, null, 1) {
        override fun onCreate(p0: SQLiteDatabase) {
            p0.execSQL(CREATE_TABLE)

            posts.forEach {
                p0.execSQL(
                    """
                    INSERT INTO $SQLNAME 
                    (${PostColumns.COLUMN_AUTHOR},${PostColumns.COLUMN_CONTENT},${PostColumns.COLUMN_PUBLISHED}) 
                    VALUES ("${it.author}","${it.content}","${it.published}")
                """.trimIndent()
                )
            }
        }

        override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
            TODO("Not yet implemented")
        }

    }

    private fun map(cursor: Cursor): Post {
        with(cursor) {
            return Post(
                id = getLong(getColumnIndexOrThrow(PostColumns.COLUMN_ID)),
                author = getString(getColumnIndexOrThrow(PostColumns.COLUMN_AUTHOR)),
                content = getString(getColumnIndexOrThrow(PostColumns.COLUMN_CONTENT)),
                published = getString(getColumnIndexOrThrow(PostColumns.COLUMN_PUBLISHED)),
                likedByMe = getInt(getColumnIndexOrThrow(PostColumns.COLUMN_LIKED_BY_ME)) != 0,
                likesNum = getLong(getColumnIndexOrThrow(PostColumns.COLUMN_LIKES)),
                sharesNum = getLong(getColumnIndexOrThrow(PostColumns.COLUMN_SHARES)),
                seenNum = getLong(getColumnIndexOrThrow(PostColumns.COLUMN_SEEN)),
                video = getString(getColumnIndexOrThrow(PostColumns.COLUMN_VIDEO))
            )
        }
    }


    private val db = postsSQLdbHelper(context).writableDatabase
    private val data = MutableLiveData(posts)

//    init {
//        val file = context.filesDir.resolve(FILENAME)
//        if (file.exists()) {
//            context.openFileInput(FILENAME).bufferedReader().use { it ->
//                posts = gson.fromJson(it, token)
//                nextPostID = if (posts.isNotEmpty()) posts.maxOf { it.id } + 1 else 1L
//                data.value = posts
//            }
//        } else
//            sync()
//    }


    override fun getPostsAll(): LiveData<List<Post>> {
        val posts = mutableListOf<Post>()
        db.query(
            SQLNAME,
            PostColumns.ALL_COLUMNS,
            null,
            null,
            null,
            null,
            "${PostColumns.COLUMN_ID} DESC"
        ).use {
            while (it.moveToNext()) {
                posts.add(map(it))
            }
        }
        data.value = posts
        return data
    }

    override fun likeByID(id: Long) {
        db.execSQL(
            """
           UPDATE $SQLNAME SET
               likes = likes + CASE WHEN likedByMe THEN -1 ELSE 1 END,
               likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
           WHERE id = ?;
        """.trimIndent(), arrayOf(id)
        )
        var posts = data.value
        posts = posts?.map {
            if (it.id != id) it else it.copy(
                likedByMe = !it.likedByMe,
                likesNum = if (it.likedByMe) it.likesNum - 1 else it.likesNum + 1
            )
        }
        data.value = posts
    }

    override fun shareByID(id: Long) {
        db.execSQL(
            """
           UPDATE $SQLNAME SET
               shares = shares + 1
           WHERE id = ?;
        """.trimIndent(), arrayOf(id)
        )
        var posts = data.value
        posts = posts?.map {
            if (it.id != id) it else it.copy(
                sharesNum = it.sharesNum + 1
            )
        }
        data.value = posts
    }

    override fun removeByID(id: Long) {
        db.delete(
            SQLNAME,
            "${PostColumns.COLUMN_ID} = ?",
            arrayOf(id.toString())
        )
        var posts = data.value
        posts = posts?.filter { it.id != id }
        data.value = posts
    }

    override fun save(post: Post) {

        val values = ContentValues().apply {
            // TODO: remove hardcoded values
            put(PostColumns.COLUMN_AUTHOR, "Me")
            put(PostColumns.COLUMN_CONTENT, post.content)
            put(PostColumns.COLUMN_PUBLISHED, "now")
        }

        db.insert(SQLNAME, null, values)

        var posts = data.value
        posts = posts?.plus(post.copy(id = nextPostID++, author = "Me", published = "Now"))
        data.value = posts
    }

    override fun edit(post: Post) {

        val values = ContentValues().apply {
            // TODO: remove hardcoded values
            put(PostColumns.COLUMN_CONTENT, post.content)
        }

        db.update(
            SQLNAME,
            values,
            "${PostColumns.COLUMN_ID} = ?",
            arrayOf(post.id.toString()),
        )

        var posts = data.value
        posts = posts?.map {
            if (it.id != post.id) it else it.copy(content = post.content)
        }
        data.value = posts
    }
}

