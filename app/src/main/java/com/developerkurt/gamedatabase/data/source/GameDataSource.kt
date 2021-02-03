package com.developerkurt.gamedatabase.data


import com.developerkurt.gamedatabase.data.model.GameData
import com.developerkurt.gamedatabase.data.model.GameDetails
import com.developerkurt.gamedatabase.data.source.Result
import kotlinx.coroutines.flow.Flow

/**
 * Main entry point for accessing game related data.
 */
interface GameDataSource
{
    suspend fun observeGameDataList(): Flow<Result<List<GameData>>>

    suspend fun getGameDataList(): Result<List<GameData>>
}


interface LocalGameDataSource : GameDataSource
{
    suspend fun updateGameData(vararg gameData: GameData)

    suspend fun removeGameData(vararg gameData: GameData)

    suspend fun insertGameData(vararg gameData: GameData)

    suspend fun updateIsFavorite(gameId: Int, isFavorite: Int)

    suspend fun isDatabaseEmpty(): Boolean
}


abstract class RemoteGameDataSource : GameDataSource
{
    abstract val refreshIntervalInMs: Long

    protected var lastTimeGameListFetched = -1L

    abstract suspend fun getGameDetails(gameId: Int): Result<GameDetails>

    abstract suspend fun getGameDataList(retryWhenFailedEnabled: Boolean): Result<List<GameData>>

}





