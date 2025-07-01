package ru.netology.kotlin_for_android_hw_1.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.kotlin_for_android_hw_1.databinding.ItemLoadingBinding

class PostLoadingStateAdapter (private val retryListener: () -> Unit): LoadStateAdapter<PostLoadingViewHolder>() {
    override fun onBindViewHolder(
        holder: PostLoadingViewHolder,
        loadState: LoadState
    ) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): PostLoadingViewHolder = PostLoadingViewHolder(
        ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        retryListener
    )
}

class PostLoadingViewHolder(
    private val itemLoadingBinding: ItemLoadingBinding,
    private val retryListener: () -> Unit
) :
    RecyclerView.ViewHolder(itemLoadingBinding.root) {

    fun bind(loadState: LoadState) {
        itemLoadingBinding.progressBarHorizontal.isVisible = loadState is LoadState.Loading
        itemLoadingBinding.retryButton.isVisible = loadState is LoadState.Error

        itemLoadingBinding.retryButton.setOnClickListener {
            retryListener()
        }
    }
}