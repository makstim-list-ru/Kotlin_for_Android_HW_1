package ru.netology.kotlin_for_android_hw_1

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import ru.netology.kotlin_for_android_hw_1.apputils.NetologyUtilities.getFormatedNumber
import ru.netology.kotlin_for_android_hw_1.databinding.FragmentFocusBinding
import ru.netology.kotlin_for_android_hw_1.dto.Post
import ru.netology.kotlin_for_android_hw_1.viewmodel.PostViewModel

class FocusFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentFocusBinding.inflate(inflater, container, false)

        val viewModel by viewModels<PostViewModel>(ownerProducer = ::requireParentFragment)

        val postID = arguments?.getString("TEXT_TRANSFER")?.toLong() ?: return binding.root

        var posts = viewModel.data.value ?: return binding.root
        var post = posts.filter { it.id == postID }[0]

//        onBindPost(post, binding)


        with(binding.include) {

            imageButtonHeart1.setOnClickListener {
                viewModel.likeVM(post.id)
//                posts = viewModel.data.value ?: return@setOnClickListener
//                post = posts.filter { it.id == postID }[0]
//                onBindPost(post, binding)
            }

            imageButtonShare1.setOnClickListener {
                val intent = Intent().apply {
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                    action = Intent.ACTION_SEND
                }
                val shareIntent = Intent.createChooser(intent, "Sharing the post")
                startActivity(shareIntent)
                viewModel.shareVM(post.id)
//                posts = viewModel.data.value ?: return@setOnClickListener
//                post = posts.filter { it.id == postID }[0]
//                onBindPost(post, binding)
            }

            iButton1.setOnClickListener { view ->
                val pum = PopupMenu(view.context, view)
                pum.inflate(R.menu.menu_options)
                pum.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.remove -> {
                            viewModel.removeVM(post.id)
                            findNavController().navigateUp()
                            true
                        }

                        R.id.edit -> {
                            findNavController().navigate(R.id.action_focusFragment_to_editorFragment,
                                Bundle().apply { this.putString("TEXT_TRANSFER", post.content) })
                            viewModel.editVM(post)
                            true
                        }

                        else -> false
                    }
                }
                pum.show()
            }

            val url = "http://10.0.2.2:9999/avatars/${post.authorAvatar}"
            Glide.with(binding.include.imageView1)
                .load(url)
                .placeholder(R.drawable.ic_loading_100dp)
                .error(R.drawable.ic_error_100dp)
                .timeout(10_000)
                .into(binding.include.imageView1)

        }

        viewModel.data.observe(viewLifecycleOwner) {
            posts = viewModel.data.value ?: return@observe
            posts.filter { it.id == postID }.let {
                if (it.isNotEmpty()) {
                    post = it[0]
                    onBindPost(post, binding)
                }
            }
        }

        return binding.root
    }

    private fun onBindPost(post: Post, binding: FragmentFocusBinding) {
        with(binding.include) {
            author1.text = post.author
            published1.text = post.published
            content1.text = post.content

            if (post.video != "") {
                videoButton.text = post.video
                videoButton.visibility = View.VISIBLE
            } else
                videoButton.visibility = View.GONE

            videoButton.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.video))
                startActivity(intent)
            }

            imageButtonHeart1.text = getFormatedNumber(post.likesNum)
            imageButtonHeart1.isChecked = post.likedByMe

            imageButtonShare1.text = getFormatedNumber(post.sharesNum)
            imageButtonView1.text = getFormatedNumber(post.seenNum)
        }
    }
}