package com.developerkurt.gamedatabase.ui


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import com.developerkurt.developerkurtmodule.ModuleDialog
import com.developerkurt.developerkurtmodule.dataStore
import com.developerkurt.gamedatabase.R
import com.developerkurt.gamedatabase.di.app_modules.GameRepositoryModule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.core.StringContains.containsString
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*


@HiltAndroidTest
@UninstallModules(GameRepositoryModule::class)
@MediumTest
class ModuleTest
{
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val context = InstrumentationRegistry.getInstrumentation().getTargetContext()

    private lateinit var activityScenario: ActivityScenario<MainActivity>


    @Before
    fun init()
    {
        hiltRule.inject()
        activityScenario = launchActivity<MainActivity>().moveToState(Lifecycle.State.RESUMED)

    }

    @Test
    fun isDisplayingFloatingActionButton()
    {
        onView(withId(R.id.module_fab)).check(matches(isDisplayed()))
    }

    @Test
    fun isDisplayingModuleDialog()
    {
        onView(withId(R.id.module_fab)).perform(click())
        onView(withId(R.id.module_dialog)).check(matches(isDisplayed()))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun isAddingTheInstallationDate()
    {
        //First clear the datastore to prevent false-positives
        runBlocking {
            context.dataStore.edit {
                it.remove(ModuleDialog.KEY_MODULE_INSTALLATION_DATE)
            }
        }


        onView(withId(R.id.module_fab)).perform(click())

        val currentDateInInstallationFormat = ModuleDialog.installationDateFormat().format(Calendar.getInstance().time)

        onView(withId(R.id.module_tv_current_date)).check(matches(withText(containsString(currentDateInInstallationFormat))))

    }

    @ExperimentalCoroutinesApi
    @Test
    fun isTheInstallationDatePreserved()
    {


        val previousInstallationDate = ModuleDialog.installationDateFormat().format(Date(2021, 5, 29))
        runBlocking {
            context.dataStore.edit {
                it[ModuleDialog.KEY_MODULE_INSTALLATION_DATE] = previousInstallationDate
            }


            activityScenario.recreate().moveToState(Lifecycle.State.RESUMED)
            onView(withId(R.id.module_fab)).perform(click())

            onView(withId(R.id.module_tv_installation_date)).check(matches(withText(containsString(previousInstallationDate))))

        }

    }


}
