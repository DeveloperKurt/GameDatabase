package com.developerkurt.gamedatabase.data.model

import com.google.gson.annotations.SerializedName

data class GameDetails(
        @field:SerializedName("name") val name: String,
        @field:SerializedName("description") var description: String,
        @field:SerializedName("released") val releaseDate: String,
        override @field:SerializedName("background_image") val imageUrl: String,
        @field:SerializedName("metacritic") val metacriticRate: Int) : ImageURLModel()
