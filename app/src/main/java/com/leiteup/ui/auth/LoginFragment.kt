package com.leiteup.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.leiteup.R
import com.leiteup.databinding.FragmentLoginBinding
import com.leiteup.helper.FirebaseHelper

class LoginFragment : Fragment() {


    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        initClicks()
    }

    private fun initClicks() {

        binding.btnLogin.setOnClickListener {
            validateData()
        }

        binding.btnRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.btnRecover.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_recoverAccountFragment)
        }

    }

    private fun validateData() {
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()

        if(email.isNotEmpty()) {
            if(password.isNotEmpty()) {
                binding.progressBar.isVisible = true;

                loginUser(email, password)

            } else {
                Toast.makeText(requireContext(), "Informe sua senha.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Informe seu e-mail.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (isAdded && _binding != null) { // Garante que o fragmento está ativo
                    if (task.isSuccessful) {
                        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            FirebaseHelper.validError(task.exception?.message ?: ""),
                            Toast.LENGTH_SHORT
                        ).show()

                        Log.i("LOGIN_ERROR", "loginUser: ${task.exception?.message}")

                        binding.progressBar.isVisible = false
                    }
                }
            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}