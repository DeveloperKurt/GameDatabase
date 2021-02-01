package com.developerkurt.gamedatabase.ui


import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.developerkurt.gamedatabase.R
import com.developerkurt.gamedatabase.data.model.GameData
import com.developerkurt.gamedatabase.data.source.GameRepository
import com.developerkurt.gamedatabase.data.source.fake.FakeGameRepository
import com.developerkurt.gamedatabase.di.app_modules.GameRepositoryModule
import com.developerkurt.gamedatabase.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
@MediumTest
@UninstallModules(GameRepositoryModule::class)
class FavoriteGamesFragmentTest
{
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()


    @Inject
    lateinit var repository: GameRepository

    private lateinit var gameData1: GameData
    private lateinit var gameData2: GameData

    @Before
    fun init()
    {
        hiltRule.inject()
        gameData1 = GameData(1, "1111", "1", "1", 1f)
        gameData1.isInFavorites = true
        gameData2 = GameData(2, "2222", "2", "2", 2f)
        gameData2.isInFavorites = false
    }


    @Test
    fun isOnlyTheFavoriteGamesDisplayed()
    {
        (repository as FakeGameRepository).gameList = mutableListOf(gameData1, gameData2)
        launchFragmentInHiltContainer<FavoriteGamesFragment>(Bundle(), R.style.Theme_GameDatabase)

        onView(withId(R.id.recycler_view_favorite_games)).check(matches(hasDescendant(withText(gameData1.name))))
        onView(withId(R.id.recycler_view_favorite_games)).check(matches(not(hasDescendant(withText(gameData2.name)))))
    }


    @Test
    fun areTheChangesFromRepositoryRespected()
    {
        (repository as FakeGameRepository).gameList = mutableListOf(gameData1, gameData2)
        launchFragmentInHiltContainer<FavoriteGamesFragment>(Bundle(), R.style.Theme_GameDatabase)

        onView(withId(R.id.recycler_view_favorite_games)).check(matches(hasDescendant(withText(gameData1.name))))
        onView(withId(R.id.recycler_view_favorite_games)).check(matches(not(hasDescendant(withText(gameData2.name)))))


        (repository as FakeGameRepository).gameList = mutableListOf(gameData2)


        onView(withId(R.id.recycler_view_favorite_games)).check(matches(not(hasDescendant(withText(gameData1.name)))))
    }

    @Test
    fun testViewsVisibilitiesOnError()
    {
        (repository as FakeGameRepository).gameList = null
        launchFragmentInHiltContainer<FavoriteGamesFragment>(Bundle(), R.style.Theme_GameDatabase)
        assertErrorStateVisibilities()
    }

    @Test
    fun testViewsVisibilitiesOnLoading()
    {
        launchFragmentInHiltContainer<FavoriteGamesFragment>(Bundle(), R.style.Theme_GameDatabase)
        assertLoadingStateVisibilities()
    }

    @Test
    fun testViewsVisibilitiesOnSuccess()
    {
        (repository as FakeGameRepository).gameList = mutableListOf(gameData1, gameData2)
        launchFragmentInHiltContainer<FavoriteGamesFragment>(Bundle(), R.style.Theme_GameDatabase)
        assertSuccessStateVisibilities()
    }


    private fun assertLoadingStateVisibilities()
    {
        onView(withId(R.id.progressBar)).check(matches(isDisplayed()))
        onView(withId(R.id.inc_error_layout)).check(matches(not(isDisplayed())))
    }

    private fun assertSuccessStateVisibilities()
    {
        onView(withId(R.id.progressBar)).check(matches(not(isDisplayed())))
        onView(withId(R.id.inc_error_layout)).check(matches(not(isDisplayed())))
    }

    private fun assertErrorStateVisibilities()
    {
        onView(withId(R.id.inc_error_layout)).check(matches(isDisplayed()))
        onView(withId(R.id.progressBar)).check(matches(not(isDisplayed())))

    }


}
