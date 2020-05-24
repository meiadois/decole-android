package br.com.meiadois.decole.data.network.client

import br.com.meiadois.decole.data.network.NetworkConnectionInterceptor
import br.com.meiadois.decole.data.network.RequestInterceptor
import br.com.meiadois.decole.data.network.request.LikeRequest
import br.com.meiadois.decole.data.network.request.LoginRequest
import br.com.meiadois.decole.data.network.request.RegisterRequest
import br.com.meiadois.decole.data.network.request.UserUpdateRequest
import br.com.meiadois.decole.data.network.response.*
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

interface DecoleClient {
    @Headers("Content-Type: application/json")
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @Headers("Content-Type: application/json")
    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @Headers("Content-Type: application/json")
    @GET("me/routes")
    suspend fun routes(): Response<List<RouteDTO>>

    @Headers("Content-Type: application/json")
    @GET("me/routes/{id}")
    suspend fun route(@Path("id") id: Long): Response<RouteDTO>

    @Headers("Content-Type: application/json")
    @GET("me/routes/{id}")
    suspend fun routeDetails(@Path("id") id: Long): Response<RouteDetailsResponse>

    @Headers("Content-Type: application/json")
    @GET("steps/lessons/{id}")
    suspend fun steps(@Path("id") lessonId: Long): Response<List<StepDTO>>

    @Headers("Content-Type: application/json")
    @POST("me/done_routes/{id}")
    suspend fun jumpRoute(@Path("id") routeId: Long): Response<RouteJumpResponse>

    @Headers("Content-Type: application/json")
    @POST("me/done_lessons/{id}")
    suspend fun completeLesson(@Path("id") lessonId: Long): Response<CompleteLessonResponse>

    @Headers("Content-Type: application/json")
    @GET("me/companies")
    suspend fun getUserCompany(): Response<CompanyResponse>

    @Multipart
    @PUT("me/companies")
    suspend fun updateUserCompany(
        @Part("name") name: RequestBody,
        @Part("cep") cep: RequestBody,
        @Part("cnpj") cnpj: RequestBody,
        @Part("description") description: RequestBody,
        @Part("segmentId") segmentId: RequestBody,
        @Part("cellphone") cellphone: RequestBody,
        @Part("email") email: RequestBody,
        @Part("visible") visible: RequestBody,
        @Part("city") city: RequestBody,
        @Part("neighborhood") neighborhood: RequestBody,
        @Part thumbnail: MultipartBody.Part,
        @Part banner: MultipartBody.Part
    ): Response<CompanyResponse>

    @Multipart
    @POST("me/companies")
    suspend fun insertUserCompany(
        @Part("name") name: RequestBody,
        @Part("cep") cep: RequestBody,
        @Part("cnpj") cnpj: RequestBody,
        @Part("description") description: RequestBody,
        @Part("segmentId") segmentId: RequestBody,
        @Part("cellphone") cellphone: RequestBody,
        @Part("email") email: RequestBody,
        @Part("visible") visible: RequestBody,
        @Part("city") city: RequestBody,
        @Part("neighborhood") neighborhood: RequestBody,
        @Part thumbnail: MultipartBody.Part,
        @Part banner: MultipartBody.Part
    ): Response<CompanyResponse>

    @Headers("Content-Type: application/json")
    @GET("companies/{id}")
    suspend fun getCompanyById(@Path("id") companyId: Int): Response<CompanyResponse>

    @Headers("Content-Type: application/json")
    @GET("me/likes")
    suspend fun listUserMatches(@Query("status") status :String = "accepted"): Response<List<LikeResponse>>

    @Headers("Content-Type: application/json")
    @GET("segments")
    suspend fun getAllSegments(): Response<List<SegmentResponse>>

    @Headers("Content-Type: application/json")
    @PUT("likes/{id}")
    suspend fun undoPartnership(@Path("id") likeId: Int, @Body request: LikeRequest): Response<LikePutResponse>

    @Headers("Content-Type: application/json")
    @POST("me/introduce")
    suspend fun introduce(): Response<IntroduceResponse>

    @Headers("Content-Type: application/json")
    @GET("ceps/{cep_number}")
    suspend fun getCep(@Path("cep_number") cep: String): Response<CepResponse>

    @Headers("Content-Type: application/json")
    @PUT("me")
    suspend fun updateUser(@Body request: UserUpdateRequest): Response<UserUpdateResponse>

    companion object {
        operator fun invoke(
            interceptor: NetworkConnectionInterceptor,
            requestInterceptor: RequestInterceptor
        ): DecoleClient {
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(requestInterceptor)
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