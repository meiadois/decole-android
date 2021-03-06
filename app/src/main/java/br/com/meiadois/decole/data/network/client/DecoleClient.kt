package br.com.meiadois.decole.data.network.client

import br.com.meiadois.decole.data.network.NetworkConnectionInterceptor
import br.com.meiadois.decole.data.network.RequestInterceptor
import br.com.meiadois.decole.data.network.request.*
import br.com.meiadois.decole.data.network.response.*
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
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
        @Part("name") name: String,
        @Part("cep") cep: String,
        @Part("cnpj") cnpj: String,
        @Part("description") description: String,
        @Part("segment_id") segmentId: Int,
        @Part("cellphone") cellphone: String,
        @Part("email") email: String,
        @Part("visible") visible: Boolean,
        @Part("city") city: String,
        @Part("neighborhood") neighborhood: String,
        @Part thumbnail: MultipartBody.Part?,
        @Part banner: MultipartBody.Part?
    ): Response<CompanyResponse>

    @Multipart
    @POST("me/companies")
    suspend fun insertUserCompany(
        @Part("name") name: String,
        @Part("cep") cep: String,
        @Part("cnpj") cnpj: String,
        @Part("description") description: String,
        @Part("segment_id") segmentId: Int,
        @Part("cellphone") cellphone: String,
        @Part("email") email: String,
        @Part("visible") visible: Boolean,
        @Part("city") city: String,
        @Part("neighborhood") neighborhood: String,
        @Part thumbnail: MultipartBody.Part,
        @Part banner: MultipartBody.Part
    ): Response<CompanyResponse>

    @Headers("Content-Type: application/json")
    @GET("me/companies/_/search")
    suspend fun getAllCompanies(): Response<List<CompanySearchResponse>>

    @Headers("Content-Type: application/json")
    @GET("me/companies/_/search")
    suspend fun getCompaniesBySegment(@Query("segment_id") segmentId: Int): Response<List<CompanySearchResponse>>

    @Headers("Content-Type: application/json")
    @GET("companies/{id}")
    suspend fun getCompanyById(@Path("id") companyId: Int): Response<CompanyResponse>

    @Headers("Content-Type: application/json")
    @POST("likes")
    suspend fun sendLike(@Body request: LikeSenderRequest): Response<LikePutResponse>

    @Headers("Content-Type: application/json")
    @GET("me/likes")
    suspend fun getUserMatches(@Query("status") status :String = "accepted"): Response<List<LikeResponse>>

    @Headers("Content-Type: application/json")
    @GET("me/likes/sent")
    suspend fun getUserSentLikes(@Query("status") status :String = "pending"): Response<List<LikeSentResponse>>

    @Headers("Content-Type: application/json")
    @GET("me/likes/received")
    suspend fun getUserReceivedLikes(@Query("status") status :String = "pending"): Response<List<LikeReceivedResponse>>

    @Headers("Content-Type: application/json")
    @PUT("likes/{id}")
    suspend fun updateLike(@Path("id") likeId: Int, @Body request: LikeRequest): Response<LikePutResponse>

    @Headers("Content-Type: application/json")
    @DELETE("likes/{id}")
    suspend fun deleteLike(@Path("id") likeId: Int): Response<Void>

    @Headers("Content-Type: application/json")
    @GET("segments")
    suspend fun getAllSegments(): Response<List<SegmentResponse>>

    @Headers("Content-Type: application/json")
    @GET("me/segments/_/has-companies")
    suspend fun getAllSegmentsHasCompanies(): Response<List<SegmentResponse>>

    @Headers("Content-Type: application/json")
    @POST("me/introduce")
    suspend fun introduce(): Response<IntroduceResponse>

    @Headers("Content-Type: application/json")
    @GET("ceps/{cep_number}")
    suspend fun getCep(@Path("cep_number") cep: String): Response<CepResponse>

    @Headers("Content-Type: application/json")
    @PUT("me")
    suspend fun updateUser(@Body request: UserUpdateRequest): Response<UserUpdateResponse>

    @Headers("Content-Type: application/json")
    @PUT("me/change_password")
    // TODO: check if route and request and response types match with required
    suspend fun changeUserPassword(@Body request: UserChangePasswordRequest): Response<Unit>

    @Headers("Content-Type: application/json")
    @POST("generate_reset_password")
    suspend fun sendPwResetEmail(@Body request: PwResetEmailRequest): Response<PwResetEmailResponse>

    @Headers("Content-Type: application/json")
    @POST("verify_token")
    suspend fun validatePwResetToken(@Body request: ValidatePwResetCodeRequest): Response<ValidatePwResetCodeResponse>

    @Headers("Content-Type: application/json")
    @POST("forgot_password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<Unit>

    @Headers("Content-Type: application/json")
    @GET("me/accounts")
    suspend fun getUserAccounts(): Response<List<AccountResponse>>

    @Headers("Content-Type: application/json")
    @GET("me/metrics/Instagram")
    suspend fun getUserMetrics(): Response<AnalyticsResponse>

    @Headers("Content-Type: application/json")
    @POST("me/accounts")
    suspend fun insertUserAccount(@Body request: AccountRequest): Response<AccountResponse>

    @Headers("Content-Type: application/json")
    @PUT("me/accounts/{channel_name}")
    suspend fun updateUserAccount(@Path("channel_name") channelName: String, @Body request: AccountRequest): Response<AccountResponse>

    @Headers("Content-Type: application/json")
    @DELETE("me/accounts/{channel_name}")
    suspend fun deleteUserAccount(@Path("channel_name") channelName: String): Response<Void>

    @Headers("Content-Type: application/json")
    @GET("infos")
    suspend fun getAppInfo(): Response<AppInfoResponse>

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
                .baseUrl("https://api.decole.app/V1/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(DecoleClient::class.java)
        }
    }
}