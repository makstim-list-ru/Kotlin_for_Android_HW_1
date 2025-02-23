package ru.netology.kotlin_for_android_hw_1.apputils

import ru.netology.kotlin_for_android_hw_1.dto.Post
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.pow

object NetologyUtilities {
    fun getFormatedNumber(count: Long): String {
        if (count < 1000) return "" + count
        val exp = (ln(count.toDouble()) / ln(1000.0)).toInt()
        if (count / 1000.0.pow(exp.toDouble()) >= 10) {
            return String.format("%.0f%c", count / 1000.0.pow(exp.toDouble()), "kMGTPE"[exp - 1])
        } else {
            return String.format(
                "%.1f%c",
                floor(count / 1000.0.pow(exp.toDouble()) * 10) / 10,
                "kMGTPE"[exp - 1]
            )
        }
    }

    fun samplePosts(idStart: Long): List<Post> {
        var id=idStart
        val postsTests = listOf(
            Post(
                id = id++,
                author = "Нетология. Университет интернет-профессий будущего",
                published = "21 мая в 18:36",
                content = "Много лет размышлял я над жизнью земной.\n" +
                        "Непонятного нет для меня под луной.\n" +
                        "Мне известно, что мне ничего не известно!\n" +
                        "Вот последняя правда, открытая мной. → https://library.vladimir.ru/spravochnyj-material/omar2.html"
            ),
            Post(
                id = id++,
                author = "Нетология. Университет интернет-профессий будущего",
                published = "21 мая в 18:36",
                content = "Я — школяр в этом лучшем из лучших миров.\n" +
                        "Труд мой тяжек: учитель уж больно суров!\n" +
                        "До седин я у жизни хожу в подмастерьях,\n" +
                        "Все еще не зачислен в разряд мастеров… → https://library.vladimir.ru/spravochnyj-material/omar2.html"
            ),
            Post(
                id = id++,
                author = "Нетология. Университет интернет-профессий будущего",
                published = "21 мая в 18:36",
                content = "Лучше впасть в нищету, голодать или красть,\n" +
                        "Чем в число блюдолизов презренных попасть.\n" +
                        "Лучше кости глодать, чем прельститься сластями\n" +
                        "За столом у мерзавцев, имеющих власть. → https://library.vladimir.ru/spravochnyj-material/omar2.html"
            ),
            Post(
                id = id++,
                author = "Нетология. Университет интернет-профессий будущего",
                published = "21 мая в 18:36",
                content = "Не оплакивай, смертный, вчерашних потерь,\n" +
                        "Дел сегодняшних завтрашней меркой не мерь,\n" +
                        "Ни былой, ни грядущей минуте не верь,\n" +
                        "Верь минуте текущей — будь счастлив теперь! → https://library.vladimir.ru/spravochnyj-material/omar2.html"
            ),
            Post(
                id = id++,
                author = "Нетология. Университет интернет-профессий будущего",
                published = "21 мая в 18:36",
                content = "Если все государства, вблизи и вдали,\n" +
                        "Покоренные, будут валяться в пыли —\n" +
                        "Ты не станешь, великий владыка, бессмертным.\n" +
                        "Твой удел невелик: три аршина земли. → https://library.vladimir.ru/spravochnyj-material/omar2.html"
            ),
            Post(
                id = id++,
                author = "Нетология. Университет интернет-профессий будущего",
                published = "21 мая в 18:36",
                content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
                video = "https://www.youtube.com/watch?v=WhWc3b3KhnY"
            )
        )
        return postsTests
    }
}