package com.example.firstapplication

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class SignInFragment : Fragment() {
    private lateinit var auth: FirebaseAuth

    private var emailField: TextInputEditText? = null
    private var emailLayout: TextInputLayout? = null
    private var passwordField: TextInputEditText? = null
    private var passwordLayout: TextInputLayout? = null
    private var forgotPasswordField: TextView? = null
    private var loginButton: Button? = null
    private var register: TextView? = null
    private var progressBar: ProgressBar? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sign_in, container, false)

        auth = FirebaseAuth.getInstance()

        emailField = view.findViewById(R.id.email)
        emailLayout = view.findViewById(R.id.email_layout)
        passwordField = view.findViewById(R.id.password)
        passwordLayout = view.findViewById(R.id.password_layout)
        forgotPasswordField = view.findViewById(R.id.forgot_password)
        loginButton = view.findViewById(R.id.login_button)
        register = view.findViewById(R.id.register)
        progressBar = view.findViewById(R.id.progress_bar)

        loginButton?.setOnClickListener {
            loginUser()
        }

        register?.setOnClickListener {
            startActivity(Intent(requireContext(), RegisterActivity::class.java))
        }

        forgotPasswordField?.setOnClickListener {
            val email = emailField?.text.toString().trim()
            if (isValidEmail(email)) {
                sendPasswordResetEmail(email)
            } else {
                Toast.makeText(requireContext(), "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun loginUser() {
        val email = emailField?.text.toString().trim()
        val password = passwordField?.text.toString().trim()

        if (!validateForm(email, password)) {
            return
        }

        progressBar?.visibility = View.VISIBLE

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                progressBar?.visibility = View.GONE
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Login successful!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                    requireActivity().finish()
                } else {
                    Toast.makeText(requireContext(), "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun validateForm(email: String, password: String): Boolean {
        var valid = true

        if (TextUtils.isEmpty(email)) {
            emailLayout?.error = "Required"
            valid = false
        } else if (!isValidEmail(email)) {
            emailLayout?.error = "Enter a valid email address"
            valid = false
        } else {
            emailLayout?.error = null
        }

        if (TextUtils.isEmpty(password)) {
            passwordLayout?.error = "Required"
            valid = false
        } else if (password.length < 6) {
            passwordLayout?.error = "Password must be at least 6 characters"
            valid = false
        } else {
            passwordLayout?.error = null
        }

        return valid
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun sendPasswordResetEmail(email: String) {
        progressBar?.visibility = View.VISIBLE

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                progressBar?.visibility = View.GONE
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Password reset email sent", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Failed to send reset email: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
