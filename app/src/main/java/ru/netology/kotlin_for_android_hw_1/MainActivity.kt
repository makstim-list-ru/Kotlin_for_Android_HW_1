package ru.netology.kotlin_for_android_hw_1

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.kotlin_for_android_hw_1.adapter.PostsAdapter
import ru.netology.kotlin_for_android_hw_1.databinding.ActivityMainBinding
import ru.netology.kotlin_for_android_hw_1.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel by viewModels<PostViewModel>()

        val adapter = PostsAdapter { post, key ->
            if (key == "like") viewModel.likeVM(post.id)
            if (key == "share") viewModel.shareVM(post.id)
            if (key == "remove") viewModel.removeVM(post.id)
            if (key == "edit") viewModel.editVM(post)
        }
        binding.container.adapter = adapter

        viewModel.data.observe(this) { posts ->
            val newPostFlag = adapter.currentList.size < posts.size
            adapter.submitList(posts)
            if (newPostFlag) {
                binding.container.smoothScrollToPosition(adapter.currentList.size)
            }
        }

        viewModel.editedPostLD.observe(this){
            binding.content.setText(it.content)
            binding.content.requestFocus()
        }

        binding.saveButton.setOnClickListener {
            val text = binding.content.text.toString()
            if (text.isBlank()) {
                Toast.makeText(this, "error empty content", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            viewModel.saveVM(text)
            binding.content.setText("")
            binding.content.clearFocus()
        }
    }
}