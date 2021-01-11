package com.developerkurt.gamedatabase.data.source.remote

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.developerkurt.gamedatabase.data.source.Result
import com.developerkurt.gamedatabase.data.source.remote.api.DefaultGameAPIServiceGenerator
import com.developerkurt.gamedatabase.util.enqueueNotFound
import com.developerkurt.gamedatabase.util.enqueueSuccessGameDetails
import com.developerkurt.gamedatabase.util.enqueueSuccessGameList
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@MediumTest
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class DefaultRemoteDataSourceTest
{

    private val refreshTime = 1000L

    private lateinit var mockServer: MockWebServer
    private lateinit var defaultRemoteDataSourceTest: DefaultRemoteGameDataSource

    @Before
    fun init()
    {
        mockServer = MockWebServer()

        defaultRemoteDataSourceTest = DefaultRemoteGameDataSource(
                DefaultGameAPIServiceGenerator(mockServer.url("/").toString()).create(),
                Dispatchers.Main,
                false,
                refreshTime)
    }

    @After
    fun tearDown()
    {
        mockServer.shutdown()
    }

    @Test
    fun getGameDetailsReturnsErrorOnNotFound() = runBlockingTest {
        mockServer.enqueueNotFound()

        assert(defaultRemoteDataSourceTest.getGameDetails(0) is Result.Error)
    }

    @Test
    fun getGameDetailsReturnsSuccessOnSuccess() = runBlockingTest {
        mockServer.enqueueSuccessGameDetails()

        val result = defaultRemoteDataSourceTest.getGameDetails(0)

        assert(result is Result.Success)

        result as Result.Success

        assert(result.data.name == "Grand Theft Auto V")
    }

    @Test
    fun getGameDataListReturnsErrorOnNotFound() = runBlockingTest {
        mockServer.enqueueNotFound()

        assert(defaultRemoteDataSourceTest.getGameDataList() is Result.Error)
    }

    @InternalCoroutinesApi
    @Test
    fun observeGameDataListObserversCorrectly() = runBlocking {
        mockServer.enqueueNotFound()

        var firstFlowCollected = false
        var secondFlowCollected = false


        var job: Job? = null

        job = async {
            defaultRemoteDataSourceTest.observeGameDataList().take(2).collect {

                if (!firstFlowCollected)
                {
                    firstFlowCollected = true
                    assert(it is Result.Error)

                    mockServer.enqueueSuccessGameList()
                }
                else
                {
                    secondFlowCollected = true
                    assert(it is Result.Success)
                    assert((it as Result.Success).data.size == 20)

                    job!!.cancel()
                }
            }
        }

        try
        {
            job.await()
        }
        catch (e: CancellationException)
        {
            assert(firstFlowCollected)
            assert(secondFlowCollected)
        }

    }


}