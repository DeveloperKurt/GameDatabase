package com.developerkurt.gamedatabase.data.api

import com.developerkurt.gamedatabase.BuildConfig
import com.developerkurt.gamedatabase.data.model.GameDataList
import com.developerkurt.gamedatabase.data.model.GameDetails
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import timber.log.Timber
import java.util.concurrent.TimeUnit

interface GameAPIService
{

    @Headers(HEADER_KEY, HEADER_HOST)
    @GET("games")
    fun getGameList(): Call<GameDataList>

    @Headers(HEADER_KEY, HEADER_HOST)
    @GET("games/{game_id}")
    fun getGameDetails(@Path("game_id") game_id: Int): Call<GameDetails>

    companion object
    {
        private const val BASE_URL = "https://rawg-video-games-database.p.rapidapi.com/"


        private const val HEADER_KEY = "x-rapidapi-key:8ca4ee9d44msh7419ea658215022p1a3ca0jsna719390dd548"
        private const val HEADER_HOST = "x-rapidapi-host:rawg-video-games-database.p.rapidapi.com"

        private const val TIMEOUT_IN_SECONDS = 60L
        private const val MAX_RETRY_COUNT = 15
        private const val RETRY_INTERVAL = 250L

        fun create(): GameAPIService
        {
            val builder = OkHttpClient.Builder()
                .connectTimeout(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
                .addInterceptor { chain ->

                    val request: Request = chain.request()

                    // try the request
                    var response: Response? = null
                    var isSuccessful = false
                    var tryCount = 0

                    //If fails, try again
                    do
                    {
                        try
                        {
                            response = chain.proceed(request)
                            isSuccessful = response.isSuccessful

                        }
                        catch (e: Exception)
                        {
                            Timber.w("Request was not successful, tryCount: $tryCount")
                        }
                        finally
                        {
                            Thread.sleep(RETRY_INTERVAL)
                            tryCount++
                        }
                    }
                    while (!isSuccessful && tryCount < MAX_RETRY_COUNT)


                    // otherwise just pass the original response on
                    return@addInterceptor response!!
                }


            if (BuildConfig.DEBUG)
            {
                val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
                builder.addInterceptor(logger)
            }

            val client = builder.build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GameAPIService::class.java)
        }
    }
}