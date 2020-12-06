package com.developerkurt.gamedatabase.data.model

import com.google.gson.annotations.SerializedName

data class GameData(
        @field:SerializedName("id") val id: Int,
        @field:SerializedName("name") val name: String,
        @field:SerializedName("released") val releaseDate: String,
        @field:SerializedName("background_image") val backgroundImage: String,
        @field:SerializedName("rating") val rating: Float)


class GameDataList(@field:SerializedName("results") val list: List<GameData>)
