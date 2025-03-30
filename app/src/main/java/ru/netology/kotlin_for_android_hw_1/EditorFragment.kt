package ru.netology.kotlin_for_android_hw_1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import ru.netology.kotlin_for_android_hw_1.databinding.FragmentEditorBinding
import ru.netology.kotlin_for_android_hw_1.viewmodel.PostViewModel
import java.io.File

class EditorFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentEditorBinding.inflate(inflater, container, false)
        val viewModel by viewModels<PostViewModel>(ownerProducer = ::requireParentFragment)

        val postID = arguments?.getString("TEXT_TRANSFER")?.toLong()
        val post = postID?.let { viewModel.data.value?.filter { it.id == postID }?.get(0) }
        val urlPost = post?.let { "http://10.0.2.2:9999/media/${post.attachment?.url}" }
        val photoIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
                if (activityResult.resultCode == ImagePicker.RESULT_ERROR) {
                    Toast.makeText(context, "ImagePicker.RESULT_ERROR", Toast.LENGTH_LONG).show()
                    return@registerForActivityResult
                }
                val result = activityResult.data?.data ?: return@registerForActivityResult
                viewModel.changePhotoVM(result, result.toFile())
            }


        binding.content2.setText(post?.content)

        urlPost?.let {
            viewModel.changePhotoVM(
                urlPost.toUri(),
                urlPost.toUri().path?.let { File(it) },
//                urlPost.toUri().toFile()
            )
        }

        binding.content2.requestFocus()

        //binding.content2.setText(arguments?.getString("TEXT_TRANSFER"))

        viewModel.photoLive.observe(viewLifecycleOwner) { photo ->
            if (photo == null) {
                binding.photoContainer.isVisible = false
                return@observe
            } else {
                binding.photoContainer.isVisible = true
            }

            val url = viewModel.photoLive.value?.uri
            Glide.with(binding.photo)
                .load(url)
                .circleCrop()
                .placeholder(R.drawable.ic_loading_100dp)
                .error(R.drawable.ic_error_100dp)
                .timeout(10_000)
                .into(binding.photo)
        }

        binding.removePhoto.setOnClickListener {
            viewModel.removePhotoVM()
        }

        binding.makePhoto.setOnClickListener {
            println("INFO makePhoto pressed")
            ImagePicker.with(this)
                .cameraOnly()
                .crop()                    //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(
                    1080,
                    1080
                )    //Final image resolution will be less than 1080 x 1080(Optional)
                .createIntent { intent ->
                    photoIntentLauncher.launch(intent)
                }
        }

        binding.choosePhoto.setOnClickListener {
            println("INFO choosePhoto pressed")
            ImagePicker.with(this)
                .galleryOnly()
                .crop()
                .compress(1024)
                .maxResultSize(
                    2048,
                    2048
                )
                .galleryMimeTypes(arrayOf("image/ png", "image/ jpeg", "image/ jpg"))
                .createIntent { intent ->
                    photoIntentLauncher.launch(intent)
                }
        }

//        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbarEditor)

        binding.toolbarEditor.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_new_post, menu)
                println("INFO toolbarEditor's menu is inflated")
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                println("INFO toolbarEditor item selected $menuItem")
                when (menuItem.itemId) {
                    R.id.saveInToolbarEditor -> {
                        val text = binding.content2.text.toString()
                        if (text.isNotBlank()) {
                            viewModel.saveVM(text)
                        } else {
                            viewModel.cancelEditVM()
                            viewModel.removePhotoVM()
                        }
                        findNavController().navigateUp()
                        return true
                    }

                    R.id.homeInToolBarEditor -> {
                        viewModel.cancelEditVM()
                        viewModel.removePhotoVM()
                        findNavController().navigateUp()
                        return true
                    }

                    else -> return false
                }
            }
        }, viewLifecycleOwner)

        return binding.root
    }

}


//        binding.ok.setOnClickListener {
//            println("binding.ok.setOnClickListener")
//            val text = binding.content2.text.toString()
//            if (text.isNotBlank()) {
//                viewModel.saveVM(text)
//            } else {
//                viewModel.cancelVM()
//            }
//            findNavController().navigateUp()
//        }


//        requireActivity().addMenuProvider(object : MenuProvider {
//            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
//                menuInflater.inflate(R.menu.menu_new_post, menu)
//            }
//
//            override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
//                when (menuItem.itemId) {
//                    R.id.save -> {
//                        println("OK clicked")
//                        val text = binding.content2.text.toString()
//                        if (text.isNotBlank()) {
//                            viewModel.saveVM(text)
//                        } else {
//                            viewModel.cancelVM()
//                        }
//                        findNavController().navigateUp()
//                        true
//                    }
//
//                    else -> {
//                        println("Not OK clicked")
//                        false
//                    }
//                }
//
//        }, viewLifecycleOwner)
//        val navController = findNavController()
//        binding.toolbar.setupWithNavController(navController)
//
//        val settingsMenuItem = binding.toolbar.menu.add(R.string.nmedia)


//        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)