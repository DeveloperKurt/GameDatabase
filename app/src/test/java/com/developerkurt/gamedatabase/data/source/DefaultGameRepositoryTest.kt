package com.developerkurt.gamedatabase.data.source

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.MediumTest
import com.developerkurt.gamedatabase.data.model.GameData
import com.developerkurt.gamedatabase.data.model.GameDetails
import com.developerkurt.gamedatabase.data.source.fake.FakeLocalGameDataSource
import com.developerkurt.gamedatabase.data.source.fake.FakeRemoteDataSource
import com.developerkurt.gamedatabase.util.getOrAwaitValue
import com.developerkurt.gamedatabase.util.observeForTesting
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.robolectric.annotation.Config

@RunWith(Parameterized::class)
@MediumTest
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class DefaultGameRepositoryTest(var repositoryConfig: DefaultGameRepository.RepositoryConfig)
{

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()


    /**
     * This companion object is being used to run this class's tests with every config that exists
     */
    companion object
    {
        @JvmStatic
        @Parameterized.Parameters
        fun repositoryConfigs() = DefaultGameRepository.RepositoryConfig.values()
    }


    private lateinit var fakeLocalGameDataSource: FakeLocalGameDataSource
    private lateinit var fakeRemoteDataSource: FakeRemoteDataSource

    private lateinit var defaultGameRepository: DefaultGameRepository

    private val gameDetails1 = GameDetails("1", "1", "1", "1", 1)
    private val gameData1 = GameData(1, "1", "1", "1", 1f)
    private val gameData2 = GameData(2, "2", "2", "2", 2f)
    private val gameData3 = GameData(3, "3", "3", "3", 3f)

    private var isCurrentConfigSupportingLocalSource: Boolean = true

    @Before
    fun init()
    {
        println("Initializing the tests with the following config: $repositoryConfig ")
        isCurrentConfigSupportingLocalSource = (repositoryConfig == DefaultGameRepository.RepositoryConfig.LOCAL_FIRST_CONTINUOUS_NETWORK_REFRESH ||
                repositoryConfig == DefaultGameRepository.RepositoryConfig.LOCAL_FIRST_REFRESH_ONCE)

        fakeLocalGameDataSource = FakeLocalGameDataSource()
        fakeRemoteDataSource = FakeRemoteDataSource()
        defaultGameRepository = DefaultGameRepository(fakeRemoteDataSource, fakeLocalGameDataSource, repositoryConfig)

    }

    @ExperimentalCoroutinesApi
    @Test
    fun isGetGameDetailsReturningCorrectValue() = runBlockingTest {

        assert(defaultGameRepository.getGameDetails(1) is Result.Error)

        fakeRemoteDataSource.gameDetails = gameDetails1
        val successResult = defaultGameRepository.getGameDetails(1)


        assert(successResult is Result.Success)
        successResult as Result.Success
        assert(successResult.data.name == "1")
    }

    @ExperimentalCoroutinesApi
    @Test
    fun isUpdateIsFavoriteUpdatingLocalSourceData() = runBlockingTest {
        gameData1.isInFavorites = false
        fakeLocalGameDataSource.gameList = mutableListOf(gameData1)

        defaultGameRepository.updateIsFavorite(1, true)

        assert(fakeLocalGameDataSource.gameList!![0].isInFavorites)

    }


    /**
     * When [gameData1], [gameData2], [gameData3] are given to the local repository,
     * remote data source updates [gameData1],  deletes [gameData2], returns an identical [gameData3], and adds a new GameData: gameData4
     *
     * This test checks if the received updated are reflected correctly when they are getting cached
     */

    @ExperimentalCoroutinesApi
    @Test
    fun areRemoteUpdatesCachedProperly() = runBlockingTest {

        //Perform the tests only to the configs that supports caching
        if (isCurrentConfigSupportingLocalSource)
        {

            gameData1.isInFavorites = true
            val localCachedList = mutableListOf(gameData1, gameData2, gameData3)
            fakeLocalGameDataSource.gameList = localCachedList

            val updatedGameData1 = GameData(1, "1.2", "1.2", "1.2", 1.2f)
            val gameData4 = GameData(4, "4", "4", "4", 4f)

            val remoteUpdatedList = mutableListOf(updatedGameData1, gameData3, gameData4)
            fakeRemoteDataSource.gameList = remoteUpdatedList

            var assertionsMade = false

            var job: Job? = null

            job = async {
                defaultGameRepository.observeGameDataList().getOrAwaitValue {


                    Assert.assertTrue(
                            "Differences found in the cached data, remote updates were not cached properly:" +
                                    "\nlocalSource: ${fakeLocalGameDataSource.gameList} " +
                                    "\nremoteSource: ${fakeRemoteDataSource.gameList} ",
                            fakeLocalGameDataSource.gameList == remoteUpdatedList)

                    Assert.assertTrue("isFavorite state of the updated data was not preserved:",
                            fakeLocalGameDataSource.gameList!!.find { it == updatedGameData1 }!!.isInFavorites)

                    assertionsMade = true
                    job!!.cancel()
                }
            }

            assert(assertionsMade)

        }
        else
        {
            println("This config does not support caching, passing the test.")
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun isCachingProperlyWhenLocalSourceIsEmpty() = runBlockingTest {
        //Perform the tests only to the configs that supports caching
        if (isCurrentConfigSupportingLocalSource)
        {
            val remoteUpdatedList = mutableListOf(gameData1, gameData2, gameData3)
            fakeRemoteDataSource.gameList = remoteUpdatedList

            var assertionsMade = false

            var job: Job? = null

            job = async {
                defaultGameRepository.observeGameDataList().observeForTesting {

                    Assert.assertTrue(
                            "Differences found in the cached data, remote updates were not cached properly:" +
                                    "\nlocalSource: ${fakeLocalGameDataSource.gameList} " +
                                    "\nremoteSource: ${fakeRemoteDataSource.gameList} ",
                            fakeLocalGameDataSource.gameList == remoteUpdatedList)


                    assertionsMade = true
                    job!!.cancel()
                }
            }

            assert(assertionsMade)

        }
        else
        {
            println("This config does not support caching, passing the test.")
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun isPrepareGameDataListSavingToLocalSource() = runBlockingTest {

        if (isCurrentConfigSupportingLocalSource)
        {
            val remoteUpdatedList = mutableListOf(gameData1, gameData2, gameData3)
            fakeRemoteDataSource.gameList = remoteUpdatedList

            defaultGameRepository.prepareGameDataList()

            assert(fakeLocalGameDataSource.gameList == remoteUpdatedList)
        }
        else
        {
            println("This config does not support local source, passing the test.")
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun isObserveGameDataListIgnoringRemoteUpdateWhenEqualToLocal() = runBlockingTest {


        if (isCurrentConfigSupportingLocalSource)
        {

            val localCachedList = mutableListOf(gameData1, gameData2, gameData3)
            fakeLocalGameDataSource.gameList = localCachedList

            fakeRemoteDataSource.gameList = localCachedList

            var job: Job? = null

            var result: Result<List<GameData>>? = null
            job = async {

                result = defaultGameRepository.observeGameDataList().getOrAwaitValue()
                job!!.cancel()
            }

            Assert.assertEquals(Result.Success(localCachedList), result)
            Assert.assertEquals(0, fakeLocalGameDataSource.timesUpdatedData)
        }
        else
        {
            println("This config does not support caching, passing the test.")
        }

    }

    @ExperimentalCoroutinesApi
    @Test
    fun isObserveGameDataListWithContinuousRefreshConfigEmittingUpdates() = runBlockingTest {

        if (repositoryConfig == DefaultGameRepository.RepositoryConfig.LOCAL_FIRST_CONTINUOUS_NETWORK_REFRESH)
        {
            val initialList = mutableListOf(gameData1, gameData2)
            val updatedList = mutableListOf(gameData1, gameData2, gameData3)

            fakeLocalGameDataSource.gameList = mutableListOf(gameData1)
            fakeRemoteDataSource.gameList = initialList
            fakeRemoteDataSource.gameList = updatedList

            var job: Job? = null

            var result: Result<List<GameData>>? = null
            job = async {

                result = defaultGameRepository.observeGameDataList().getOrAwaitValue()
                job!!.cancel()
            }

            Assert.assertEquals(fakeRemoteDataSource.getGameDataList(), result)
        }
        else
        {
            println("This test is not applicable for the current config")
        }


    }


}
