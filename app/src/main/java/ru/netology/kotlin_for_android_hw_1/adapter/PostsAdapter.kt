package ru.netology.kotlin_for_android_hw_1.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.kotlin_for_android_hw_1.R
import ru.netology.kotlin_for_android_hw_1.databinding.PostCardBinding
import ru.netology.kotlin_for_android_hw_1.dto.Post
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

class PostViewHolder(val binding: PostCardBinding, private val callback: (Post, String) -> Unit) :
    RecyclerView.ViewHolder(binding.root) {
    fun onBindPost(post: Post) {
        with(binding) {
            author1.text = post.author
            published1.text = post.published
            content1.text = post.content
            imageButtonHeart1.text = getFormatedNumber(post.likesNum)
            imageButtonHeart1.setCompoundDrawablesWithIntrinsicBounds(
                if (!post.likedByMe) {
                    root.context.getResources()
                        .getDrawable(R.drawable.outline_favorite_border_24)
                } else {
                    root.context.getResources()
                        .getDrawable(R.drawable.baseline_favorite_24)
                }, null, null, null
            )
            imageButtonShare1.text = getFormatedNumber(post.sharesNum)
            imageButtonView1.text = getFormatedNumber(post.seenNum)

            imageButtonHeart1.setOnClickListener {
                callback(post, "like")
                //viewModel.likeVM(post.id)
            }

            imageButtonShare1.setOnClickListener {
                callback(post, "share")
                //viewModel.shareVM(post.id)
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



