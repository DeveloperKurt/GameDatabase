package com.developerkurt.gamedatabase.data.source.fake

import com.developerkurt.gamedatabase.data.RemoteGameDataSource
import com.developerkurt.gamedatabase.data.model.GameData
import com.developerkurt.gamedatabase.data.model.GameDetails
import com.developerkurt.gamedatabase.data.source.Result
import com.developerkurt.gamedatabase.util.mapGameDataListToResult
import com.developerkurt.gamedatabase.util.mapGameDetailsToResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlin.coroutines.coroutineContext

class FakeRemoteDataSource : RemoteGameDataSource()
{
    var gameList: MutableList<GameData>? = null
    var gameDetails: GameDetails? = null


    override val refreshIntervalInMs: Long = 100

    override suspend fun getGameDetails(gameId: Int): Result<GameDetails> = mapGameDetailsToResult(gameDetails)

    /**
     * Note: Retries are not functional at the moment
     */
    override suspend fun getGameDataList(retryWhenFailedEnabled: Boolean): Result<List<GameData>> = getGameDataList()


    override suspend fun getGameDataList(): Result<List<GameData>> = mapGameDataListToResult(gameList.also { it?.sortBy { it.name } })

    override suspend fun observeGameDataList(): Flow<Result<List<GameData>>>
    {
        return flow {
            while (coroutineContext.isActive)
            {
                emit(mapGameDataListToResult(gameList.also { it?.sortBy { it.name } }))
                kotlinx.coroutines.delay(refreshIntervalInMs)
            }
        }
    }
}