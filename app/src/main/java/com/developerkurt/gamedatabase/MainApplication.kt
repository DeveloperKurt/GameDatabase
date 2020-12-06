package com.developerkurt.gamedatabase

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MainApplication : Application()
{
    override fun onCreate()
    {
        super.onCreate()

        //Display the logs only in debug mode.
        if (BuildConfig.DEBUG)
        {
            Timber.plant(Timber.DebugTree())
        }

    }
}