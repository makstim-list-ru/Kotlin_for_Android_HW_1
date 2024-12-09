package ru.netology.kotlin_for_android_hw_1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import ru.netology.kotlin_for_android_hw_1.databinding.ActivityMain2Binding

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.content2.setText(intent?.getStringExtra(Intent.EXTRA_TEXT))

        binding.ok.setOnClickListener {
            val text = binding.content2.text.toString()
            if (text.isBlank()) {
                setResult(RESULT_CANCELED, null)
            } else {
                val intent = Intent().apply {
                    putExtra(Intent.EXTRA_TEXT, text)
                }
                setResult(RESULT_OK, intent)
            }
            finish()
        }
    }
}

object ContractMainActivity2 : ActivityResultContract<String?, String?>() {
    override fun createIntent(context: Context, input: String?): Intent {
        return Intent(context, MainActivity2::class.java).apply {
            putExtra(Intent.EXTRA_TEXT, input)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): String? {
        return intent?.getStringExtra(Intent.EXTRA_TEXT)
    }

}