package com.example.firstapplication

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private var fullNameField: TextInputEditText? = null
    private var fullNameLayout: TextInputLayout? = null
    private var emailField: TextInputEditText? = null
    private var emailLayout: TextInputLayout? = null
    private var passwordField: TextInputEditText? = null
    private var passwordLayout: TextInputLayout? = null
    private var confirmPasswordField: TextInputEditText? = null
    private var confirmPasswordLayout: TextInputLayout?= null
    private var progressBar: ProgressBar? = null

    private var buttonSignIn: LinearLayout? = null
    private var buttonRegister: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        fullNameField = view.findViewById(R.id.full_name)
        fullNameLayout = view.findViewById(R.id.full_name_layout)
        emailField = view.findViewById(R.id.email)
        emailLayout = view.findViewById(R.id.email_layout)
        passwordField = view.findViewById(R.id.password)
        passwordLayout = view.findViewById(R.id.password_layout)
        confirmPasswordField = view.findViewById(R.id.confirm_password)
        confirmPasswordLayout = view.findViewById(R.id.confirm_password_layout)

        buttonSignIn = view.findViewById(R.id.sign_in_layout)
        buttonRegister = view.findViewById(R.id.register_button)

        buttonRegister?.setOnClickListener {
            registerUser()
        }

        buttonSignIn?.setOnClickListener {
            findNavController(view).popBackStack()
        }

        return view
    }

    private fun registerUser() {
        val fullName = fullNameField?.text.toString().trim()
        val email = emailField?.text.toString().trim()
        val password = passwordField?.text.toString().trim()
        val confirmPassword = confirmPasswordField?.text.toString().trim()

        if (!validateForm(fullName, email, password, confirmPassword)) {
            return
        }

        progressBar?.visibility = View.VISIBLE

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userMap = hashMapOf(
                        "uid" to user?.uid,
                        "fullName" to fullName,
                        "email" to email,
                        "createdAt" to System.currentTimeMillis()
                    )

                    db.collection("users").document(user?.uid ?: "")
                        .set(userMap)
                        .addOnSuccessListener {
                            progressBar?.visibility = View.GONE
                            Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT).show()

                            val intent = Intent(activity, MainActivity::class.java)
                            startActivity(intent)
                            activity?.finishAffinity()
                        }
                        .addOnFailureListener { e ->
                            progressBar?.visibility = View.GONE
                            Toast.makeText(context, "Error creating profile: ${e.message}",
                                Toast.LENGTH_SHORT).show()
                        }
                } else {
                    progressBar?.visibility = View.GONE
                    Toast.makeText(context, "${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun validateForm(fullName: String, email: String, password: String, confirmPassword: String): Boolean {
        var valid = true

        if (TextUtils.isEmpty(fullName)) {
            fullNameLayout?.error = "Required"
            valid = false
        } else {
            fullNameLayout?.error = null
        }

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

        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordLayout?.error = "Required"
            valid = false
        } else if (password != confirmPassword) {
            confirmPasswordLayout?.error = "Passwords do not match"
            valid = false
        } else {
            confirmPasswordLayout?.error = null
        }

        return valid
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
        return email.matches(emailPattern.toRegex())
    }
}