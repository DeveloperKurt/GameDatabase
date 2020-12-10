package com.developerkurt.gamedatabase.data.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import com.developerkurt.gamedatabase.data.model.GameData

const val databaseName = "game_database"

@Database(entities = arrayOf(GameData::class), version = 1)
abstract class RoomAppDatabase : RoomDatabase()
{
    abstract fun gameDataDao(): GameDataDao
}

