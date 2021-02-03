package com.developerkurt.gamedatabase.data.source

import androidx.lifecycle.LiveData
import com.developerkurt.gamedatabase.data.model.GameData
import com.developerkurt.gamedatabase.data.model.GameDetails

abstract class GameRepository
{


    abstract suspend fun observeGameDataList(): LiveData<Result<List<GameData>>>

    /**
     * Tries to get the [GameData] list to the local repository.
     * @return true if succeeds, false if fails.
     */
    abstract suspend fun prepareGameDataList(): Boolean

    /**
     * Prior to caching, checks if there is already some cached data. If so, it updates the ones that are different
     * and adds or deletes the records if they were found/not found in the fetched [gameDataList]
     */
    abstract protected suspend fun cacheGameDataList(gameDataList: List<GameData>)

    abstract suspend fun getGameDetails(gameId: Int): Result<GameDetails>

    abstract suspend fun updateIsFavorite(gameId: Int, isFavorite: Boolean)

}