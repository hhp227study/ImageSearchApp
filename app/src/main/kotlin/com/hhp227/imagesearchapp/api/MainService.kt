package com.hhp227.imagesearchapp.api

import com.hhp227.imagesearchapp.data.SearchResponse
import com.hhp227.imagesearchapp.data.SortOption
import com.hhp227.imagesearchapp.utilities.DEFAULT_PAGE
import com.hhp227.imagesearchapp.utilities.DEFAULT_PAGE_SIZE
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface MainService {
    @GET("v2/search/image")
    suspend fun searchImages(
        @Header("Authorization") authorization: String,
        @Query("query") query: String,
        @Query("sort") sort: SortOption = SortOption.accuracy,
        @Query("page") page: Int = DEFAULT_PAGE,
        @Query("size") size: Int = DEFAULT_PAGE_SIZE
    ): SearchResponse

    companion object {
        private val Json = Json {
            isLenient = true
            ignoreUnknownKeys = true
            coerceInputValues = true
        }

        private const val BASE_URL = "https://dapi.kakao.com/"

        fun create(): MainService {
            val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
                .build()
                .create(MainService::class.java)
        }
    }
}