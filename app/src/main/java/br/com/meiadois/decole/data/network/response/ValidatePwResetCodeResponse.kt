package br.com.meiadois.decole.data.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ValidatePwResetCodeResponse(
    @Json(name = "isValid")
    val isValid: Boolean
)