package com.developerkurt.gamedatabase.data.source.local

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.developerkurt.gamedatabase.data.model.GameData
import com.developerkurt.gamedatabase.data.source.Result
import com.developerkurt.gamedatabase.data.source.local.room.RoomAppDatabase
import com.developerkurt.gamedatabase.util.MainCoroutineRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class RoomLocalDataSourceTest
{
    private lateinit var localDataSource: RoomLocalGameDataSource
    private lateinit var database: RoomAppDatabase

    private val gameData1: GameData by lazy { GameData(0, "Doom", "2016", "URL", 5.0f) }
    private val gameData2: GameData by lazy { GameData(1, "Doom Eternal", "2020", "URL", 5.0f) }

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()


    @Before
    fun setup()
    {
        // using an in-memory database for testing, since it doesn't survive killing the process
        database = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                RoomAppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        localDataSource = RoomLocalGameDataSource(database.gameDataDao(), Dispatchers.Main)
    }

    @After
    fun cleanUp()
    {
        database.close()
    }

    @Test
    fun isDatabaseEmptyReturnsTrueWhenEmpty() = runBlockingTest {
        assert(localDataSource.isDatabaseEmpty())

        localDataSource.insertGameData(gameData1)

        assert(!localDataSource.isDatabaseEmpty())
    }

    @InternalCoroutinesApi
    @Test
    fun isObserveGameDataListGettingUpdates() = runBlockingTest {

        localDataSource.insertGameData(gameData1)

        localDataSource.observeGameDataList().take(1).collect {
            assert(it is Result.Success)

            it as Result.Success

            assert(it.data[0] == gameData1)
        }

        localDataSource.insertGameData(gameData2)

        localDataSource.observeGameDataList().take(1).collect {
            assert(it is Result.Success)

            it as Result.Success

            assert(it.data.contains(gameData1) && it.data.contains(gameData2))
        }

    }

    @Test
    fun isGetGameDataListReturningRightResults() = runBlockingTest {

        println(localDataSource.getGameDataList())
        assert(localDataSource.getGameDataList() is Result.Error)

        localDataSource.insertGameData(gameData1)

        assert(localDataSource.getGameDataList() is Result.Success)

    }


    @Test
    fun isUpdateIsFavoriteUpdatingCorrectly() = runBlockingTest {
        localDataSource.insertGameData(gameData1)

        assert(!(localDataSource.getGameDataList() as Result.Success).data[0].isInFavorites)

        localDataSource.updateIsFavorite(gameData1.id, 1)

        assert((localDataSource.getGameDataList() as Result.Success).data[0].isInFavorites)

    }


    @Test
    fun isUpdateGameDataUpdatingCorrectly() = runBlockingTest {
        localDataSource.insertGameData(gameData1)

        localDataSource.updateGameData(GameData(gameData1.id, "updated", "updated", "updated", 4f))

        val updatedGameData1 = (localDataSource.getGameDataList() as Result.Success).data[0]

        assert(updatedGameData1.id == gameData1.id)
        assert(updatedGameData1.name == "updated")
        assert(updatedGameData1.releaseDate == "updated")
        assert(updatedGameData1.imageUrl == "updated")
        assert(updatedGameData1.rating == 4f)
    }

    @Test
    fun isRemoveGameDataUpdatingRemoving() = runBlockingTest {
        localDataSource.insertGameData(gameData1)
        localDataSource.insertGameData(gameData2)

        localDataSource.removeGameData(gameData1)

        val gameDataList = (localDataSource.getGameDataList() as Result.Success).data

        assert(gameDataList.size == 1)
        assert(gameDataList[0] == gameData2)

    }

    @Test
    fun isInsertGameDataInserting() = runBlockingTest {

        localDataSource.insertGameData(gameData1)

        val gameDataList = (localDataSource.getGameDataList() as Result.Success).data

        assert(gameDataList.size == 1)
        assert(gameDataList[0] == gameData1)
    }


}