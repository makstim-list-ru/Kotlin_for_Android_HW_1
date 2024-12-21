package ru.netology.kotlin_for_android_hw_1.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.kotlin_for_android_hw_1.R
import ru.netology.kotlin_for_android_hw_1.databinding.PostCardBinding
import ru.netology.kotlin_for_android_hw_1.dto.Post
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.pow

class PostsAdapter(private val callback: (Post, String) -> Unit) :
    ListAdapter<Post, PostViewHolder>(PostDiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = PostCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, callback)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {

        val post = getItem(position)
        holder.onBindPost(post)
    }
}

object PostDiffUtil : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}

class PostViewHolder(private val binding: PostCardBinding, private val callback: (Post, String) -> Unit) :
    RecyclerView.ViewHolder(binding.root) {
    fun onBindPost(post: Post) {
        with(binding) {
            author1.text = post.author
            published1.text = post.published
            content1.text = post.content

            if (post.video != "") {
                videoButton.text = post.video
                videoButton.visibility = View.VISIBLE
            } else
                videoButton.visibility = View.GONE

            videoButton.setOnClickListener{
                callback(post, "video")
            }

            imageButtonHeart1.text = getFormatedNumber(post.likesNum)
            imageButtonHeart1.isChecked = post.likedByMe

            imageButtonShare1.text = getFormatedNumber(post.sharesNum)
            imageButtonView1.text = getFormatedNumber(post.seenNum)

            imageButtonHeart1.setOnClickListener {
                callback(post, "like")
            }

            imageButtonShare1.setOnClickListener {
                callback(post, "share")
            }

            author1.setOnClickListener{
                callback(post, "post")
            }
            published1.setOnClickListener{
                callback(post, "post")
            }
            content1.setOnClickListener{
                callback(post, "post")
            }

            iButton1.setOnClickListener { view ->
                val pum = PopupMenu(view.context, view)
                pum.inflate(R.menu.menu_options)
                pum.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.remove -> {
                            callback(post, "remove")
                            true
                        }

                        R.id.edit -> {
                            callback(post, "edit")
                            true
                        }

                        else -> false
                    }
                }
                pum.show()
            }
        }
    }

    private fun getFormatedNumber(count: Long): String {
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
}



