package com.developerkurt.gamedatabase.data.source.remote

import android.annotation.SuppressLint
import com.developerkurt.gamedatabase.data.RemoteGameDataSource
import com.developerkurt.gamedatabase.data.model.GameData
import com.developerkurt.gamedatabase.data.model.GameDetails
import com.developerkurt.gamedatabase.data.source.Result
import com.developerkurt.gamedatabase.data.source.remote.api.DefaultGameAPIService
import com.developerkurt.gamedatabase.util.execute
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import timber.log.Timber
import kotlin.coroutines.coroutineContext

class DefaultRemoteGameDataSource(
        private val gameAPIService: DefaultGameAPIService,
        private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
        private val retryWhenFailedEnabled: Boolean = true,
        override val refreshIntervalInMs: Long = 25000L) : RemoteGameDataSource()
{
    private var gameDataListResultFlow: MutableSharedFlow<Result<List<GameData>>> = MutableSharedFlow()

    @Volatile private var isGettingContinuousUpdates = false


    @SuppressLint("BinaryOperationInTimber")
    suspend override fun observeGameDataList(): Flow<Result<List<GameData>>>
    {


        if (!isGettingContinuousUpdates)
        {

            CoroutineScope(Dispatchers.Default + coroutineContext).launch {

                Timber.d("Before the cursed while loop: $isGettingContinuousUpdates")
                while (currentCoroutineContext().isActive)
                {
                    isGettingContinuousUpdates = true

                    //Fetch new data only when the elapsed time is more than the [refreshIntervalInMs]
                    if (System.currentTimeMillis() - lastTimeGameListFetched > refreshIntervalInMs || lastTimeGameListFetched == -1L)
                    {
                        gameDataListResultFlow.emit(getGameDataList())

                        lastTimeGameListFetched = System.currentTimeMillis()
                    }
                    else
                    {
                        Timber.i(
                                "GameData list is still fresh, not fetching again in ${refreshIntervalInMs / 1000} seconds. " +
                                        "Elapsed time: ${System.currentTimeMillis() - lastTimeGameListFetched}")
                    }
                    delay(refreshIntervalInMs)

                }//End of the while loop
                isGettingContinuousUpdates = false
            }

        }

        return gameDataListResultFlow
    }


    override suspend fun getGameDataList(): Result<List<GameData>> = getGameDataList(retryWhenFailedEnabled)


    @SuppressLint("BinaryOperationInTimber")
    override suspend fun getGameDataList(retryWhenFailedEnabled: Boolean): Result<List<GameData>> = withContext(ioDispatcher) {

        var result: Result<List<GameData>>? = null
        var list: List<GameData>
        try
        {
            val response = gameAPIService.getGameList().execute(retryWhenFailedEnabled)

            if (response.isSuccessful && response.body() != null)
            {
                Timber.i("Response is successful")
                list = response.body()!!.list
                list = list.sortedBy { it.name }
                result = Result.Success(list)
            }
            else
            {
                val errorMessage = "Failed to fetch the data from the remote database.}"
                Timber.w(errorMessage + "Error body: ${response.errorBody()}, Response body: ${response.body()}")
                result = Result.Error(errorMessage = errorMessage)
            }
        }
        catch (e: Exception)
        {
            val errorMessage = "Exception while trying to fetch data from the network database. Stacktrace: ${e.printStackTrace()}"

            Timber.w(errorMessage)
            result = Result.Error(exception = e, errorMessage = errorMessage)

        }
        finally
        {
            return@withContext result!!
        }

        //The IDE is not detecting we are returning to the lambda no matter what in the try/catch block for some reason.
        //Therefore, keep this in here
        result!!

    }

    override suspend fun getGameDetails(gameId: Int): Result<GameDetails> = withContext(ioDispatcher) {

        var gameDetailsResult: Result<GameDetails>? = null


        val response: retrofit2.Response<GameDetails>
        try
        {
            response = gameAPIService.getGameDetails(gameId).execute(retryWhenFailedEnabled)

            if (response.isSuccessful && response.body() != null)
            {
                Timber.i("Response is successful")

                gameDetailsResult = Result.Success(response.body()!!)
            }
            else if (response.body() == null)
            {
                val errorMessage = "Retrieved a null response from the remote database"
                Timber.w(errorMessage)

                gameDetailsResult = Result.Error(errorMessage = errorMessage)
            }
            else
            {
                Timber.w("Failed to fetch data from the network database. Error body: ${response.errorBody()}, Response body: ${response.body()}")

                gameDetailsResult = Result.Error()
            }
        }
        catch (e: Exception)
        {
            val errorMessage = "Exception while trying to fetch data from the network database. Stacktrace: ${e.printStackTrace()}"
            Timber.w(errorMessage)

            gameDetailsResult = Result.Error(exception = e, errorMessage = errorMessage)
        }
        finally
        {
            return@withContext gameDetailsResult!!
        }

        //The IDE is not detecting we are returning to the lambda no matter what in the try/catch block for some reason.
        //Therefore, keep this in here
        gameDetailsResult!!

    }


}