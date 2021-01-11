package com.developerkurt.gamedatabase.data.source.fake

import androidx.lifecycle.LiveData
import com.developerkurt.gamedatabase.data.model.GameData
import com.developerkurt.gamedatabase.data.model.GameDetails
import com.developerkurt.gamedatabase.data.source.GameRepository
import com.developerkurt.gamedatabase.data.source.Result

class FakeGameRepository(var gameList: MutableList<GameData>? = null, var gameDetails: GameDetails? = null) : GameRepository()
{
    override suspend fun observeGameDataList(): LiveData<Result<List<GameData>>>
    {
        TODO("Not yet implemented")
    }

    override suspend fun prepareGameDataList(): Boolean
    {
        TODO("Not yet implemented")
    }

    override suspend fun cacheGameDataList(gameDataList: List<GameData>)
    {
        TODO("Not yet implemented")
    }

    override suspend fun getGameDetails(gameId: Int): Result<GameDetails>
    {
        TODO("Not yet implemented")
    }

    override suspend fun updateIsFavorite(gameId: Int, isFavorite: Boolean)
    {
        TODO("Not yet implemented")
    }

}