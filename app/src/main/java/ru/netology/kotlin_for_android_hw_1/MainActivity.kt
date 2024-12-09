package ru.netology.kotlin_for_android_hw_1

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
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

        val getContent2 = registerForActivityResult(ContractMainActivity2) { result ->
            result ?: return@registerForActivityResult
            viewModel.saveVM(result)
        }

        val adapter = PostsAdapter { post, key ->
            if (key == "like") viewModel.likeVM(post.id)
            if (key == "share") {
                val intent = Intent().apply {
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                    action = Intent.ACTION_SEND
                }
                val shareIntent = Intent.createChooser(intent, "Sharing the post")
                startActivity(shareIntent)
                viewModel.shareVM(post.id)
            }
            if (key == "video") {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.video))
                startActivity(intent)
            }
            if (key == "remove") viewModel.removeVM(post.id)
            if (key == "edit") {
                getContent2.launch(post.content)
                viewModel.editVM(post)
            }
            if (key == "cancel") viewModel.cancelVM()
        }
        binding.container.adapter = adapter

        viewModel.data.observe(this) { posts ->
            val newPostFlag = adapter.currentList.size < posts.size
            adapter.submitList(posts)
            if (newPostFlag) {
                binding.container.smoothScrollToPosition(adapter.currentList.size)
            }
        }

        binding.plusButton.setOnClickListener {
            getContent2.launch(null)
        }


//        viewModel.editedPostLD.observe(this) {
//            binding.content.setText(it.content)
//            binding.content.requestFocus()
//            if (it.id != 0L) {
//                binding.preview1.setText(it.author)
//                binding.preview2.setText(it.published)
//                binding.group1.setVisibility(View.VISIBLE)
//            } else binding.group1.setVisibility(View.GONE)
//        }
//
//        binding.saveButton.setOnClickListener {
//            val text = binding.content.text.toString()
//            if (text.isBlank()) {
//                Toast.makeText(this, "error empty content", Toast.LENGTH_LONG).show()
//                return@setOnClickListener
//            }
//            viewModel.saveVM(text)
//            binding.content.setText("")
//            binding.content.clearFocus()
//        }
//
//        binding.cancelEdit.setOnClickListener {
//            binding.content.setText("")
//            binding.content.clearFocus()
//            viewModel.cancelVM()
//            //binding.group1.setVisibility(View.GONE)
//        }
    }
}