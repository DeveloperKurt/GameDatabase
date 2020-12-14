package com.developerkurt.gamedatabase

import com.developerkurt.gamedatabase.util.RetrofitExtensionConstants
import com.developerkurt.gamedatabase.util.execute
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Test
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET


class RetrofitRetryTest
{
    private val mockServer = MockWebServer()

    private val failResponse = MockResponse().setResponseCode(404)
    private val successResponse = MockResponse().setResponseCode(200)

    private val api = Retrofit.Builder()
        .baseUrl(mockServer.url("/").toString())
        .client(OkHttpClient.Builder().build())
        .build()
        .create(TestAPI::class.java)


    /**NOTE: If you get the error "shorten command line"  while running this test add the following line in workspace.xml under .idea
     *
     *`<property name="dynamic.classpath" value="true"/>` in between `<component name="PropertiesComponent">  </component>`
     */
    @Test
    fun `is retrying when the request fails`()
    {
        repeat(RetrofitExtensionConstants.defaultMaxRetryCount) {
            mockServer.enqueue(failResponse)
        }

        val timeBeforeRequest = System.currentTimeMillis()
        api.getTest().execute(true)
        val timeAfterRequest = System.currentTimeMillis()

        val diff = timeAfterRequest - timeBeforeRequest
        val expectedDiff = RetrofitExtensionConstants.defaultRetryIntervalInMs * RetrofitExtensionConstants.defaultMaxRetryCount

        assert(diff >= expectedDiff,
                { "Difference: $diff, expected: $expectedDiff" })
    }

    @Test
    fun `is stopping retrying when received a successful response`()
    {
        repeat(5) {
            mockServer.enqueue(failResponse)
        }
        mockServer.enqueue(successResponse)

        val timeBeforeRequest = System.currentTimeMillis()
        val response = api.getTest().execute(true, maxRetryCount = 15)
        val timeAfterRequest = System.currentTimeMillis()

        val diff = timeAfterRequest - timeBeforeRequest
        val maxExpectedDiff = (RetrofitExtensionConstants.defaultRetryIntervalInMs * 15)

        assert(diff < maxExpectedDiff,
                { "Difference: $diff, expected: $maxExpectedDiff" })

        assert(response.code() == 200)
    }


    @Test
    fun `is retryWhenFailed parameter respected`()
    {
        repeat(2) {
            mockServer.enqueue(failResponse)
        }

        val timeBeforeRequest = System.currentTimeMillis()
        val response = api.getTest().execute(false, maxRetryCount = 15)
        val timeAfterRequest = System.currentTimeMillis()

        val diff = timeAfterRequest - timeBeforeRequest

        val estimatedTimeToGetAResponse = 200


        assert(diff < estimatedTimeToGetAResponse,
                { "Difference: $diff, expected: $estimatedTimeToGetAResponse" })

    }


    interface TestAPI
    {
        @GET("test")
        fun getTest(): Call<ResponseBody>
    }
}