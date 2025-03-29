package ru.netology.kotlin_for_android_hw_1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.kotlin_for_android_hw_1.databinding.FragmentEditorBinding
import ru.netology.kotlin_for_android_hw_1.viewmodel.PostViewModel

class EditorFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentEditorBinding.inflate(inflater, container, false)
        val viewModel by viewModels<PostViewModel>(ownerProducer = ::requireParentFragment)

        binding.content2.requestFocus()
        binding.content2.setText(arguments?.getString("TEXT_TRANSFER"))

        binding.takePhoto.setOnClickListener {
            println("INFO takePhoto pressed")
        }


        binding.pickPhoto.setOnClickListener {
            println("INFO pickPhoto pressed")
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
                            viewModel.cancelVM()
                        }
                        findNavController().navigateUp()
                        return true
                    }

                    android.R.id.home -> {
                        viewModel.cancelVM()
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