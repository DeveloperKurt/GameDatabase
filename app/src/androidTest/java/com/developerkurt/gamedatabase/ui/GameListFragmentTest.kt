package com.developerkurt.gamedatabase.ui


import androidx.test.filters.LargeTest
import com.developerkurt.gamedatabase.di.GameRepositoryModule
import com.developerkurt.gamedatabase.di.NetworkModule
import com.developerkurt.gamedatabase.di.PersistenceModule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@LargeTest
@UninstallModules(NetworkModule::class, GameRepositoryModule::class, PersistenceModule::class)
class GameListFragmentTest
{
    @get:Rule
    val hiltRule = HiltAndroidRule(this)


    @Before
    fun init()
    {
        hiltRule.inject()
    }

    @Test
    fun areTheDisplayedViewsCorrectWhenWaitingForDataRetrieval()
    {

    }

    @Test
    fun areTheDisplayedViewsCorrectOnFailedDataRetrieval()
    {

    }

    @Test
    fun areTheDisplayedViewsCorrectOnSucceededDataRetrieval()
    {

    }


    @Test
    fun viewsRespondToDataChangeCorrectly()
    {

    }

    @Test
    fun searchBarIgnoresSearchTermsShorterThan3()
    {

    }

}
