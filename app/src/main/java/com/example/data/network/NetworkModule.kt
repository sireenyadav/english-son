package com.example.data.network

import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object NetworkModule {
    private const val BASE_URL = BuildConfig.SUPABASE_URL

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val authInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("apikey", BuildConfig.SUPABASE_ANON_KEY)
            .addHeader("Authorization", "Bearer ${BuildConfig.SUPABASE_ANON_KEY}")
            .build()
        chain.proceed(request)
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    private val groqAuthInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer ${BuildConfig.GROQ_API_KEY}")
            .build()
        chain.proceed(request)
    }

    private val groqClient = OkHttpClient.Builder()
        .addInterceptor(groqAuthInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(if (BASE_URL.isNotEmpty()) (if (BASE_URL.endsWith("/")) BASE_URL else "$BASE_URL/") else "https://example.supabase.co/")
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    private val groqRetrofit = Retrofit.Builder()
        .baseUrl("https://api.groq.com/openai/v1/")
        .client(groqClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val supabaseApi: SupabaseApi by lazy {
        retrofit.create(SupabaseApi::class.java)
    }

    val groqApi: GroqApi by lazy {
        groqRetrofit.create(GroqApi::class.java)
    }
}
