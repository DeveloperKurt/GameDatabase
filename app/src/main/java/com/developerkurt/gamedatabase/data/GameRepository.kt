package com.developerkurt.gamedatabase.data

import android.annotation.SuppressLint
import com.developerkurt.gamedatabase.data.api.GameAPIService
import com.developerkurt.gamedatabase.data.model.GameData
import com.developerkurt.gamedatabase.data.model.GameDetails
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import kotlin.coroutines.coroutineContext


class GameRepository constructor(private val apiService: GameAPIService) : BaseRepository()
{
    private var lastGameList = listOf<GameData>()
    private var lastTimeGameListFetched = -1L

    @SuppressLint("BinaryOperationInTimber")
    override suspend fun getTheLatestGameList(errorListener: ErrorListener?): Flow<List<GameData>>
    {

        var alreadyRetrievedFromNetwork = false
        return flow {

            //TODO launch another async coroutine to return cached if network repository didn't emit it already


            while (coroutineContext.isActive)
            {

                //Fetch new data only when the elapsed time is bigger than the [refreshIntervalInMs]
                if (System.currentTimeMillis() - lastTimeGameListFetched > refreshIntervalInMs || lastTimeGameListFetched == -1)
                {
                    val deferredLatestList = fetchGameListFromNetwork()
                    val updatedList = deferredLatestList.await()

                    if (updatedList != null)
                    {

                        if (lastGameList.isEmpty() || lastGameList != updatedList)
                        {
                            lastGameList.map { it.imageBitmap = TODO("url to bitmap") }
                            lastGameList = updatedList
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
                                    "Elapsed time: ${System.currentTimeMillis() - lastTimeGameListFetched}")
                }
            }

            delay(refreshIntervalInMs)


        }.flowOn(Dispatchers.IO)
    }


    override suspend fun fetchGameListFromNetwork(): Deferred<List<GameData>?> = withContext(Dispatchers.IO) {

        return@withContext async {
            var list: List<GameData>? = null
            try
            {
                val response = apiService.getGameList().execute()

                if (response.isSuccessful && response.body() != null)
                {
                    Timber.i("Response is successful")

                    list = response.body()!!.list
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

    override suspend fun fetchGameDetailsOnceFromDatabase(gameId: Int, errorListener: ErrorListener?): Deferred<GameDetails?> =
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

    override suspend fun getCachedGameList(): Deferred<List<GameData>?>
    {
        TODO("Not yet implemented")

    }


    override suspend fun cacheGameList(gameDataList: List<GameData>): Job
    {
        TODO("Not yet implemented")
    }


    override suspend fun addGameToFavorites(gameId: Int): Job
    {
        TODO("Not yet implemented")
    }

    override suspend fun removeGameFromFavorites(gameId: Int): Job
    {
        TODO("Not yet implemented")
    }

}

enum class RequestResult
{
    SUCCESS, FAIL
}