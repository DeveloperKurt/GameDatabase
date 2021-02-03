package com.developerkurt.gamedatabase.data.source.fake

import com.developerkurt.gamedatabase.data.LocalGameDataSource
import com.developerkurt.gamedatabase.data.model.GameData
import com.developerkurt.gamedatabase.data.source.Result
import com.developerkurt.gamedatabase.util.mapGameDataListToResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf


class FakeLocalGameDataSource : LocalGameDataSource
{
    var gameList: MutableList<GameData>? = null

    //Any update, delete, or write operation increases this counter
    var timesUpdatedData = 0
        protected set

    override suspend fun updateGameData(vararg gameData: GameData)
    {
        require(gameList != null)

        gameData.forEach { currentGameData ->
            val result = gameList!!.find { it.id == currentGameData.id }

            if (result != null)
            {
                gameList!!.remove(result)
                gameList!!.add(currentGameData)
            }
        }
        gameList?.sortBy { it.name }
        timesUpdatedData++
    }

    override suspend fun removeGameData(vararg gameData: GameData)
    {
        gameList?.removeAll(gameData)
        gameList?.sortBy { it.name }
        timesUpdatedData++
    }

    override suspend fun insertGameData(vararg gameData: GameData)
    {
        if (gameList == null) gameList = mutableListOf()

        gameList?.addAll(gameData)
        gameList?.sortBy { it.name }
        timesUpdatedData++
    }

    override suspend fun updateIsFavorite(gameId: Int, isFavorite: Int)
    {
        require(gameList != null)

        var index = 0
        gameList!!.forEach {
            if (it.id == gameId)
            {
                gameList!![index].isInFavorites = if (isFavorite == 1) true else false
            }
            index++
        }
        timesUpdatedData++

    }

    override suspend fun isDatabaseEmpty(): Boolean = gameList.isNullOrEmpty()


    override suspend fun observeGameDataList(): Flow<Result<List<GameData>>> =
        flowOf(mapGameDataListToResult(gameList.also { gameList?.sortBy { it.name } }))


    override suspend fun getGameDataList(): Result<List<GameData>> = mapGameDataListToResult(gameList.also { gameList?.sortBy { it.name } })


}