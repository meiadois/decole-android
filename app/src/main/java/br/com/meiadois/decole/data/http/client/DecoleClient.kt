package br.com.meiadois.decole.data.http.client

import br.com.meiadois.decole.data.http.request.LoginRequest
import br.com.meiadois.decole.data.http.request.RegisterRequest
import br.com.meiadois.decole.data.http.response.LoginResponse
import br.com.meiadois.decole.data.http.response.RegisterResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface DecoleClient {
    @Headers("Content-Type: application/json")
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @Headers("Content-Type: application/json")
    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    companion object {
        operator fun invoke(): DecoleClient {
            return Retrofit.Builder()
                .baseUrl("https://decoleapi.herokuapp.com/v1/")
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(DecoleClient::class.java)
        }
    }
}