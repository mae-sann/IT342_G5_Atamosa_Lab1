package com.it342.miniapp.models

data class RegisterRequest(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    val token: String,
    val email: String,
    val firstName: String,
    val lastName: String
)

data class ApiResponse(
    val success: Boolean,
    val message: String,
    val data: Any? = null
)