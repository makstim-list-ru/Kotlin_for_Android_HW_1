package ru.netology.kotlin_for_android_hw_1

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContract
import androidx.fragment.app.Fragment
import ru.netology.kotlin_for_android_hw_1.databinding.FragmentEditorBinding

class EditorFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding= FragmentEditorBinding.inflate(inflater,container,false)


        binding.content2.setText(activity?.intent?.getStringExtra(Intent.EXTRA_TEXT))

        binding.ok.setOnClickListener {
            val text = binding.content2.text.toString()
            if (text.isBlank()) {
                activity?.setResult(Activity.RESULT_CANCELED, null)
            } else {
                val intent = Intent().apply {
                    putExtra(Intent.EXTRA_TEXT, text)
                }
                activity?.setResult(Activity.RESULT_OK, intent)
            }
            activity?.finish()
        }


        return binding.root
    }
}

object ContractMainActivity2 : ActivityResultContract<String?, String?>() {
    override fun createIntent(context: Context, input: String?): Intent {
        return Intent(context, EditorFragment::class.java).apply {
            putExtra(Intent.EXTRA_TEXT, input)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): String? {
        return intent?.getStringExtra(Intent.EXTRA_TEXT)
    }

}