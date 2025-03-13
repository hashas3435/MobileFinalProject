package com.example.firstapplication

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    private var email: TextInputEditText? = null
    private var emailLayout: TextInputLayout? = null
    private var password: TextInputEditText? = null
    private var passwordLayout: TextInputLayout? = null
    private var forgotPassword: TextView? = null
    private var loginButton: Button? = null
    private var register: TextView? = null
    private var progressBar: ProgressBar? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_in)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        FirebaseApp.initializeApp(this)

        auth = FirebaseAuth.getInstance()

        email = findViewById(R.id.email)
        emailLayout = findViewById(R.id.email_layout)
        password = findViewById(R.id.password)
        passwordLayout = findViewById(R.id.password_layout)
        forgotPassword = findViewById(R.id.forgot_password)
        loginButton = findViewById(R.id.login_button)
        register = findViewById(R.id.register)

        loginButton?.setOnClickListener {
            loginUser()
        }

        register?.setOnClickListener {
            startActivity(Intent(this, Register::class.java))
        }

        forgotPassword?.setOnClickListener {
            val email = email?.text.toString().trim()
            if (isValidEmail(email)) {
                sendPasswordResetEmail(email)
            } else {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginUser() {
        val email = email?.text.toString().trim()
        val password = password?.text.toString().trim()

        if (!validateForm(email, password)) {
            return
        }

        progressBar?.visibility = View.VISIBLE

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                progressBar?.visibility = View.GONE

                if (task.isSuccessful) {
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Authentication failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(this, "Password reset email sent", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to send reset email: ${task.exception?.message}",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
}