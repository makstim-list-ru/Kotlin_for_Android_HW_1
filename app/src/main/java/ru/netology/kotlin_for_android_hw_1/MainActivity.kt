package ru.netology.kotlin_for_android_hw_1

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.kotlin_for_android_hw_1.databinding.ActivityMainBinding
import ru.netology.kotlin_for_android_hw_1.databinding.PostCardBinding
import ru.netology.kotlin_for_android_hw_1.dto.Post
import ru.netology.kotlin_for_android_hw_1.viewmodel.PostViewModel
import kotlin.math.ln
import kotlin.math.pow

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel by viewModels<PostViewModel>()


//        val post = Post(
//            id = 1,
//            author = "Нетология. Университет интернет-профессий будущего",
//            published = "21 мая в 18:36",
//            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb"
//        )

        viewModel.data.observe(this) { posts ->
            binding.container.removeAllViews()
            posts.map { post ->
                PostCardBinding.inflate(layoutInflater, binding.container, false).apply {
                    this.author1.text = post.author
                    this.published1.text = post.published
                    this.content1.text = post.content
                    this.imageButtonHeart1.text = getFormatedNumber(post.likesNum)
                    this.imageButtonHeart1.setCompoundDrawablesWithIntrinsicBounds(
                        if (!post.likedByMe) {
                            getResources().getDrawable(R.drawable.outline_favorite_border_24)
                        } else {
                            getResources().getDrawable(R.drawable.baseline_favorite_24)
                        },
                        null, null, null
                    )
                    this.imageButtonShare1.text = getFormatedNumber(post.sharesNum)
                    this.imageButtonView1.text = getFormatedNumber(post.seenNum)


                    this.imageButtonHeart1.setOnClickListener {
                        viewModel.likeVM(post.id)
//                if (post.likedByMe) post.likesNum-- else post.likesNum++
//                binding.imageButtonHeart1.text = getFormatedNumber(post.likesNum)
//
//                post.likedByMe = !post.likedByMe
//                binding.imageButtonHeart1.setCompoundDrawablesWithIntrinsicBounds(
//                    if (!post.likedByMe) {
//                        getResources().getDrawable(R.drawable.outline_favorite_border_24)
//                    } else {
//                        getResources().getDrawable(R.drawable.baseline_favorite_24)
//                    },
//                    null, null, null
//                )
                    }

                    this.imageButtonShare1.setOnClickListener {
                        viewModel.shareVM(post.id)
                        // post.sharesNum++
                        //binding.imageButtonShare1.text = getFormatedNumber(post.sharesNum)
                    }

//                    this.root.setOnClickListener {
//                        println("binding.root.setOnClickListener_ON")
//                    }
                }.root
            }.forEach { binding.container.addView(it) }
        }


//        enableEdgeToEdge()
//        setContentView(R.layout.activity_main)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }


//        println("cdaedcaefdcfefdcfcefc\t" + getFormatedNumber(99))
//        println("cdaedcaefdcfefdcfcefc\t" + getFormatedNumber(999))
//        println("cdaedcaefdcfefdcfcefc\t" + getFormatedNumber(1_100))
//        println("cdaedcaefdcfefdcfcefc\t" + getFormatedNumber(11_000))
//        println("cdaedcaefdcfefdcfcefc\t" + getFormatedNumber(1_100_999))
//        println("cdaedcaefdcfefdcfcefc\t" + getFormatedNumber(11_100_999))
//        println("cdaedcaefdcfefdcfcefc\t" + getFormatedNumber(111_100_999))
//        println("cdaedcaefdcfefdcfcefc\t" + getFormatedNumber(1_100_999_999))

    }

    fun getFormatedNumber(count: Long): String {
        if (count < 1000) return "" + count
        val exp = (ln(count.toDouble()) / ln(1000.0)).toInt()
        if (count / 1000.0.pow(exp.toDouble()) >= 10) {
            return String.format("%.0f%c", count / 1000.0.pow(exp.toDouble()), "kMGTPE"[exp - 1])
        } else {
            return String.format(
                "%.1f%c",
                Math.floor(count / 1000.0.pow(exp.toDouble()) * 10) / 10,
                "kMGTPE"[exp - 1]
            )
        }
    }

}