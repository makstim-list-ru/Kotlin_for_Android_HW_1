package ru.netology.kotlin_for_android_hw_1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    ): View? {
        val binding= FragmentEditorBinding.inflate(inflater,container,false)

        val viewModel by viewModels<PostViewModel>(ownerProducer = ::requireParentFragment)

        binding.content2.requestFocus()

        binding.content2.setText(arguments?.getString("TEXT_TRANSFER"))

        binding.ok.setOnClickListener {
            val text = binding.content2.text.toString()
            if (text.isNotBlank()) {
                viewModel.saveVM(text)
            } else {
                viewModel.cancelVM()
            }
            findNavController().navigateUp()
        }
        return binding.root
    }
}