package ru.netology.kotlin_for_android_hw_1

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.kotlin_for_android_hw_1.adapter.PostsAdapter
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

        val adapter = PostsAdapter { post, key ->
            if (key.equals("like")) viewModel.likeVM(post.id)
            if (key.equals("share")) viewModel.shareVM(post.id)
        }
        binding.container.adapter = adapter

        viewModel.data.observe(this) { posts ->
            adapter.list = posts
        }
    }
}