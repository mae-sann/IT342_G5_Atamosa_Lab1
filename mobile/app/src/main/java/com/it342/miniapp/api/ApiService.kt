package com.it342.miniapp.api

import com.it342.miniapp.models.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<ApiResponse>

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    @GET("api/user/me")
    suspend fun getCurrentUser(
        @Header("Authorization") token: String
    ): Response<User>

    @POST("api/auth/logout")
    suspend fun logout(
        @Header("Authorization") token: String
    ): Response<ApiResponse>
}