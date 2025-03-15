package com.example.firstapplication

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {
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
    private var buttonSignIn: TextView? = null
    private var buttonRegister: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()


        fullNameField = findViewById(R.id.full_name)
        fullNameLayout = findViewById(R.id.full_name_layout)
        emailField = findViewById(R.id.email)
        emailLayout = findViewById(R.id.email_layout)
        passwordField = findViewById(R.id.password)
        passwordLayout = findViewById(R.id.password_layout)
        confirmPasswordField = findViewById(R.id.confirm_password)
        confirmPasswordLayout = findViewById(R.id.confirm_password_layout)


        buttonSignIn = findViewById(R.id.sign_in)
        buttonRegister = findViewById(R.id.register_button)

        buttonRegister?.setOnClickListener {
            registerUser()
        }

        buttonSignIn?.setOnClickListener {
            finish()
        }
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
            .addOnCompleteListener(this) { task ->
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
                            Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()

                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finishAffinity()
                        }
                        .addOnFailureListener { e ->
                            progressBar?.visibility = View.GONE
                            Toast.makeText(this, "Error creating profile: ${e.message}",
                                Toast.LENGTH_SHORT).show()
                        }
                } else {
                    progressBar?.visibility = View.GONE

                    Toast.makeText(this, "${task.exception?.message}",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun validateForm(fullName: String, email: String, password: String,
                             confirmPassword: String): Boolean {
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

