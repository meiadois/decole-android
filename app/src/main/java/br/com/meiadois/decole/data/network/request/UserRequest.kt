package br.com.meiadois.decole.data.network.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserUpdateRequest(
    @field:Json(name = "name") val name: String,
    @field:Json(name = "email") val email: String
)

@JsonClass(generateAdapter = true)
data class UserChangePasswordRequest(
    @field:Json(name = "current_password") val currentPassword: String,
    @field:Json(name = "new_password") val newPassword: String
)