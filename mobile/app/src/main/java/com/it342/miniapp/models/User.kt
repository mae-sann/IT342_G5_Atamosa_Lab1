package com.it342.miniapp.models

data class User(
    val id: Long? = null,
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val createdAt: String? = null
)