package com.developerkurt.gamedatabase.data.model

import com.google.gson.annotations.SerializedName


data class GameData(
        @field:SerializedName("id") val id: Int,
        @field:SerializedName("name") val name: String,
        @field:SerializedName("released") val releaseDate: String,
        override @field:SerializedName("background_image") val imageUrl: String,
        @field:SerializedName("rating") val rating: Float) : ImageURLModel()


class GameDataList(@field:SerializedName("results") val list: List<GameData>)
