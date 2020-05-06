package br.com.meiadois.decole.data.network.client

import br.com.meiadois.decole.data.network.NetworkConnectionInterceptor
import br.com.meiadois.decole.data.network.request.LoginRequest
import br.com.meiadois.decole.data.network.request.RegisterRequest
import br.com.meiadois.decole.data.network.response.LoginResponse
import br.com.meiadois.decole.data.network.response.RegisterResponse
import br.com.meiadois.decole.data.network.response.RouteDTO
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface DecoleClient {
    @Headers("Content-Type: application/json")
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @Headers("Content-Type: application/json")
    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @Headers("Content-Type: application/json")
    @GET("routes")
    suspend fun routes(): Response<List<RouteDTO>>

    companion object {
        operator fun invoke(interceptor: NetworkConnectionInterceptor): DecoleClient {
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build()

            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("https://decoleapi.herokuapp.com/v1/")
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(DecoleClient::class.java)
        }
    }
}