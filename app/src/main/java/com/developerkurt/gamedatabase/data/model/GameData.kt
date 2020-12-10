package com.developerkurt.gamedatabase.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

const val GAME_TABLE_NAME = "game_data"

const val GAME_ID_KEY = "id"
const val GAME_NAME_KEY = "name"
const val RELEASE_DATE_KEY = "released"
const val IMAGE_URL_KEY = "background_image"
const val RATING_KEY = "rating"
const val IS_IN_FAVORITES_KEY = "is_in_favorites"


@Entity(tableName = GAME_TABLE_NAME)
data class GameData(
    @PrimaryKey @field:SerializedName(GAME_ID_KEY)
    val id: Int,
    @ColumnInfo(name = GAME_NAME_KEY) @field:SerializedName(GAME_NAME_KEY)
    val name: String,
    @ColumnInfo(name = RELEASE_DATE_KEY) @field:SerializedName(RELEASE_DATE_KEY)
    val releaseDate: String,
    @ColumnInfo(name = IMAGE_URL_KEY) override @field:SerializedName(IMAGE_URL_KEY)
    val imageUrl: String,
    @ColumnInfo(name = RATING_KEY) @field:SerializedName(RATING_KEY)
    val rating: Float
) : ImageURLModel()
{
    /**
     * Note: Since this is not in the primary constructor, it is not taken into account in the
     * auto-generated equals() comparator; therefore, it doesn't create a bug where retrieved data
     * seems to be "different" than the cached data.
     */
    @ColumnInfo(name = IS_IN_FAVORITES_KEY)
    var isInFavorites: Boolean = false

}

/**
 * Used only when deserializing the JSON via Retrofit
 */
class GameDataList(@field:SerializedName("results") val list: List<GameData>)
