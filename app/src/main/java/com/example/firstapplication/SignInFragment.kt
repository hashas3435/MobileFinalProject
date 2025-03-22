package com.example.firstapplication

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import com.example.firstapplication.databinding.FragmentSignInBinding
import com.example.firstapplication.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

const val MIN_PASSWORD_LENGTH = 6

class SignInFragment : Fragment() {
    private var binding: FragmentSignInBinding? = null

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSignInBinding.inflate(inflater, container, false)
        this.binding = binding

        binding.loginButton.setOnClickListener {
            loginUser()
        }
        binding.register.setOnClickListener {
            val action = SignInFragmentDirections.actionSignInFragmentToRegisterFragment()
            findNavController(binding.root).navigate(action)
        }
        binding.forgotPassword.setOnClickListener {
            val email = binding.email.text.toString().trim()
            if (isValidEmail(email)) {
                sendPasswordResetEmail(email)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please enter a valid email address",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        return binding.root
    }

    private fun getBinding(): FragmentSignInBinding {
        return binding ?: throw IllegalStateException("AuctionRoomFragment binding is null")
    }

    private fun loginUser() {
        val binding = getBinding()
        val email = binding.email.text.toString().trim()
        val password = binding.password.text.toString().trim()

        if (!checkInput(email, password)) {
            return
        }

        binding.progressBar.visibility = View.VISIBLE

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    onSuccessfulSignIn(user)
                } else {
                    onFailedSignIn(task.exception)
                }
            }
    }

    private fun onSuccessfulSignIn(authUser: FirebaseUser?) {
        val binding = getBinding()

        if (authUser !== null && authUser.uid.isNotBlank()) {
            UserModel.shared.getUserById(authUser.uid) { userData ->
                UserModel.shared.user = userData
                Toast.makeText(requireContext(), "Login successful!", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE

                startActivity(Intent(requireContext(), MainActivity::class.java))
                requireActivity().finish()
            }
        } else {
            Toast.makeText(requireContext(), "Login failed", Toast.LENGTH_SHORT).show()
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun onFailedSignIn(exception: Exception?) {
        val binding = getBinding()

        binding.progressBar.visibility = View.GONE
        Log.d("SIGN_IN", "Authentication failed: ${exception?.message}")
        Toast.makeText(
            requireContext(),
            "Password and email does not match",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun checkInput(email: String, password: String): Boolean {
        val isValidEmail = validateEmail(email)
        val isValidPassword = validatePassword(password)

        return isValidEmail && isValidPassword
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun validateEmail(email: String): Boolean {
        val binding = getBinding()
        var error: String? = null

        if (TextUtils.isEmpty(email)) {
            error = "Email is required"
        } else if (!isValidEmail(email)) {
            error = "Enter a valid email address"
        }
        binding.emailLayout.error = error

        return error !== null
    }

    private fun validatePassword(password: String): Boolean {
        val binding = getBinding()
        var error: String? = null

        if (TextUtils.isEmpty(password)) {
            error = "Password is required"
        } else if (password.length < MIN_PASSWORD_LENGTH) {
            error = "Password must be at least $MIN_PASSWORD_LENGTH characters"
        }
        binding.passwordLayout.error = error

        return error !== null
    }

    private fun sendPasswordResetEmail(email: String) {
        val binding = getBinding()
        binding.progressBar.visibility = View.VISIBLE

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                binding.progressBar.visibility = View.GONE
                if (task.isSuccessful) {
                    Toast.makeText(
                        requireContext(),
                        "Password reset email sent",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Failed to send reset email: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}
