package ru.netology.kotlin_for_android_hw_1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.kotlin_for_android_hw_1.databinding.FragmentAuthBinding
import ru.netology.kotlin_for_android_hw_1.viewmodel.LoginViewModel

@AndroidEntryPoint
class AuthFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentAuthBinding.inflate(layoutInflater, container, false)

        val loginViewModel by activityViewModels<LoginViewModel>()

        binding.authName.setText(loginViewModel.data.login)
        binding.authPassword.setText(loginViewModel.data.pass)

        binding.authOK.setOnClickListener {
            val name = binding.authName.text.toString()
            val passw = binding.authPassword.text.toString()
            if (name.isNotBlank() && passw.isNotBlank()) {
                Toast.makeText(
                    context,
                    "AUTHORIZATION in PROGRESS, please WAIT...",
                    Toast.LENGTH_LONG
                ).show()
                loginViewModel.loginVM(name, passw)
//                findNavController().navigateUp()
            } else {
                Toast.makeText(
                    context,
                    "Login and/or Password is empty, please try again",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        loginViewModel.loginFaultFlag.observe(viewLifecycleOwner) { flag ->
            when (flag) {
                true -> {
                    Toast.makeText(
                        context,
                        "Login and/or Password is incorrect, please try again",
                        Toast.LENGTH_LONG
                    ).show()
                    loginViewModel.loginFaultClear()
                }

                false -> {
                    loginViewModel.loginFaultClear()
                    findNavController().navigateUp()
                }

                else -> {}
            }
        }


        return binding.root
    }
}