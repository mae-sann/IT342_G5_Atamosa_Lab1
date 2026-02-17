package com.it342.miniapp

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.it342.miniapp.api.RetrofitClient
import com.it342.miniapp.models.User
import com.it342.miniapp.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardActivity : Activity() {

    private lateinit var tvAvatar: TextView
    private lateinit var tvFullName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvUserId: TextView
    private lateinit var tvMemberSince: TextView
    private lateinit var btnLogout: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Initialize views
        tvAvatar = findViewById(R.id.tvAvatar)
        tvFullName = findViewById(R.id.tvFullName)
        tvEmail = findViewById(R.id.tvEmail)
        tvUserId = findViewById(R.id.tvUserId)
        tvMemberSince = findViewById(R.id.tvMemberSince)
        btnLogout = findViewById(R.id.btnLogout)
        progressBar = findViewById(R.id.progressBar)

        sessionManager = SessionManager(this)

        // Check if logged in
        if (!sessionManager.isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Set logout click listener - NOW SHOWS CONFIRMATION DIALOG
        btnLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        // Load user data
        loadUserData()
    }

    @Suppress("SetTextI18n")
    private fun loadUserData() {
        // Show basic info from session first
        val firstName = sessionManager.getUserFirstName() ?: ""
        val lastName = sessionManager.getUserLastName() ?: ""
        val email = sessionManager.getUserEmail() ?: ""

        tvFullName.text = "$firstName $lastName"
        tvEmail.text = email

        val firstChar = if (firstName.isNotEmpty()) firstName[0].toString() else ""
        val lastChar = if (lastName.isNotEmpty()) lastName[0].toString() else ""
        tvAvatar.text = firstChar + lastChar

        // Get full data from API
        val token = sessionManager.getToken()
        if (token != null) {
            progressBar.visibility = View.VISIBLE

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = RetrofitClient.apiService.getCurrentUser("Bearer $token")

                    withContext(Dispatchers.Main) {
                        progressBar.visibility = View.GONE

                        if (response.isSuccessful) {
                            response.body()?.let { user ->
                                updateUI(user)
                            }
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }

    @Suppress("SetTextI18n")
    private fun updateUI(user: User) {
        tvFullName.text = "${user.firstName} ${user.lastName}"
        tvEmail.text = user.email

        val firstChar = if (user.firstName.isNotEmpty()) user.firstName[0].toString() else ""
        val lastChar = if (user.lastName.isNotEmpty()) user.lastName[0].toString() else ""
        tvAvatar.text = firstChar + lastChar

        tvUserId.text = "User ID: ${user.id ?: "N/A"}"

        user.createdAt?.let {
            val dateStr = if (it.length >= 10) it.substring(0, 10) else it
            tvMemberSince.text = "Member since: $dateStr"
        }
    }

    private fun showLogoutConfirmationDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_logout_confirmation, null)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        // Explicitly use android.widget.Button to avoid ambiguity
        val btnConfirm = dialogView.findViewById<android.widget.Button>(R.id.btnConfirm)
        val btnCancel = dialogView.findViewById<android.widget.Button>(R.id.btnCancel)

        btnConfirm.setOnClickListener {
            dialog.dismiss()
            performLogout()
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun performLogout() {
        val token = sessionManager.getToken()

        progressBar.visibility = View.VISIBLE
        btnLogout.isEnabled = false

        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (token != null) {
                    RetrofitClient.apiService.logout("Bearer $token")
                }
            } catch (e: Exception) {
                // Ignore errors on logout
            } finally {
                withContext(Dispatchers.Main) {
                    sessionManager.clearSession()
                    startActivity(Intent(this@DashboardActivity, LoginActivity::class.java))
                    finish()
                }
            }
        }
    }
}