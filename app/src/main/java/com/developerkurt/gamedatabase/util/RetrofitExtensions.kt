package com.developerkurt.gamedatabase.util

import com.developerkurt.gamedatabase.util.RetrofitExtensionConstants.defaultMaxRetryCount
import com.developerkurt.gamedatabase.util.RetrofitExtensionConstants.defaultRetryIntervalInMs
import retrofit2.Call
import timber.log.Timber

object RetrofitExtensionConstants
{
    const val defaultMaxRetryCount = 15
    const val defaultRetryIntervalInMs = 250L
}

fun <T> Call<T>.execute(
        retryWhenFailed: Boolean,
        maxRetryCount: Int = defaultMaxRetryCount,
        retryIntervalInMs: Long = defaultRetryIntervalInMs): retrofit2.Response<T>
{
    require(maxRetryCount >= 0)
    require(retryIntervalInMs >= 0)

    if (!retryWhenFailed)
    {
        return this.execute()
    }
    else
    {

        var response: retrofit2.Response<T>? = null
        var isSuccessful = false
        var tryCount = 0

        var lastException: java.lang.Exception? = null
        var retryClone: Call<T>? = null
        do
        {
            try
            {
                //First attempt, execute the original call
                if (retryClone == null)
                {
                    response = this.execute()
                }
                //Retry attempt, clone this call and try again
                else
                {
                    response = retryClone.execute()
                }
                isSuccessful = response.isSuccessful

            }
            catch (e: Exception)
            {
                Timber.w(e, "Request was not successful, tryCount: $tryCount")
                lastException = e
            }
            finally
            {
                if (!isSuccessful)
                {
                    Thread.sleep(retryIntervalInMs)

                    //Cancel the call, it failed
                    this.cancel()
                    retryClone?.cancel()

                    //Create a new clone to try again
                    retryClone = this.clone()
                    tryCount++
                }
                else
                {
                    //Succeeded, previous exception(s) are now irrelevant
                    lastException = null
                }

            }
        }
        while (!isSuccessful && tryCount < maxRetryCount)


        if (response == null || lastException != null)
        {
            throw lastException!!
        }
        else
            return response
    }
}