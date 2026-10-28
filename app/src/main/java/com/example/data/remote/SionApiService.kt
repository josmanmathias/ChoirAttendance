package com.example.data.remote

import com.example.data.model.AttendanceRecord
import com.example.data.model.User
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

interface SionApiService {

    @FormUrlEncoded
    @POST("jki/ministry/choir/api/login.php")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    @POST("jki/ministry/choir/api/absensi.php")
    suspend fun submitAttendance(
        @Body record: AttendanceRecord
    ): Response<AttendanceApiResponse>

    @GET("jki/ministry/choir/api/rekap.php")
    suspend fun getAttendanceHistory(): Response<List<AttendanceRecord>>
}

data class LoginResponse(
    val status: Boolean,
    val message: String,
    val user: User?
)

data class AttendanceApiResponse(
    val status: Boolean,
    val message: String,
    val id: Int?
)

object NetworkClient {
    private const val BASE_URL = "https://terbangcirebon.sch.id/"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    val apiService: SionApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(SionApiService::class.java)
    }
}
