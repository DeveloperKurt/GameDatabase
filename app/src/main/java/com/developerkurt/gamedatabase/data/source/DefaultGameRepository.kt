package com.developerkurt.gamedatabase.data.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.developerkurt.gamedatabase.data.LocalGameDataSource
import com.developerkurt.gamedatabase.data.RemoteGameDataSource
import com.developerkurt.gamedatabase.data.model.GameData
import com.developerkurt.gamedatabase.data.model.GameDetails
import com.developerkurt.gamedatabase.util.stripHtml
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.coroutines.coroutineContext

/**
 * @param config Config for the retrieval method of the GameList
 */
class DefaultGameRepository(
        private val remoteDataSource: RemoteGameDataSource,
        private val localGameDataSource: LocalGameDataSource,
        private val config: RepositoryConfig) : GameRepository()
{

    /**
     * NOTE: StateFlow was deliberately not used since it doesn't emit the "same" values which creates a problem with the GameData
     * since isInFavorites property is not used in the equals() method
     */
    private val gameDataListResultMutableLiveData: MutableLiveData<Result<List<GameData>>> = MutableLiveData(Result.Loading)

    private var localSourceGameDataListFlow: Flow<Result<List<GameData>>>? = null


    override suspend fun prepareGameDataList(): Boolean
    {

        if (!localGameDataSource.isDatabaseEmpty())
        {
            Timber.i("Found cached local data")
            return true
        }

        //If reached here there isn't any local data, get them over the network
        val result = remoteDataSource.getGameDataList(false)

        if (result is Result.Success)
        {
            localGameDataSource.insertGameData(*result.data.toTypedArray())
            return true
        }
        else
        {
            return false
        }

    }

    private suspend fun reflectChangesOnLocalDataSource()
    {
        if (localSourceGameDataListFlow == null)
        {
            localSourceGameDataListFlow = localGameDataSource.observeGameDataList()
        }

        CoroutineScope(Dispatchers.IO + coroutineContext).launch {
            localSourceGameDataListFlow!!.collectLatest {
                gameDataListResultMutableLiveData.postValue(it)
            }
        }

    }

    /**
     * If finds the GameData list in the heap (which might be stored when a previous call was made through any class that has access to this repo)
     * returns that.
     * Otherwise, does the operations depending on the config provided.
     */
    override suspend fun observeGameDataList(): LiveData<Result<List<GameData>>>
    {
        //If the config uses the local database, listen to it
        if (config == RepositoryConfig.LOCAL_FIRST_CONTINUOUS_NETWORK_REFRESH || config == RepositoryConfig.LOCAL_FIRST_REFRESH_ONCE)
        {
            reflectChangesOnLocalDataSource()
        }

        when (config)
        {
            RepositoryConfig.LOCAL_FIRST_CONTINUOUS_NETWORK_REFRESH ->
            {

                val gameDataListFlow = remoteDataSource.observeGameDataList()

                CoroutineScope(Dispatchers.Default + coroutineContext).launch {
                    gameDataListFlow.collectLatest {

                        val result = decideFinalStateOfResult(it, !localGameDataSource.isDatabaseEmpty())

                        /**
                         * If it succeeds the cached data will trigger the flow emission where we collect at #reflectChangesOnLocalDataSource
                         * Otherwise we want to decide on the Result's last state. Because an Error state sent by the RemoteSource might not always be
                         * an error when we already have a stored data in the LocalDataSource
                         */
                        if (result is Result.Success)
                        {

                            if (gameDataListResultMutableLiveData.value is Result.Success || gameDataListResultMutableLiveData.value != result)
                            {
                                cacheGameDataList(result.data)
                            }
                        }
                        else
                        {
                            gameDataListResultMutableLiveData.postValue(result)
                        }

                    }
                }
            }


            RepositoryConfig.LOCAL_FIRST_REFRESH_ONCE ->
            {

                val result = decideFinalStateOfResult(remoteDataSource.getGameDataList(), !localGameDataSource.isDatabaseEmpty())

                if (result is Result.Success)
                    cacheGameDataList(result.data)

                gameDataListResultMutableLiveData.postValue(result)

            }

        }


        return gameDataListResultMutableLiveData
    }

    /**
     * Depending on the [hasCachedGameData] modifies the Result accordingly if the state is not [Result.Success]
     */
    private fun decideFinalStateOfResult(result: Result<List<GameData>>, hasCachedGameData: Boolean): Result<List<GameData>>
    {
        return if (hasCachedGameData && result is Result.Error)
        {
            Result.FailedToUpdate
        }
        else
        {
            result
        }

    }

    /**
     * Prior to caching, checks if there is already some cached data. If so, it updates the ones that are different
     * and adds or deletes the records if they were found/not found in the fetched [gameDataList]
     */
    override suspend fun cacheGameDataList(gameDataList: List<GameData>)
    {
        val cachedDataResult = localGameDataSource.getGameDataList()

        if (cachedDataResult.succeeded)
        {
            val cachedData = (cachedDataResult as Result.Success).data

            if (cachedData.size > 0)
            {
                val sumOfLists = cachedData + gameDataList

                val groupedMapSumOfLists: MutableMap<Int, List<GameData>> = (sumOfLists.groupBy { it.id }).toMutableMap()

                //Handle the uncommon elements
                groupedMapSumOfLists.filter { it.value.size == 1 } //If the id groups' size is 1, it means it's either removed from or added to the server
                    .flatMap { it.value }
                    .forEach {
                        val indexOfCurrentGameDataInCache = cachedData.indexOf(it)

                        //If this data exists in the cache it means it doesn't exist in the fetched network data, delete it
                        if (indexOfCurrentGameDataInCache > -1)
                        {
                            localGameDataSource.removeGameData(it)
                        }
                        //If this data doesn't exist in the local data, add it
                        else
                        {
                            localGameDataSource.insertGameData(it)
                        }

                        //Remove the handled data to optimize the next loop
                        groupedMapSumOfLists.remove(it.id)

                    }

                //Handle the elements with an updated data
                groupedMapSumOfLists.keys.forEach {
                    val gameDatasWithSameIds = groupedMapSumOfLists.get(it)

                    require(gameDatasWithSameIds!!.size == 2,
                            { "There should never be duplicates of the data in either source or an unhandled addition/deletion at this stage" })

                    //Found an updated [GameData]. Take its is favorite state and add it to the new data.
                    /*NOTE: since isInFavorites field is not in the primary constructor of the GameData,
                            it won't be taken into account when comparing them*/
                    if (gameDatasWithSameIds[0] != gameDatasWithSameIds[1])
                    {
                        val isInFavorites = gameDatasWithSameIds[0].isInFavorites
                        gameDatasWithSameIds[1].isInFavorites = isInFavorites
                        localGameDataSource.updateGameData(gameDatasWithSameIds[1])
                    }
                }

            }
            else
            {
                localGameDataSource.insertGameData(*gameDataList.toTypedArray())
            }
        }
        else
        {
            localGameDataSource.insertGameData(*gameDataList.toTypedArray())
        }
    }

    override suspend fun getGameDetails(gameId: Int): Result<GameDetails>
    {
        val result = remoteDataSource.getGameDetails(gameId)

        if (result is Result.Success)
        {
            result.data.description = result.data.description.stripHtml()
        }

        return result
    }

    override suspend fun updateIsFavorite(gameId: Int, isFavorite: Boolean) =
        localGameDataSource.updateIsFavorite(gameId, if (isFavorite) 1 else 0)


    enum class RepositoryConfig
    {
        /**
         * Starts by checking the local cache and follows it by sending continuous rapid network requests at
         * intervals specified in [RemoteGameDataSource.refreshIntervalInMs]
         */
        LOCAL_FIRST_CONTINUOUS_NETWORK_REFRESH,

        /**
         * Starts by checking/returning the local cache and sends a single network requests afterwards
         */
        LOCAL_FIRST_REFRESH_ONCE

    }

}
