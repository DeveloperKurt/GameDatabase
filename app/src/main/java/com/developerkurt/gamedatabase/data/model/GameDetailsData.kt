package com.developerkurt.gamedatabase.data.model

import com.google.gson.annotations.SerializedName

data class GameDetailsData(
        @field:SerializedName("name") val name: String,
        @field:SerializedName("description") val description: String,
        @field:SerializedName("released") val released: String,
        @field:SerializedName("background_image") val background_image: String,
        @field:SerializedName("rating") val rating: Float)
{
}