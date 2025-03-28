package ru.netology.kotlin_for_android_hw_1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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

//        var fragmentBinding: FragmentEditorBinding? = null

        binding.content2.requestFocus()

        binding.content2.setText(arguments?.getString("TEXT_TRANSFER"))

        binding.ok.setOnClickListener {
            println("binding.ok.setOnClickListener")
//            val text = binding.content2.text.toString()
//            if (text.isNotBlank()) {
//                viewModel.saveVM(text)
//            } else {
//                viewModel.cancelVM()
//            }
//            findNavController().navigateUp()
        }


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


        binding.toolbar.setOnMenuItemClickListener {
            println("binding.toolbar.setOnMenuItemClickListener")
            when (it.itemId) {
                // these ids should match the item ids from my_fragment_menu.xml file
                R.id.save -> {
                    println("OK clicked")

                    // by returning 'true' we're saying that the event
                    // is handled and it shouldn't be propagated further
                    true
                }

                else -> {
                    println("ERROR clicked")
                    false
                }
            }
        }

        return binding.root
    }

}