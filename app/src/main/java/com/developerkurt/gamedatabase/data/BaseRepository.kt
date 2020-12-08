package com.developerkurt.gamedatabase.data

import com.developerkurt.gamedatabase.data.model.GameData
import com.developerkurt.gamedatabase.data.model.GameDetails
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow

//TODO (Currently there isn't any implemented config functionality but variations for fetching the data can be added and
// also their base implementations can be implemented here by using the runnable.run() to increase code reusability.
// Example variations: LOCAL_FIRST_CONTINUOUS_NETWORK_REFRESH, LOCAL_FIRST_UNTIL_STALE, LOCAL_ONLY, NETWORK_ONLY

abstract class BaseRepository
{

    //TODO [Before Release] Change to 1 min
    var refreshIntervalInMs = 20000L
        protected set

    /**
     * ## Fetch the game data in specified intervals ([refreshIntervalInMs]) and return it if it's different from the cached data
     */
    abstract suspend fun getTheLatestGameList(errorListener: ErrorListener? = null): Flow<List<GameData>>

    /**
     * Fetch the game data over network
     */
    abstract protected suspend fun fetchGameListFromNetwork(): Deferred<List<GameData>?>

    /**
     * Fetch the game data from the cached local database
     */
    abstract protected suspend fun getCachedGameList(): Deferred<List<GameData>?>

    /**
     * Cache the give game data list to the local database
     */
    abstract protected suspend fun cacheGameList(gameDataList: List<GameData>): Job

    abstract suspend fun fetchGameDetailsOnceFromDatabase(gameId: Int, errorListener: ErrorListener? = null): Deferred<GameDetails?>

    abstract suspend fun addGameToFavorites(gameId: Int): Job
    abstract suspend fun removeGameFromFavorites(gameId: Int): Job

    interface ErrorListener
    {
        fun onError()
    }
}