package ru.netology.kotlin_for_android_hw_1

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.snackbar.Snackbar
import ru.netology.kotlin_for_android_hw_1.databinding.ActivityAppBinding

class AppActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val binding = ActivityAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHostFragment.navController

        intent?.let {
            if (it.action != Intent.ACTION_SEND) return@let

            val text = it.getStringExtra(Intent.EXTRA_TEXT)
            if (text.isNullOrBlank()) {
                Snackbar.make(binding.root, "It can't be empty", Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok) {
                        finish()
                    }.show()
                return@let
            }

//            findNavController(R.id.fragment_container)
//                .navigate(
//                    R.id.action_mainFragment_to_editorFragment,
//                    Bundle().apply { this.putString("TEXT_TRANSFER", text) }
//                )
            navController
                .navigate(
                    R.id.action_mainFragment_to_editorFragment,
                    Bundle().apply { this.putString("TEXT_TRANSFER", text) }
                )

        }


    }
}