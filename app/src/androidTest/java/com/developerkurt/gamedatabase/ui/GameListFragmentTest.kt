package com.developerkurt.gamedatabase.ui


import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.developerkurt.gamedatabase.di.GameRepositoryModule
import com.developerkurt.gamedatabase.di.NetworkModule
import com.developerkurt.gamedatabase.di.PersistenceModule
import com.developerkurt.gamedatabase.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
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

    //TODO implement
    @Test
    fun gameListFragmentTest()
    {
        launchFragmentInHiltContainer<GameListFragment> {

        }
    }
}
