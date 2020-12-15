package com.developerkurt.gamedatabase.data

import android.content.Context
import androidx.room.Room
import androidx.test.filters.MediumTest
import com.developerkurt.gamedatabase.FileReader
import com.developerkurt.gamedatabase.data.api.GameAPIService
import com.developerkurt.gamedatabase.data.api.GameAPIServiceGenerator
import com.developerkurt.gamedatabase.data.model.GameData
import com.developerkurt.gamedatabase.data.model.GameDataList
import com.developerkurt.gamedatabase.data.persistence.RoomAppDatabase
import com.developerkurt.gamedatabase.di.GameRepositoryModule
import com.developerkurt.gamedatabase.di.NetworkModule
import com.developerkurt.gamedatabase.di.PersistenceModule
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.*
import javax.inject.Inject
import javax.inject.Singleton

@UninstallModules(NetworkModule::class, GameRepositoryModule::class, PersistenceModule::class)
@HiltAndroidTest
@MediumTest
class GameRepositoryTest
{

    private val HEADER_CONTENT_TYPE_VALUE = "application/json; charset=utf-8"
    private val HEADER_CONTENT_TYPE = "Content-Type"


    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var mockServer: MockWebServer

    @Inject
    lateinit var gameRepository: GameRepository

    @Inject
    lateinit var roomDatabase: RoomAppDatabase

    @Before
    fun init()
    {
        hiltRule.inject()
    }

    @After
    fun tearDown()
    {
        mockServer.shutdown()
    }


    @Test
    fun isReturningTheCachedGameDataRightAway() = runBlocking {

        val cachedList = cacheDefaultGameDataList().sortedBy { it.name }
        val returnedList = gameRepository.getGameDataFlow().first().sortedBy { it.name }
        assert(returnedList == cachedList, {
            "Returned list wasn't equal with the cached list." +
                    "\n ReturnedList: $returnedList" +
                    "\n CachedList: $cachedList"
        })
    }

    /**
     * Updated response deletes GTA V, adds GTA VI, and changes the Doom's rating to a full score of 5.0 out of the fear of the doom guy's retaliation
     *
     * Because I know he will come, just as he always had, as he always will, to feast on the blood of the wicked.
     * For he alone could draw strength from his fallen foes, and ever his power grew, swift and unrelenting.
     */
    @Test
    fun doesTheGameDataCacheGetsUpdatedProperly()
    {
        runBlocking {

            val firstList = cacheDefaultGameDataList().sortedBy { it.name }

            mockServer.enqueue(
                    MockResponse().setResponseCode(200)
                        .addHeader(HEADER_CONTENT_TYPE, HEADER_CONTENT_TYPE_VALUE)
                        .setBody(FileReader.readStringFromFile("success_game_list_updated_response.json")))


            val job = GlobalScope.launch {
                gameRepository.startGettingGameDataUpdates()

            }

            var updatedList: List<GameData>? = null

            //First dropped one is the cached list, 2nd one is the one with the handled addition/deletion operation
            //and the 3th one that we collect is the final list that contains the updated one on top of it
            gameRepository.getGameDataFlow().drop(2).take(1).collect {
                job.cancel()
                updatedList = it.sortedBy { it.name }
            }

            val gtaVI = GameData(
                    6666,
                    "Grand Theft Auto VI",
                    "2020-09-17",
                    "https://media.rawg.io/media/games/84d/84da2ac3fdfc6507807a1808595afb12.jpg",
                    4.48f)

            val gtaV = GameData(
                    3498,
                    "Grand Theft Auto V",
                    "2013-09-17",
                    "https://media.rawg.io/media/games/84d/84da2ac3fdfc6507807a1808595afb12.jpg",
                    4.48f)

            val doom = GameData(
                    2454,
                    "DOOM (2016)",
                    "2016-05-13",
                    "https://media.rawg.io/media/games/c4b/c4b0cab189e73432de3a250d8cf1c84e.jpg",
                    4.39f)

            val updatedDoom = GameData(
                    2454,
                    "DOOM (2016)",
                    "2016-05-13",
                    "https://media.rawg.io/media/games/c4b/c4b0cab189e73432de3a250d8cf1c84e.jpg",
                    5.0f)

            assert(updatedList != null)
            assert(updatedList != firstList)

            assert(updatedList!!.contains(gtaVI) && !updatedList!!.contains(gtaV), {
                "Addition or deletion was not applied to the cached list" +
                        "1st statement: ${updatedList!!.contains(gtaVI)}, 2nd statement: ${!updatedList!!.contains(gtaV)} "
            })

            assert(updatedList!!.contains(updatedDoom) && !updatedList!!.contains(doom), {
                "Updating an existing element wasn't done properly." +
                        "1st statement: ${updatedList!!.contains(updatedDoom)}, 2nd statement: ${!updatedList!!.contains(doom)} "
            })

            assert(updatedList!!.filter { it.name == "DOOM (2016)" }[0].isInFavorites == true,
                    { "Favorite state was not preserved when the element received an update" })

            assert(updatedList!!.size == firstList.size)

        }
    }

    @Test
    fun doesGameListsDataStatesChangeCorrectly()
    {
        val failMockResponse = MockResponse().setResponseCode(404)
            .addHeader(HEADER_CONTENT_TYPE, HEADER_CONTENT_TYPE_VALUE)
            .setBody(FileReader.readStringFromFile("not_found_response.json"))

        val successMockResponse = MockResponse().setResponseCode(200)
            .addHeader(HEADER_CONTENT_TYPE, HEADER_CONTENT_TYPE_VALUE)
            .setBody(FileReader.readStringFromFile("success_game_list_response.json"))



        runBlocking {

            //----------------Test the default state------------
            Assert.assertTrue(
                    "Default state wasn't UNKNOWN",
                    gameRepository.gameDataStateFlow().first() == BaseRepository.DataState.UNKNOWN)


            val job = GlobalScope.launch {
                gameRepository.startGettingGameDataUpdates()

            }

            //------------Test the fail state when there is no cached data------------
            mockServer.enqueue(failMockResponse)


            gameRepository.gameDataStateFlow().drop(1).take(1).collect { failedDataState ->
                assert(failedDataState == BaseRepository.DataState.FAILED, { "Data state was not FAILED. dataState: ${failedDataState}" })


                //------------Test the success state------------
                mockServer.enqueue(successMockResponse)

                //Wait for the response to be emitted to the flow, so we can know when to first loop completed. First drop is the empty list.
                gameRepository.getGameDataFlow().drop(1).take(1).collect {

                    assert(gameRepository.gameDataStateFlow().first() == BaseRepository.DataState.SUCCESS)


                    //------------Test the fail state when there is cached data------------
                    mockServer.enqueue(failMockResponse)


                    gameRepository.gameDataStateFlow().drop(1).take(1).collect { failedToUpdateDataState ->

                        assert(failedToUpdateDataState == BaseRepository.DataState.FAILED_TO_UPDATE,
                                { "Data state was not FAILED_TO_UPDATE. dataState: ${failedToUpdateDataState}" })
                        job.cancel()
                    }
                }
            }
        }
    }


    @Test
    fun isFetchGameDetailsOnceFromDatabaseInvokingTheRightDataStates() = runBlocking {

        //Test the default state
        Assert.assertTrue(
                "Default state wasn't UNKNOWN",
                gameRepository.gameDetailsStateFlow().first() == BaseRepository.DataState.UNKNOWN)


        //Test the success state
        mockServer.enqueue(
                MockResponse().setResponseCode(200)
                    .addHeader(HEADER_CONTENT_TYPE, HEADER_CONTENT_TYPE_VALUE)
                    .setBody(FileReader.readStringFromFile("success_game_details_response.json")))

        gameRepository.fetchGameDetailsOnceFromDatabase(0)

        val stateAfterSuccessfulReply: BaseRepository.DataState = gameRepository.gameDetailsStateFlow().first()

        Assert.assertTrue(
                "DataState didn't change to success after receiving response code 200. DataState: $stateAfterSuccessfulReply",
                stateAfterSuccessfulReply == BaseRepository.DataState.SUCCESS)


        //Test the fail state
        mockServer.enqueue(
                MockResponse().setResponseCode(404)
                    .addHeader(HEADER_CONTENT_TYPE, HEADER_CONTENT_TYPE_VALUE)
                    .setBody(FileReader.readStringFromFile("not_found_response.json")))


        gameRepository.fetchGameDetailsOnceFromDatabase(0)

        val stateAfterFailedReply: BaseRepository.DataState = gameRepository.gameDetailsStateFlow().first()

        Assert.assertTrue(
                "DataState didn't change to failed  after receiving an error response code. DataState: $stateAfterFailedReply",
                stateAfterFailedReply == BaseRepository.DataState.FAILED)


    }


    private suspend fun cacheDefaultGameDataList(): List<GameData>
    {
        val gameList: GameDataList = Gson().fromJson(FileReader.readStringFromFile("success_game_list_response.json"), GameDataList::class.java)

        gameList.list.filter { it.name == "DOOM (2016)" }[0].isInFavorites = true

        roomDatabase.gameDataDao().insert(*gameList.list.toTypedArray())
        return gameList.list
    }


}

@InstallIn(SingletonComponent::class)
@Module
class NetworkTestingModule
{

    @Singleton
    @Provides
    fun provideMockWebServer(): MockWebServer = MockWebServer()


    @Singleton
    @Provides
    fun provideGameAPIService(mockServer: MockWebServer): GameAPIService
    {
        return GameAPIServiceGenerator(baseUrl = mockServer.url("/").toString()).create()
    }

}

@InstallIn(SingletonComponent::class)
@Module
class PersistenceTestingModule
{

    @Singleton
    @Provides
    fun provideRoomDatabase(@ApplicationContext applicationContext: Context): RoomAppDatabase
    {
        return Room.inMemoryDatabaseBuilder(
                applicationContext, RoomAppDatabase::class.java).build()

    }
}

//Disable retry when failed to save from execution time.
@InstallIn(SingletonComponent::class)
@Module
class GameRepositoryTestingModule
{
    @Singleton
    @Provides
    fun provideGameRepository(
            apiService: GameAPIService,
            roomAppDatabase: RoomAppDatabase): GameRepository
    {
        return GameRepository.GameRepositoryBuilder()
            .setApiService(apiService)
            .setRoomDatabase(roomAppDatabase)
            .setConfig(BaseRepository.RepositoryConfig.LOCAL_FIRST_CONTINUOUS_NETWORK_REFRESH)
            .setShouldRetryWhenFailed(false)
            .setRefreshIntervalInMs(0L)
            .create()
    }
}


