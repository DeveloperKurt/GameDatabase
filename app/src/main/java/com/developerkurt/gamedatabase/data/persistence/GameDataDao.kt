package com.developerkurt.gamedatabase.data.persistence

import androidx.room.*
import com.developerkurt.gamedatabase.data.model.GameData
import com.developerkurt.gamedatabase.data.model.IS_IN_FAVORITES_KEY
import com.developerkurt.gamedatabase.data.model.GAME_TABLE_NAME

@Dao
interface GameDataDao
{
    @Query("SELECT * FROM $GAME_TABLE_NAME")
    suspend fun getAll(): List<GameData>?

    @Query("SELECT * FROM $GAME_TABLE_NAME WHERE $IS_IN_FAVORITES_KEY = 1")
    suspend fun loadAllFavorites(): List<GameData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg gameData: GameData)

    @Delete
    suspend fun delete(vararg gameData: GameData)

    @Update
    suspend fun update(vararg gameData: GameData)
}