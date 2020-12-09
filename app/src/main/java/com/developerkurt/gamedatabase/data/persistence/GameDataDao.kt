package com.developerkurt.gamedatabase.data.persistence

import androidx.room.*
import com.developerkurt.gamedatabase.data.model.GAME_ID_KEY
import com.developerkurt.gamedatabase.data.model.GAME_TABLE_NAME
import com.developerkurt.gamedatabase.data.model.GameData
import com.developerkurt.gamedatabase.data.model.IS_IN_FAVORITES_KEY

@Dao
interface GameDataDao
{
    @Query("SELECT * FROM $GAME_TABLE_NAME")
    suspend fun getAll(): List<GameData>?

    @Query("SELECT * FROM $GAME_TABLE_NAME WHERE $IS_IN_FAVORITES_KEY = 1")
    @Deprecated("Not tested yet since there's no need for this query at this time.")
    suspend fun getAllFavorites(): List<GameData>

    /**
     * @param isFavorite 0 for false, 1 for true
     */
    @Query("UPDATE $GAME_TABLE_NAME SET $IS_IN_FAVORITES_KEY = :isFavorite WHERE $GAME_ID_KEY = :id")
    suspend fun updateIsFavorite(id: Int, isFavorite: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg gameData: GameData)

    @Delete
    suspend fun delete(vararg gameData: GameData)

    @Update
    suspend fun update(vararg gameData: GameData)
}