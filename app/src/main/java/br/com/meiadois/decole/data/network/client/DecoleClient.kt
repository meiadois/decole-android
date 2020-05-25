package br.com.meiadois.decole.data.network.client

import br.com.meiadois.decole.data.network.NetworkConnectionInterceptor
import br.com.meiadois.decole.data.network.RequestInterceptor
import br.com.meiadois.decole.data.network.request.*
import br.com.meiadois.decole.data.network.response.*
import okhttp3.OkHttpClient
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

    @Headers("Content-Type: application/json")
    @GET("companies")
    suspend fun getAllCompanies(): Response<List<CompanyResponse>>

    @Headers("Content-Type: application/json")
    @GET("companies/_/search")
    suspend fun getCompanyBySegment(@Query("segment_id") segmentId: Int): Response<List<CompanySearchResponse>>

    @Headers("Content-Type: application/json")
    @GET("companies/{id}")
    suspend fun getCompanyById(@Path("id") companyId: Int): Response<CompanyResponse>

    @Headers("Content-Type: application/json")
    @POST("likes")
    suspend fun sendLike(@Body request: LikeSenderRequest): Response<LikePutResponse>

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