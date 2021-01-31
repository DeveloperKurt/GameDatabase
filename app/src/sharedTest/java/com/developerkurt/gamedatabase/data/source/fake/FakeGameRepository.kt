package com.developerkurt.gamedatabase.data.source.fake

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.developerkurt.gamedatabase.data.model.GameData
import com.developerkurt.gamedatabase.data.model.GameDetails
import com.developerkurt.gamedatabase.data.source.GameRepository
import com.developerkurt.gamedatabase.data.source.Result
import com.developerkurt.gamedatabase.util.mapGameDataListToResult
import com.developerkurt.gamedatabase.util.mapGameDetailsToResult

class FakeGameRepository : GameRepository()
{
    /**
     * NOTE: Manipulations made by calling list-specific methods like add, remove won't have any effects for the observed LiveData.
     * If you want the LiveData to emit a new series of data, simply assign a different list to this variable.
     */
    var gameList: MutableList<GameData>? = mutableListOf()
        set(value)
        {
            field = value
            mutableGameListLiveData.postValue(mapGameDataListToResult(value))
        }

    var gameDetails: GameDetails? = null

    private val mutableGameListLiveData = MutableLiveData<Result<List<GameData>>>(Result.Loading)

    override suspend fun observeGameDataList(): LiveData<Result<List<GameData>>> = mutableGameListLiveData

    override suspend fun prepareGameDataList(): Boolean = (!gameList.isNullOrEmpty())

    override suspend fun cacheGameDataList(gameDataList: List<GameData>)
    {
    }

    override suspend fun getGameDetails(gameId: Int): Result<GameDetails> = mapGameDetailsToResult(gameDetails)


    override suspend fun updateIsFavorite(gameId: Int, isFavorite: Boolean)
    {
        gameList!!.find { it.id == gameId }!!.isInFavorites = isFavorite
    }


}