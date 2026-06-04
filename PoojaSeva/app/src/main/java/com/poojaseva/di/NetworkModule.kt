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
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    @Provides
    @Singleton
    fun provideOkHttp(tokenStore: TokenStore): OkHttpClient {
        val authInterceptor = Interceptor { chain ->
            val token = tokenStore.cachedToken()
            val request = chain.request().newBuilder().apply {
                if (!token.isNullOrBlank()) addHeader("Authorization", "Bearer $token")
            }.build()
            chain.proceed(request)
        }

        // Render's free tier sleeps after idle; the first call can be slow or briefly
        // return 502/503 while the instance wakes. Retry a couple of times with backoff.
        val retryInterceptor = Interceptor { chain ->
            val request = chain.request()
            var lastError: IOException? = null
            var attempt = 0
            while (attempt < 3) {
                try {
                    val response = chain.proceed(request)
                    if (response.code in 502..504 && attempt < 2) {
                        response.close()
                        attempt++
                        sleepQuietly(1500L * attempt)
                        continue
                    }
                    return@Interceptor response
                } catch (e: IOException) {
                    lastError = e
                    attempt++
                    if (attempt >= 3) break
                    sleepQuietly(1500L * attempt)
                }
            }
            throw lastError ?: IOException("Network request failed")
        }

        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }

        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(retryInterceptor)
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

    private fun sleepQuietly(millis: Long) {
        try {
            Thread.sleep(millis)
        } catch (_: InterruptedException) {
            Thread.currentThread().interrupt()
        }
    }
}
