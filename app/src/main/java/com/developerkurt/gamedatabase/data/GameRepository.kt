package com.developerkurt.gamedatabase.data

import android.annotation.SuppressLint
import com.developerkurt.gamedatabase.data.api.GameAPIService
import com.developerkurt.gamedatabase.data.model.GameData
import com.developerkurt.gamedatabase.data.model.GameDetails
import com.developerkurt.gamedatabase.data.persistence.RoomAppDatabase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber

//TODO [Improvement] Add multiple configs and apply it for every model unless they opt-out
/**
 * @param config Config for the retrieval method of the GameList
 */
class GameRepository private constructor(
        private val apiService: GameAPIService,
        private val roomDatabase: RoomAppDatabase,
        private val config: RepositoryConfig,
        private var refreshIntervalInMs: Long,
        private var dataExpirationDurationInMs: Long
                                        ) : BaseRepository()
{

    private var lastTimeGameListFetched = -1L


    @Volatile private var hasCachedGameData = false
    @Volatile private var isGettingContinuousUpdates = false

    private val gameDataMutableStateFlow = MutableStateFlow(DataState.UNKNOWN)
    fun gameDataStateFlow(): Flow<DataState> = gameDataMutableStateFlow

    private val gameDetailsMutableStateFlow = MutableStateFlow(DataState.UNKNOWN)
    fun gameDetailsStateFlow(): Flow<DataState> = gameDetailsMutableStateFlow

    fun getGameDataFlow(): Flow<List<GameData>> = roomDatabase.gameDataDao().subscribeToAll()


    /**
     * Fetches the data over the network depending on the provided config
     * @return Returns a boolean flow indicating whether any error has occurred while fetching the data
     */
    suspend fun startGettingGameDataUpdates()
    {

        when (config)
        {
            RepositoryConfig.LOCAL_FIRST_CONTINUOUS_NETWORK_REFRESH ->
            {

                fetchAndCacheContinuously()
            }

            RepositoryConfig.LOCAL_UNTIL_STALE ->
            {
                TODO("This config is not yet implemented")
            }
        }
    }

    /**
     * Tries to get the [GameData] list to the local repository.
     * @return true if succeeds, false if fails.
     */
    suspend fun ifAblePrepareGameDataList(): Boolean
    {

        if (roomDatabase.gameDataDao().getAnyGameData() != null)
        {
            Timber.i("Found cached local data")
            hasCachedGameData = true
            return true
        }


        //If reached here there isn't any local data, get them over the network
        val list = fetchGameListOverNetwork().await()
        if (list == null)
        {
            return false
        }
        else
        {
            cacheGameList(list)
            return true
        }

    }

    @SuppressLint("BinaryOperationInTimber")
    private suspend fun fetchAndCacheContinuously()
    {

        //We wouldn't want multiple threads to race to fetchData to the same source indefinitely right?
        if (!isGettingContinuousUpdates)
        {
            while (currentCoroutineContext().isActive)
            {
                isGettingContinuousUpdates = true

                //Fetch new data only when the elapsed time is more than the [refreshIntervalInMs]
                if (System.currentTimeMillis() - lastTimeGameListFetched > refreshIntervalInMs || lastTimeGameListFetched == -1L)
                {
                    val updatedList = fetchGameListOverNetwork().await()

                    if (updatedList != null)
                    {
                        cacheGameList(updatedList)
                        gameDataMutableStateFlow.emit(DataState.SUCCESS)
                        lastTimeGameListFetched = System.currentTimeMillis()
                    }
                    else
                    {
                        Timber.w("Fetched game list was null")

                        gameDataMutableStateFlow.emit(DataState.FAILED_TO_UPDATE)

                        if (!hasCachedGameData) gameDataMutableStateFlow.emit(DataState.FAILED)

                    }
                }
                else
                {
                    Timber.i(
                            "GameData list is still fresh, not fetching again in ${refreshIntervalInMs / 1000} seconds. " +
                                    "Elapsed time: ${System.currentTimeMillis() - lastTimeGameListFetched}")
                }
                delay(refreshIntervalInMs)
            }
            isGettingContinuousUpdates = false

        }

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
                        gameDetailsMutableStateFlow.emit(DataState.SUCCESS)
                    }
                    else
                    {
                        Timber.w("Failed to fetch data from the network database. Error body: ${response.errorBody()}, Response body: ${response.body()}")
                        gameDetailsMutableStateFlow.emit(DataState.FAILED_TO_UPDATE)
                        gameDetailsMutableStateFlow.emit(DataState.FAILED)


                    }
                }
                catch (e: Exception)
                {
                    Timber.w("Exception while trying to fetch data from the network database. Stacktrace: ${e.printStackTrace()}")
                    gameDetailsMutableStateFlow.emit(DataState.FAILED_TO_UPDATE)
                    gameDetailsMutableStateFlow.emit(DataState.FAILED)


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


    /**
     * Prior to caching, checks if there is already some cached data. If so, it updates the ones that are different
     * and adds or deletes the records if they were found/not found in the fetched [gameDataList]
     */
    private suspend fun cacheGameList(gameDataList: List<GameData>): Job =
        withContext(Dispatchers.IO) {
            return@withContext async {
                val cachedData = roomDatabase.gameDataDao().getAll()

                if (cachedData != null)
                {
                    val sumOfLists = cachedData + gameDataList

                    val groupedMapSumOfLists: MutableMap<Int, List<GameData>> = (sumOfLists.groupBy { it.id }).toMutableMap()

                    //Handle the uncommon elements
                    groupedMapSumOfLists.filter { it.value.size == 1 } //If the id groups' size is 1, it means it's either removed from or added to the server
                        .flatMap { it.value }
                        .forEach {
                            //If this data doesn't exist in the fetched network data, delete it
                            if (cachedData.contains(it))
                            {
                                roomDatabase.gameDataDao().delete(it)
                            }
                            //If this data doesn't exist in the local data, add it
                            else if (gameDataList.contains(it))
                            {
                                roomDatabase.gameDataDao().insert(it)
                            }

                            //Remove the handled data to optimize the next loop
                            groupedMapSumOfLists.remove(it.id)

                        }

                    //Handle the elements with an updated data
                    groupedMapSumOfLists.keys.forEach {
                        val gameDatasWithSameIds = groupedMapSumOfLists.get(it)

                        require(gameDatasWithSameIds!!.size == 2,
                                { "There should never be duplicates of the data in either source or unhandled addition/deletion at this stage" })

                        //Found an updated [GameData]. Take its is favorite state and add it to the new data.
                        /*NOTE: since isInFavorites field is not in the primary constructor of the GameData,
                        it won't be taken into account when comparing them*/
                        if (gameDatasWithSameIds[0] != gameDatasWithSameIds[1])
                        {
                            val isInFavorites = gameDatasWithSameIds[0].isInFavorites
                            gameDatasWithSameIds[1].isInFavorites = isInFavorites
                            roomDatabase.gameDataDao().update(gameDatasWithSameIds[1])
                        }
                    }

                }
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

        private var refreshIntervalInMs: Long = 25000L
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

        /**
         * Certain RepositoryConfigs might not require some of the fields;
         * therefore, the dependencies are checked individually for every config
         */
        fun create(): GameRepository
        {
            requireNotNull(apiService, { "API Service was not set" })
            requireNotNull(config, { "Config was not set" })
            requireNotNull(roomDatabase, { "This config requires a Room database" })


            return GameRepository(
                    apiService!!,
                    roomDatabase!!,
                    config!!,
                    refreshIntervalInMs,
                    dataExpirationDurationInMs)
        }

    }

}
