package com.poojaseva.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.poojaseva.BuildConfig
import com.poojaseva.data.local.TokenStore
import com.poojaseva.data.remote.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideJson(): Json = Json { ignoreUnknownKeys = true }

    @Provides
    @Singleton
    fun provideOkHttp(tokenStore: TokenStore): OkHttpClient {
        val authInterceptor = Interceptor { chain ->
            val token = tokenStore.getTokenBlocking()
            val builder = chain.request().newBuilder()
            if (!token.isNullOrBlank()) {
                builder.addHeader("Authorization", "Bearer $token")
            }
            chain.proceed(builder.build())
        }
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(json: Json, client: OkHttpClient): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)
}
