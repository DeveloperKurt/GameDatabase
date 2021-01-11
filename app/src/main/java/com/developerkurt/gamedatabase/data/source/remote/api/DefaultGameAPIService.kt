package com.developerkurt.gamedatabase.data.source.remote.api

import com.developerkurt.gamedatabase.data.model.GameDataList
import com.developerkurt.gamedatabase.data.model.GameDetails
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface DefaultGameAPIService
{

    //TODO [Improvement] utilize the coroutine support of Retrofit by making the API functions suspend

    @Headers(HEADER_KEY, HEADER_HOST)
    @GET("games")
    fun getGameList(): Call<GameDataList>

    @Headers(HEADER_KEY, HEADER_HOST)
    @GET("games/{game_id}")
    fun getGameDetails(@Path("game_id") game_id: Int): Call<GameDetails>


    companion object
    {
        private const val HEADER_KEY = "x-rapidapi-key:8ca4ee9d44msh7419ea658215022p1a3ca0jsna719390dd548"
        private const val HEADER_HOST = "x-rapidapi-host:rawg-video-games-database.p.rapidapi.com"

    }
}