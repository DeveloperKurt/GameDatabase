package com.developerkurt.gamedatabase.data.model

import com.google.gson.annotations.SerializedName

data class GameDetails(
        @field:SerializedName("name") val name: String,
        @field:SerializedName("description") val description: String,
        @field:SerializedName("released") val releaseDate: String,
        @field:SerializedName("background_image") val backgroundImageURL: String,
        @field:SerializedName("metacritic") val metacriticRate: Int)
{
}