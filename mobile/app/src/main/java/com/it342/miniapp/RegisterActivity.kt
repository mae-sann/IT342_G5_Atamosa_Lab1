package com.it342.miniapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.it342.miniapp.api.RetrofitClient
import com.it342.miniapp.models.RegisterRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : Activity() {

    private lateinit var etFirstName: EditText
    private lateinit var etLastName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var tvLogin: TextView
    private lateinit var tvMessage: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize views
        etFirstName = findViewById(R.id.etFirstName)
        etLastName = findViewById(R.id.etLastName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnRegister = findViewById(R.id.btnRegister)
        tvLogin = findViewById(R.id.tvLogin)
        tvMessage = findViewById(R.id.tvMessage)
        progressBar = findViewById(R.id.progressBar)

        // Set click listeners
        btnRegister.setOnClickListener {
            performRegister()
        }

        tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun performRegister() {
        val firstName = etFirstName.text.toString().trim()
        val lastName = etLastName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        // Validate
        if (firstName.isEmpty()) {
            etFirstName.error = "First name required"
            etFirstName.requestFocus()
            return
        }

        if (lastName.isEmpty()) {
            etLastName.error = "Last name required"
            etLastName.requestFocus()
            return
        }

        if (email.isEmpty()) {
            etEmail.error = "Email required"
            etEmail.requestFocus()
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Invalid email"
            etEmail.requestFocus()
            return
        }

        if (password.length < 6) {
            etPassword.error = "Password must be at least 6 characters"
            etPassword.requestFocus()
            return
        }

        // Show progress
        progressBar.visibility = View.VISIBLE
        btnRegister.isEnabled = false
        tvMessage.visibility = View.GONE

        // Make API call
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.register(
                    RegisterRequest(email, password, firstName, lastName)
                )

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    btnRegister.isEnabled = true

                    if (response.isSuccessful) {
                        response.body()?.let { apiResponse ->
                            if (apiResponse.success) {
                                tvMessage.text = apiResponse.message
                                tvMessage.setTextColor(android.graphics.Color.GREEN)
                                tvMessage.visibility = View.VISIBLE

                                // Clear form
                                etFirstName.text.clear()
                                etLastName.text.clear()
                                etEmail.text.clear()
                                etPassword.text.clear()

                                // Go to login after 2 seconds
                                etEmail.postDelayed({
                                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                                    finish()
                                }, 2000)
                            } else {
                                tvMessage.text = apiResponse.message
                                tvMessage.setTextColor(android.graphics.Color.RED)
                                tvMessage.visibility = View.VISIBLE
                            }
                        }
                    } else {
                        tvMessage.text = "Registration failed"
                        tvMessage.setTextColor(android.graphics.Color.RED)
                        tvMessage.visibility = View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    btnRegister.isEnabled = true
                    tvMessage.text = "Network error: ${e.message}"
                    tvMessage.setTextColor(android.graphics.Color.RED)
                    tvMessage.visibility = View.VISIBLE
                }
            }
        }
    }
}