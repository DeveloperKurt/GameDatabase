package com.developerkurt.gamedatabase.data

import android.annotation.SuppressLint
import com.developerkurt.gamedatabase.data.api.GameAPIService
import com.developerkurt.gamedatabase.data.model.GameData
import com.developerkurt.gamedatabase.data.model.GameDetails
import com.developerkurt.gamedatabase.data.persistence.RoomAppDatabase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber

//TODO [Before Release] Change refreshIntervalInMs to 1 min
//TODO coroutinescope is not respected
/**
 * @param config Config for the retrieval method of the GameList
 */
class GameRepository private constructor(
    private val apiService: GameAPIService,
    _roomDatabase: RoomAppDatabase?,
    private val config: RepositoryConfig,
    private var refreshIntervalInMs: Long,
    private var dataExpirationDurationInMs: Long
) : BaseRepository()
{

    private var lastTimeGameListFetched = -1L

    @Volatile
    private var gameList: List<GameData>? = null


    private val roomDatabase = _roomDatabase!!

    //TODO don't use [gameList] as a data source, use room instead. It causes a bug in favorites fragment. Also when updating the cache,
    //take the data's favorite state in account

    /**
     * Fetches the data depending on the provided config
     */
    suspend fun getTheLatestGameList(errorListener: ErrorListener?): Flow<List<GameData>>
    {

        return flow {
            if (gameList != null)
            {
                Timber.i("Found a local list of games, emitting right away.")
                emit(gameList!!)
            }
            else
            {

                when (config)
                {
                    RepositoryConfig.LOCAL_FIRST_CONTINUOUS_NETWORK_REFRESH ->
                    {
                        val cachedList = getCachedGameList().await()
                        if (cachedList != null)
                        {
                            Timber.i("Found a cached list")
                            gameList = cachedList.sortedBy { it.name }
                            emit(gameList!!)
                        }

                        fetchContinuously(errorListener).collect {
                            cacheGameList(it)
                            emit(it)
                        }

                    }
                    RepositoryConfig.LOCAL_UNTIL_STALE ->
                    {
                        TODO("This config is not implemented yet")
                    }


                }
            }
        }
    }


    @SuppressLint("BinaryOperationInTimber")
    private fun fetchContinuously(errorListener: ErrorListener?): Flow<List<GameData>>
    {

        return flow {
            while (currentCoroutineContext().isActive)
            {
                //Fetch new data only when the elapsed time is bigger than the [refreshIntervalInMs]
                if (System.currentTimeMillis() - lastTimeGameListFetched > refreshIntervalInMs || lastTimeGameListFetched == -1L)
                {
                    val updatedList = fetchGameListOverNetwork().await()

                    if (updatedList != null)
                    {

                        if (gameList == null || gameList != updatedList)
                        {
                            gameList = updatedList
                            emit(updatedList)
                        }
                        else
                        {
                            Timber.i("Not emitting the recently fetch game list hence they are identical")
                        }

                        lastTimeGameListFetched = System.currentTimeMillis()

                    }
                    else
                    {
                        Timber.w("Fetched game list was null")
                        errorListener?.onError()
                    }
                }
                else
                {
                    Timber.i(
                        "GameData list is still fresh, not fetching again in ${refreshIntervalInMs / 1000} seconds. " +
                                "Elapsed time: ${System.currentTimeMillis() - lastTimeGameListFetched}"
                    )
                }
                delay(refreshIntervalInMs)
            }
        }.flowOn(Dispatchers.IO)
    }

    private suspend fun fetchGameListOverNetwork(): Deferred<List<GameData>?> =
        withContext(Dispatchers.IO) {

            return@withContext async {
                var list: List<GameData>? = null
                try
                {
                    val response = apiService.getGameList().execute()

                    if (response.isSuccessful && response.body() != null)
                    {
                        Timber.i("Response is successful")
                        list = response.body()!!.list
                        list = list.sortedBy { it.name }
                    }
                    else
                    {
                        Timber.w("Failed to fetch data from the network database. Error body: ${response.errorBody()}, Response body: ${response.body()}")

                    }
                }
                catch (e: Exception)
                {
                    Timber.w("Exception while trying to fetch data from the network database. Stacktrace: ${e.printStackTrace()}")

                }
                finally
                {
                    return@async list
                }
                list //IDE is not smart enough to realize we are already returning no matter what in finally block; therefore, this needs to stay here
            }

        }


    suspend fun fetchGameDetailsOnceFromDatabase(gameId: Int): Deferred<GameDetails?> =
        withContext(Dispatchers.IO) {

            var gameDetails: GameDetails? = null
            return@withContext async {
                try
                {
                    val response = apiService.getGameDetails(gameId).execute()

                    if (response.isSuccessful)
                    {
                        Timber.i("Response is successful")

                        gameDetails = response.body()
                    }
                    else
                    {
                        Timber.w("Failed to fetch data from the network database. Error body: ${response.errorBody()}, Response body: ${response.body()}")

                    }
                }
                catch (e: Exception)
                {
                    Timber.w("Exception while trying to fetch data from the network database. Stacktrace: ${e.printStackTrace()}")

                }
                finally
                {
                    return@async gameDetails
                }
                gameDetails
            }

        }


    private suspend fun getCachedGameList(): Deferred<List<GameData>?> =
        withContext(Dispatchers.IO) {
            return@withContext async {
                return@async roomDatabase.gameDataDao().getAll()
            }
        }

    //TODO delete entire old cached data
    private suspend fun cacheGameList(gameDataList: List<GameData>): Job =
        withContext(Dispatchers.IO) {
            return@withContext async {
                roomDatabase.gameDataDao().insert(*gameDataList.toTypedArray())
            }
        }


    suspend fun updateIsFavorite(gameDataId: Int, isFavorite: Boolean): Job = withContext(Dispatchers.IO) {

        return@withContext async {
            roomDatabase.gameDataDao().updateIsFavorite(gameDataId, if (isFavorite) 1 else 0)
        }
    }


    suspend fun updateGameData(gameData: GameData): Job = withContext(Dispatchers.IO) {

        return@withContext async {
            roomDatabase.gameDataDao().update(gameData)
        }
    }


    class GameRepositoryBuilder
    {
        private var apiService: GameAPIService? = null
        private var roomDatabase: RoomAppDatabase? = null
        private var config: RepositoryConfig? = null

        private var refreshIntervalInMs: Long = 20000L
        private var dataExpirationDurationInMs: Long = 1000 * 60 * 60L


        fun setApiService(apiService: GameAPIService): GameRepositoryBuilder
        {
            this.apiService = apiService
            return this
        }


        fun setRoomDatabase(roomDatabase: RoomAppDatabase): GameRepositoryBuilder
        {
            this.roomDatabase = roomDatabase
            return this
        }


        fun setConfig(config: RepositoryConfig): GameRepositoryBuilder
        {
            this.config = config
            return this
        }

        fun setRefreshIntervalInMs(value: Long): GameRepositoryBuilder
        {
            require(value > 0, { "Refresh interval has to be bigger than 0" })
            this.refreshIntervalInMs = value
            return this
        }

        fun setDataExpirationDurationInMs(value: Long): GameRepositoryBuilder
        {
            require(value > 0, { "Data expiration duration has to be bigger than 0" })
            this.dataExpirationDurationInMs = value
            return this
        }

        fun create(): GameRepository
        {
            requireNotNull(apiService, { "API Service was not set" })
            requireNotNull(config, { "Config was not set" })

            when (config)
            {
                RepositoryConfig.LOCAL_FIRST_CONTINUOUS_NETWORK_REFRESH ->
                {
                    requireNotNull(roomDatabase, { "This config requires a Room database" })

                }
                RepositoryConfig.LOCAL_UNTIL_STALE ->
                {
                    requireNotNull(roomDatabase, { "This config requires a Room database" })

                }

            }

            return GameRepository(
                apiService!!,
                roomDatabase,
                config!!,
                refreshIntervalInMs,
                dataExpirationDurationInMs
            )
        }

    }


}
