package ru.netology.kotlin_for_android_hw_1

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.netology.kotlin_for_android_hw_1.adapter.PostsAdapter
import ru.netology.kotlin_for_android_hw_1.auth.AppAuthorization
import ru.netology.kotlin_for_android_hw_1.databinding.FragmentMainBinding
import ru.netology.kotlin_for_android_hw_1.viewmodel.AuthViewModel
import ru.netology.kotlin_for_android_hw_1.viewmodel.PostViewModel

class MainFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
                    Bundle().apply { this.putString("TEXT_TRANSFER", post.id.toString()) })
                viewModel.editVM(post)
            }
            if (key == "cancel") viewModel.cancelEditVM()
            if (key == "post") {
                findNavController().navigate(R.id.action_mainFragment_to_focusFragment,
                    Bundle().apply { this.putString("TEXT_TRANSFER", post.id.toString()) })
            }
        }
        binding.container.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) { posts ->
            binding.emptyText.isVisible = posts.isNullOrEmpty()
            val newPostFlag = adapter.currentList.size < posts.size
            adapter.submitList(posts) {
                if (newPostFlag) {
                    var position = adapter.currentList.size
                    if (position > 1) position--
                    binding.container.scrollToPosition(position)
                }
            }
        }

        viewModel.dataServerStatus.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading
            binding.errorGroup.isVisible = state.error
            binding.emptyText.isVisible = state.empty
            binding.swipeRefresh.isRefreshing = state.refreshing
            if (state.error) Toast.makeText(
                activity,
                "Error IO with the Server, please, try again!",
                Toast.LENGTH_LONG
            ).show()
        }

        viewModel.newerCount.observe(viewLifecycleOwner) {
            println(it)
            binding.newerPostsButton.isVisible = it > 0
        }

        binding.plusButton.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_editorFragment)
        }

        binding.retryButton.setOnClickListener {
            println("Button <retry> pressed")
            viewModel.loadAllPostsVM()
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadAllPostsVM()
        }

        binding.newerPostsButton.setOnClickListener {
            binding.newerPostsButton.isVisible = false
            viewModel.loadNewerVM()
        }


        binding.toolbarAuth.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {

                val authViewModel: AuthViewModel by viewModels()

                menuInflater.inflate(R.menu.menu_main, menu)
                println("INFO toolbarMain's menu is inflated")

                authViewModel.data.flowWithLifecycle(lifecycle).onEach {
                    menu.setGroupVisible(R.id.unauthenticated, !authViewModel.authenticated)
                    menu.setGroupVisible(R.id.authenticated, authViewModel.authenticated)
                }
                    .launchIn(lifecycleScope)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                println("INFO toolbarEditor item selected $menuItem")
                when (menuItem.itemId) {
                    R.id.signin -> {
                        // TODO: just hardcode it, implementation must be in homework
                        AppAuthorization.getInstance().setAuth(5, "x-token")
                        return true
                    }

                    R.id.signup -> {
                        // TODO: just hardcode it, implementation must be in homework
                        AppAuthorization.getInstance().setAuth(5, "x-token")
                        return true
                    }

                    R.id.signout -> {
                        // TODO: just hardcode it, implementation must be in homework
                        AppAuthorization.getInstance().removeAuth()
                        return true
                    }

                    else -> return false
                }
            }
        }, viewLifecycleOwner)

        return binding.root
    }
}