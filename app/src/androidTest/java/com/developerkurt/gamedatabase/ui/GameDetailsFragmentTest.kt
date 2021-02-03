package com.developerkurt.gamedatabase.ui


import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import com.developerkurt.gamedatabase.R
import com.developerkurt.gamedatabase.data.model.GameData
import com.developerkurt.gamedatabase.data.model.GameDetails
import com.developerkurt.gamedatabase.data.source.GameRepository
import com.developerkurt.gamedatabase.data.source.fake.FakeGameRepository
import com.developerkurt.gamedatabase.di.app_modules.GameRepositoryModule
import com.developerkurt.gamedatabase.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.hamcrest.CoreMatchers.not
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject


@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
@MediumTest
@UninstallModules(GameRepositoryModule::class)
class GameDetailsFragmentTest
{
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val context = InstrumentationRegistry.getInstrumentation().getTargetContext()
    private lateinit var gameDetails1: GameDetails
    private lateinit var gameDetails1Bundle: Bundle

    @Inject
    lateinit var repository: GameRepository


    @Before
    fun init()
    {
        hiltRule.inject()
        gameDetails1 = GameDetails("1", "11", "111", "1111", 111111)
        gameDetails1Bundle = Bundle()
        gameDetails1Bundle.putInt("gameId", 1)
        gameDetails1Bundle.putBoolean("isInFavorites", true)
    }


    @Test
    fun isDisplayingTheCorrectValues()
    {
        (repository as FakeGameRepository).gameDetails = gameDetails1

        launchFragmentInHiltContainer<GameDetailsFragment>(gameDetails1Bundle, R.style.Theme_GameDatabase)

        onView(withId(R.id.tv_game_title)).check(matches(withText(gameDetails1.name)))
        onView(withId(R.id.tv_metacritic_rate)).check(matches(withText(context.getString(R.string.metacritic_rate, gameDetails1.metacriticRate))))
        onView(withId(R.id.tv_release_date)).check(matches(withText(context.getString(R.string.release_date, gameDetails1.releaseDate))))
        onView(withId(R.id.tv_description)).check(matches(withText(gameDetails1.description)))


    }

    @Test
    fun isLikingOpsWorking()
    {
        (repository as FakeGameRepository).gameList = mutableListOf(GameData(1, "1", "111", "1111", 111111f))
        (repository as FakeGameRepository).gameDetails = gameDetails1

        launchFragmentInHiltContainer<GameDetailsFragment>(gameDetails1Bundle, R.style.Theme_GameDatabase)

        onView(withId(R.id.img_btn_add_to_favs)).perform(click())

        //Assert that the value of the game with id 1's isFavorite field change to false from true
        Assert.assertFalse((repository as FakeGameRepository).gameList!![0].isInFavorites)

        onView(withId(R.id.img_btn_add_to_favs)).perform(click())

        //Assert that the value of the game with id 1's isFavorite field change to true from false
        Assert.assertTrue((repository as FakeGameRepository).gameList!![0].isInFavorites)


    }


    @Test
    fun testViewsVisibilitiesOnError()
    {
        (repository as FakeGameRepository).isGameDetailsLoading = false
        (repository as FakeGameRepository).gameDetails = null

        launchFragmentInHiltContainer<GameDetailsFragment>(gameDetails1Bundle, R.style.Theme_GameDatabase)
        assertErrorStateVisibilities()
    }

    @Test
    fun testViewsVisibilitiesOnLoading()
    {
        (repository as FakeGameRepository).isGameDetailsLoading = true
        launchFragmentInHiltContainer<GameDetailsFragment>(gameDetails1Bundle, R.style.Theme_GameDatabase)

        assertLoadingStateVisibilities()
    }

    @Test
    fun testViewsVisibilitiesOnSuccess()
    {
        (repository as FakeGameRepository).isGameDetailsLoading = false
        (repository as FakeGameRepository).gameDetails = gameDetails1

        launchFragmentInHiltContainer<GameDetailsFragment>(gameDetails1Bundle, R.style.Theme_GameDatabase)

        assertSuccessStateVisibilities()
    }


    private fun assertLoadingStateVisibilities()
    {
        onView(withId(R.id.progressBar)).check(matches(isDisplayed()))

        onView(withId(R.id.inc_error_layout)).check(matches(not(isDisplayed())))
        onView(withId(R.id.iv_game_image)).check(matches(not(isDisplayed())))
        onView(withId(R.id.details_layout)).check(matches(not(isDisplayed())))
    }

    private fun assertSuccessStateVisibilities()
    {
        onView(withId(R.id.progressBar)).check(matches(not(isDisplayed())))
        onView(withId(R.id.inc_error_layout)).check(matches(not(isDisplayed())))

        onView(withId(R.id.iv_game_image)).check(matches(isDisplayed()))
        onView(withId(R.id.details_layout)).check(matches(isDisplayed()))
    }

    private fun assertErrorStateVisibilities()
    {
        onView(withId(R.id.progressBar)).check(matches(not(isDisplayed())))
        onView(withId(R.id.iv_game_image)).check(matches(not(isDisplayed())))
        onView(withId(R.id.details_layout)).check(matches(not(isDisplayed())))
        onView(withId(R.id.progressBar)).check(matches(not(isDisplayed())))

        onView(withId(R.id.inc_error_layout)).check(matches(isDisplayed()))
    }

}
