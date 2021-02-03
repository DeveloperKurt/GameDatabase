package com.developerkurt.gamedatabase.ui


import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.typeText
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
class GameListFragmentTest
{
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()


    @Inject
    lateinit var repository: GameRepository

    private val gameData1 = GameData(1, "1111", "1", "1", 1f)
    private val gameData2 = GameData(2, "2222", "2", "2", 2f)

    @Before
    fun init()
    {
        hiltRule.inject()
    }

    @Test
    fun areTheDisplayedViewsCorrectWhenWaitingForDataRetrieval()
    {

        launchFragmentInHiltContainer<GameListFragment>(Bundle(), R.style.Theme_GameDatabase)
        assertLoadingStateVisibilities()
    }

    @Test
    fun areTheDisplayedViewsCorrectOnFailedDataRetrieval()
    {
        (repository as FakeGameRepository).gameList = null
        launchFragmentInHiltContainer<GameListFragment>(Bundle(), R.style.Theme_GameDatabase)

        assertErrorStateVisibilities()


    }

    @Test
    fun areTheDisplayedViewsCorrectOnSucceededDataRetrieval()
    {
        (repository as FakeGameRepository).gameList = mutableListOf(gameData1)
        launchFragmentInHiltContainer<GameListFragment>(Bundle(), R.style.Theme_GameDatabase)

        assertSuccessStateVisibilities()

    }


    @Test
    fun viewsRespondToDataChangeCorrectly()
    {
        launchFragmentInHiltContainer<GameListFragment>(Bundle(), R.style.Theme_GameDatabase)
        assertLoadingStateVisibilities()

        (repository as FakeGameRepository).gameList = mutableListOf(gameData1)
        assertSuccessStateVisibilities()

        (repository as FakeGameRepository).gameList = mutableListOf()
        assertErrorStateVisibilities()

        (repository as FakeGameRepository).gameList = mutableListOf(gameData1)
        assertSuccessStateVisibilities()
    }

    @Test
    fun searchBarIgnoresSearchTermsShorterThan3()
    {
        (repository as FakeGameRepository).gameList = mutableListOf(gameData1, gameData2)
        launchFragmentInHiltContainer<GameListFragment>(Bundle(), R.style.Theme_GameDatabase)

        onView(withId(R.id.et_search)).perform(typeText("11"))

        //close the keyboard
        Espresso.pressBack()


        onView(withId(R.id.tv_no_results)).check(matches(not(isDisplayed())))
        //verify the displayed items are not filtered
        onView(withId(R.id.recycler_view_game_data)).check(matches(hasDescendant(withText(gameData1.name))))
        onView(withId(R.id.recycler_view_game_data)).check(matches(hasDescendant(withText(gameData2.name))))
    }

    @Test
    fun searchBarDisplaysTheRightResults()
    {
        (repository as FakeGameRepository).gameList = mutableListOf(gameData1, gameData2)
        launchFragmentInHiltContainer<GameListFragment>(Bundle(), R.style.Theme_GameDatabase)

        onView(withId(R.id.et_search)).perform(typeText("111"))

        onView(withId(R.id.view_pager_game_images)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tl_view_pager_scroll)).check(matches(not(isDisplayed())))

        onView(withId(R.id.recycler_view_game_data)).check(matches(hasDescendant(withText(gameData1.name))))
        onView(withId(R.id.recycler_view_game_data)).check(matches(not(hasDescendant(withText(gameData2.name)))))
    }

    private fun assertLoadingStateVisibilities()
    {
        onView(withId(R.id.progressBar)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_no_results)).check(matches(not(isDisplayed())))
    }

    private fun assertSuccessStateVisibilities()
    {
        onView(withId(R.id.progressBar)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tv_no_results)).check(matches(not(isDisplayed())))
        onView(withId(R.id.inc_error_layout)).check(matches(not(isDisplayed())))

        onView(withId(R.id.view_pager_game_images)).check(matches(isDisplayed()))
        onView(withId(R.id.recycler_view_game_data)).check(matches(isDisplayed()))
        onView(withId(R.id.tl_view_pager_scroll)).check(matches(isDisplayed()))
    }

    private fun assertErrorStateVisibilities()
    {
        onView(withId(R.id.progressBar)).check(matches(not(isDisplayed())))
        onView(withId(R.id.view_pager_game_images)).check(matches(not(isDisplayed())))
        onView(withId(R.id.recycler_view_game_data)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tl_view_pager_scroll)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tv_no_results)).check(matches(not(isDisplayed())))

        onView(withId(R.id.inc_error_layout)).check(matches(isDisplayed()))
    }

}
