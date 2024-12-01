package ru.netology.kotlin_for_android_hw_1.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.netology.kotlin_for_android_hw_1.R
import ru.netology.kotlin_for_android_hw_1.databinding.PostCardBinding
import ru.netology.kotlin_for_android_hw_1.dto.Post
import kotlin.math.ln
import kotlin.math.pow

class PostsAdapter(val callback: (Post, String) -> Unit) : RecyclerView.Adapter<PostViewHolder>() {

    var list = emptyList<Post>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = PostCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = list[position]
        with(holder.getBinding()) {
            this.author1.text = post.author
            this.published1.text = post.published
            this.content1.text = post.content
            this.imageButtonHeart1.text = getFormatedNumber(post.likesNum)
            this.imageButtonHeart1.setCompoundDrawablesWithIntrinsicBounds(
                if (!post.likedByMe) {
                    holder.getBinding().root.context.getResources()
                        .getDrawable(R.drawable.outline_favorite_border_24)
                } else {
                    holder.getBinding().root.context.getResources()
                        .getDrawable(R.drawable.baseline_favorite_24)
                }, null, null, null
            )
            this.imageButtonShare1.text = getFormatedNumber(post.sharesNum)
            this.imageButtonView1.text = getFormatedNumber(post.seenNum)


            this.imageButtonHeart1.setOnClickListener {
                callback(post, "like")
                //viewModel.likeVM(post.id)
            }

            this.imageButtonShare1.setOnClickListener {
                callback(post, "share")
                //viewModel.shareVM(post.id)
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

class PostViewHolder(private val binding: PostCardBinding) : RecyclerView.ViewHolder(binding.root) {
    fun getBinding() = binding
}