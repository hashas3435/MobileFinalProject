package com.example.firstapplication

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import com.example.firstapplication.base.Constants
import com.example.firstapplication.databinding.FragmentRegisterBinding
import com.example.firstapplication.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterFragment : Fragment() {
    private var binding: FragmentRegisterBinding? = null

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val binding = FragmentRegisterBinding.inflate(inflater, container, false)
        this.binding = binding

        binding.registerButton.setOnClickListener {
            registerUser()
        }
        binding.signInLayout.setOnClickListener {
            findNavController(binding.root).popBackStack()
        }

        return binding.root
    }

    private fun getBinding(): FragmentRegisterBinding {
        return binding ?: throw IllegalStateException("AuctionRoomFragment binding is null")

    }

    private fun registerUser() {
        val binding = getBinding()

        val fullName = binding.fullName.text.toString().trim()
        val email = binding.email.text.toString().trim()
        val phone = binding.phone.text.toString().trim()
        val password = binding.password.text.toString().trim()
        val confirmPassword = binding.confirmPassword.text.toString().trim()

        if (!checkInput(fullName, email, phone, password, confirmPassword)) {
            return
        }

        binding.progressBar.visibility = View.VISIBLE

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userMap = mapOf(
                        "fullName" to fullName,
                        "email" to email,
                        "phone" to phone,
                        "createdAt" to System.currentTimeMillis()
                    )
                    UserModel.shared.createUser(userMap, user?.uid ?: email) { userId ->
                        if (!userId.isNullOrBlank()) {
                            Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT)
                                .show()

                            val intent = Intent(activity, MainActivity::class.java)
                            startActivity(intent)
                            activity?.finishAffinity()
                        } else {
                            Toast.makeText(
                                context, "Failed to Register",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        binding.progressBar.visibility = View.GONE
                    }
                } else {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(context, "${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun checkInput(
        fullName: String,
        email: String,
        phone: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        val isValidFullName = validateFullName(fullName)
        val isValidEmail = validateEmail(email)
        val isValidPhone = validatePhone(phone)
        val isValidPassword = validatePassword(password)
        val isValidConfirmPassword = validateConfirmPassword(password, confirmPassword)

        return isValidFullName && isValidEmail && isValidPhone && isValidPassword && isValidConfirmPassword
    }

    private fun validateFullName(fullName: String): Boolean {
        val binding = getBinding()
        var error: String? = null

        if (TextUtils.isEmpty(fullName)) {
            error = "Full name is required"
        }

        binding.fullNameLayout.error = error
        return error === null
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
        return email.matches(emailPattern.toRegex())
    }

    private fun validateEmail(email: String): Boolean {
        val binding = getBinding()
        var error: String? = null

        if (TextUtils.isEmpty(email)) {
            error = "Required"
        } else if (!isValidEmail(email)) {
            error = "Enter a valid email address"
        }

        binding.emailLayout.error = error
        return error === null
    }

    private fun isValidPhoneNumber(phone: String): Boolean {
        val regex = Regex("^\\+?[1-9][0-9]{7,14}$")
        return phone.matches(regex)
    }

    private fun validatePhone(phone: String): Boolean {
        val binding = getBinding()
        var error: String? = null

        if (TextUtils.isEmpty(phone)) {
            error = "Phone is required"
        } else if (!isValidPhoneNumber(phone)) {
            error = "Enter a valid phone"
        }

        binding.phoneLayout.error = error
        return error === null
    }

    private fun validatePassword(password: String): Boolean {
        val binding = getBinding()
        var error: String? = null
        val minLength = Constants.LOGIN_VALIDATION.MIN_PASSWORD_LENGTH

        if (TextUtils.isEmpty(password)) {
            error = "Required"
        } else if (password.length < minLength) {
            error = "Password must be at least $minLength characters"
        }

        binding.passwordLayout.error = error
        return error === null
    }

    private fun validateConfirmPassword(password: String, confirmPassword: String): Boolean {
        val binding = getBinding()
        var error: String? = null

        if (TextUtils.isEmpty(confirmPassword)) {
            error = "Password confirm is required"
        } else if (password != confirmPassword) {
            error = "Passwords do not match"
        }

        binding.confirmPasswordLayout.error = error
        return error === null
    }
}