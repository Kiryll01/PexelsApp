package com.example.pexelsapp.Web

import com.example.pexelsapp.Data.Responses.PexelsCollectionsResponse
import com.example.pexelsapp.Data.Responses.PexelsSearchResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


interface PexelsApiService {
    companion object{
        const val PEXELS_PAGE_SIZE= 20
    }
    @GET("search")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("per_page") perPage: Int = PEXELS_PAGE_SIZE,
        @Query("page") page: Int = 1
    ): PexelsSearchResponse

    @GET("collections/featured")
    suspend fun getFeaturedCollections(
        @Query("per_page") perPage: Int = 7,
        @Query("page") page: Int = 1
    ) : PexelsCollectionsResponse
    @GET("curated")
    suspend fun getCuratedPhotos(
        @Query("per_page") perPage: Int=30,
        @Query("page") page :Int = 1
    ) : PexelsSearchResponse
}


object PexelsApiClient {
    private const val BASE_URL = "https://api.pexels.com/v1/"
    private const val API_KEY = "3WWYeD9MRzPMDChSUIBBsn10TKh3adA5glr8GBySPTyWXqptsBQqgvGN"

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", API_KEY)
                .build()
            chain.proceed(request)
        }
        .addInterceptor(HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BASIC))
        .build()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .add(IntNullableAdapter())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val apiService = retrofit.create(PexelsApiService::class.java)
}