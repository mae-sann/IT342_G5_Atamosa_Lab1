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
import com.it342.miniapp.models.LoginRequest
import com.it342.miniapp.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : Activity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvRegister: TextView
    private lateinit var tvError: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize views
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvRegister = findViewById(R.id.tvRegister)
        tvError = findViewById(R.id.tvError)
        progressBar = findViewById(R.id.progressBar)

        sessionManager = SessionManager(this)

        // Check if already logged in
        if (sessionManager.isLoggedIn()) {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }

        // Set click listeners
        btnLogin.setOnClickListener {
            performLogin()
        }

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun performLogin() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        // Validate
        if (email.isEmpty()) {
            etEmail.error = "Email required"
            etEmail.requestFocus()
            return
        }

        if (password.isEmpty()) {
            etPassword.error = "Password required"
            etPassword.requestFocus()
            return
        }

        // Show progress
        progressBar.visibility = View.VISIBLE
        btnLogin.isEnabled = false
        tvError.visibility = View.GONE

        // Make API call
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.login(LoginRequest(email, password))

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    btnLogin.isEnabled = true

                    if (response.isSuccessful) {
                        response.body()?.let { authResponse ->
                            // Save session
                            sessionManager.saveAuthResponse(authResponse)

                            // Go to dashboard
                            startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
                            finish()
                        }
                    } else {
                        tvError.text = "Invalid email or password"
                        tvError.visibility = View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    btnLogin.isEnabled = true
                    tvError.text = "Network error: ${e.message}"
                    tvError.visibility = View.VISIBLE
                }
            }
        }
    }
}