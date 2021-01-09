package com.developerkurt.gamedatabase.data

import androidx.test.filters.LargeTest
import com.developerkurt.gamedatabase.data.api.GameAPIService
import com.developerkurt.gamedatabase.di.GameRepositoryTestingModule
import com.developerkurt.gamedatabase.di.NetworkTestingModule
import com.developerkurt.gamedatabase.di.PersistenceTestingModule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
@UninstallModules(NetworkTestingModule::class, GameRepositoryTestingModule::class, PersistenceTestingModule::class)
@LargeTest //Makes network calls
class GameAPIServiceTest
{
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var apiService: GameAPIService

    @Before
    fun init()
    {
        hiltRule.inject()
    }

    @Test
    fun areTheServerURLsAndDeserializingWorking()
    {
        val gameDataList = apiService.getGameList().execute().body()

        assert(gameDataList != null && gameDataList.list.size > 0)

        assert(apiService.getGameDetails(gameDataList!!.list[0].id).execute().body() != null)

    }
}