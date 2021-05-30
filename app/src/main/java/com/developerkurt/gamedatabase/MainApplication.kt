package com.developerkurt.gamedatabase

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MainApplication : Application()
{
    /**
     * ## Set to true to force the dark mode. Might come in handy if your android version doesn't support the night mode.
     */
    private val forceDarkMode = true


    override fun onCreate()
    {
        super.onCreate()

        //Display the logs only in debug mode.
        if (BuildConfig.DEBUG)
        {
            Timber.plant(Timber.DebugTree())
        }
        if (forceDarkMode)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

    }
}