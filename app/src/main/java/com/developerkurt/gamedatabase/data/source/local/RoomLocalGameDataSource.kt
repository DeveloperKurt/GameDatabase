package com.developerkurt.gamedatabase.data.source.local

import com.developerkurt.gamedatabase.data.LocalGameDataSource
import com.developerkurt.gamedatabase.data.model.GameData
import com.developerkurt.gamedatabase.data.source.Result
import com.developerkurt.gamedatabase.data.source.local.room.GameDataDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber

class RoomLocalGameDataSource(
        private val gameDataDao: GameDataDao,
        private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) : LocalGameDataSource
{


    override suspend fun isDatabaseEmpty(): Boolean
    {
        return withContext(ioDispatcher)
        {
            return@withContext gameDataDao.getAnyGameData() == null
        }
    }

    override suspend fun observeGameDataList(): Flow<Result<List<GameData>>>
    {
        return withContext(ioDispatcher)
        {
            return@withContext gameDataDao.subscribeToAll().map { list ->
                return@map getGameDataListResult(list)
            }
        }
    }

    override suspend fun getGameDataList(): Result<List<GameData>>
    {
        return withContext(ioDispatcher)
        {
            val list = gameDataDao.getAll()

            getGameDataListResult(list)
        }
    }

    private fun getGameDataListResult(gameDataList: List<GameData>?): Result<List<GameData>>
    {

        return if (gameDataList == null || gameDataList.isEmpty())
        {
            val errorMessage = "No game data list was found in the local database"
            Timber.w(errorMessage)

            Result.Error(errorMessage = errorMessage)

        }
        else
        {
            Result.Success(gameDataList)
        }

    }


    override suspend fun updateIsFavorite(gameId: Int, isFavorite: Int)
    {
        withContext(ioDispatcher)
        {
            gameDataDao.updateIsFavorite(gameId, isFavorite)
        }
    }

    override suspend fun updateGameData(vararg gameData: GameData)
    {
        withContext(ioDispatcher)
        {
            gameDataDao.update(*gameData)
        }

    }

    override suspend fun removeGameData(vararg gameData: GameData)
    {
        withContext(ioDispatcher)
        {
            gameDataDao.delete(*gameData)
        }
    }

    override suspend fun insertGameData(vararg gameData: GameData)
    {
        withContext(ioDispatcher)
        {
            gameDataDao.insert(*gameData)
        }
    }
}