package ru.netology.kotlin_for_android_hw_1

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import ru.netology.kotlin_for_android_hw_1.databinding.ActivityAppBinding

class AppActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val binding = ActivityAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent?.let {
            if(it.action!=Intent.ACTION_SEND) return@let

            findNavController(R.id.fragment_container).navigate(R.id.action_mainFragment_to_editorFragment)
        }


    }
}