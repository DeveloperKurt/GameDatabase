package com.developerkurt.gamedatabase.data

abstract class BaseRepository
{
    enum class RepositoryConfig
    {
        /**
         * Starts by checking the local cache and follows it by sending continuous rapid network requests at
         * intervals specified in [refreshIntervalInMs]
         */
        LOCAL_FIRST_CONTINUOUS_NETWORK_REFRESH,

        /**
         * Starts by checking the local cache and returns the data if found any, otherwise tries to
         * fetch the data until the request timeouts. Follows it by sending infrequent requests
         * at intervals specified in [dataExpirationDurationInMs]
         */
        LOCAL_UNTIL_STALE,

        /**
         * Left this one up to you to figure out
         */
        NETWORK_ONLY
    }

    interface ErrorListener
    {
        fun onError()
    }
}