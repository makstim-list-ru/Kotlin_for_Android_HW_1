package ru.netology.kotlin_for_android_hw_1

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.kotlin_for_android_hw_1.adapter.PostsAdapter
import ru.netology.kotlin_for_android_hw_1.databinding.FragmentMainBinding
import ru.netology.kotlin_for_android_hw_1.viewmodel.PostViewModel

class MainFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMainBinding.inflate(inflater, container, false)


        val viewModel by viewModels<PostViewModel>(ownerProducer = ::requireParentFragment)

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
                findNavController().navigate(R.id.action_mainFragment_to_editorFragment,
                    Bundle().apply { this.putString("TEXT_TRANSFER", post.content) })
                viewModel.editVM(post)
            }
            if (key == "cancel") viewModel.cancelVM()
        }
        binding.container.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) { posts ->
            val newPostFlag = adapter.currentList.size < posts.size
            adapter.submitList(posts)
            if (newPostFlag) {
                binding.container.smoothScrollToPosition(adapter.currentList.size)
            }
        }

        binding.plusButton.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_editorFragment)
        }


        return binding.root
    }
}