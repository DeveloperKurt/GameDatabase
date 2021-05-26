package com.developerkurt.gamedatabase.data.source.remote.api

import com.developerkurt.gamedatabase.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

//TODO [IMPROVEMENT] Use Moshi instead of GSON
class DefaultGameAPIServiceGenerator(
        private val baseUrl: String = "https://rawg-video-games-database.p.rapidapi.com/",
        private val timeoutInSeconds: Long = 30L)
{

    init
    {
        require(timeoutInSeconds >= 0)
    }

    fun create(): DefaultGameAPIService
    {
        val builder = OkHttpClient.Builder()
            .connectTimeout(timeoutInSeconds, TimeUnit.SECONDS)
            .readTimeout(timeoutInSeconds, TimeUnit.SECONDS)
            .writeTimeout(timeoutInSeconds, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                //Add the auth key as a default query to every request
                val urlWithAuthKey = chain.request().url.newBuilder().addQueryParameter("key", DefaultGameAPIService.AUTH_KEY).build()
                val request: Request = chain.request().newBuilder().url(urlWithAuthKey).build()

                return@addInterceptor chain.proceed(request)

            }


        if (BuildConfig.DEBUG)
        {
            val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
            builder.addInterceptor(logger)
        }

        val client = builder.build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DefaultGameAPIService::class.java)
    }
}