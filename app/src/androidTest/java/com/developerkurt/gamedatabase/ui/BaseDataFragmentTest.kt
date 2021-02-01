package com.developerkurt.gamedatabase.ui


import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.annotation.UiThreadTest
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import com.developerkurt.gamedatabase.R
import com.developerkurt.gamedatabase.TestBaseDataFragmentImp
import com.developerkurt.gamedatabase.data.source.Result
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import kotlin.reflect.KFunction


@RunWith(AndroidJUnit4::class)
@SmallTest
class BaseDataFragmentTest
{

    @Mock lateinit var mockBaseDataFragment: TestBaseDataFragmentImp

    private val context = InstrumentationRegistry.getInstrumentation().getTargetContext()
    private val failedToUpdateToastString: String = context.resources.getString(R.string.data_update_fail)

    private val fragmentScenario by lazy { launchFragmentInContainer<TestBaseDataFragmentImp>(Bundle(), R.style.Theme_AppCompat) }
    private val baseTransientBottomBarFadeAnimationDuration = 180L

    @Before
    fun init()
    {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun handlingTheLOADINGStateProperly()
    {
        mockBaseDataFragment.handleDataStateChange(Result.Loading)
        assertInvocationTimesForResultCallbacks(Result.Loading)
    }

    @Test
    @UiThreadTest
    fun handlingTheFAILED_TO_UPDATEStateProperly()
    {
        val mockitoSpy = spy(TestBaseDataFragmentImp::class.java)
        doNothing().`when`(mockitoSpy).showFailedToUpdateSnackBar()
        mockBaseDataFragment.handleDataStateChange(Result.FailedToUpdate)
        assertInvocationTimesForResultCallbacks(Result.FailedToUpdate)
    }

    @Test
    fun handlingTheFAILEStateProperly()
    {
        mockBaseDataFragment.handleDataStateChange(Result.Error())
        assertInvocationTimesForResultCallbacks(Result.Error())
    }

    @Test
    fun handlingTheSUCCESSStateProperly()
    {
        mockBaseDataFragment.handleDataStateChange(Result.Success(null))
        assertInvocationTimesForResultCallbacks(Result.Success(null))
    }


    @Test
    fun isDisplayingSnackbarWhenFAILED_TO_UPDATE()
    {
        fragmentScenario.onFragment {
            it.handleDataStateChange(Result.FailedToUpdate)

        }
        onView(withId(com.google.android.material.R.id.snackbar_text)).check(matches(withText(failedToUpdateToastString)))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }


    /**
     * Makes sure the Snackbar is not displayed repeatedly when data requests keeps on failing to update.
     */
    @Test
    fun isDisplayingSnackbarOnlyOnceWhenFAILED_TO_UPDATE()
    {
        fragmentScenario.onFragment {
            it.handleDataStateChange(Result.FailedToUpdate)
        }

        onView(withId(com.google.android.material.R.id.snackbar_text)).check(matches(withText(failedToUpdateToastString)))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))


        fragmentScenario.onFragment {
            it.dismissFailedToUpdateSnackBar()
            it.handleDataStateChange(Result.FailedToUpdate)
        }

        Thread.sleep(baseTransientBottomBarFadeAnimationDuration * 2)

        onView(withText(failedToUpdateToastString)).check(doesNotExist())

    }


    /**
     * Makes sure the Snackbar is displayed after these DataState changes happen:
     * FAILED_TO_UPDATE -> SUCCESS -> FAILED_TO_UPDATE
     *
     */
    @Test
    fun isDisplayingToastAfterRecoveredStateFailsAgain()
    {
        fragmentScenario.onFragment {
            it.handleDataStateChange(Result.FailedToUpdate)
        }

        onView(withId(com.google.android.material.R.id.snackbar_text)).check(matches(withText(failedToUpdateToastString)))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))


        fragmentScenario.onFragment { it.dismissFailedToUpdateSnackBar() }

        Thread.sleep(baseTransientBottomBarFadeAnimationDuration * 2)


        fragmentScenario.onFragment {
            it.handleDataStateChange(Result.Success(null))
            it.handleDataStateChange(Result.FailedToUpdate)
        }

        onView(withId(com.google.android.material.R.id.snackbar_text)).check(matches(withText(failedToUpdateToastString)))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }


    /**
     * Asserts that only the corresponding result function is called
     */
    fun assertInvocationTimesForResultCallbacks(result: Result<*>)
    {
        val resultFunctions = listOf(
                getCorrespondingResultFunction(Result.Error()),
                getCorrespondingResultFunction(Result.Success(null)),
                getCorrespondingResultFunction(Result.FailedToUpdate),
                getCorrespondingResultFunction(Result.Loading))

        val givenDataStateFunction = getCorrespondingResultFunction(result)

        resultFunctions.forEach { currentDataStateFunction ->

            if (givenDataStateFunction == currentDataStateFunction)
            {
                verify(mockBaseDataFragment, times(1))
            }
            else
            {
                verify(mockBaseDataFragment, never())
            }

            currentDataStateFunction.call()
        }
    }


    fun getCorrespondingResultFunction(result: Result<*>): KFunction<Unit> = when (result)
    {
        is Result.Loading -> mockBaseDataFragment::changeLayoutStateToLoading

        is Result.Success -> mockBaseDataFragment::changeLayoutStateToReady

        is Result.Error -> mockBaseDataFragment::changeLayoutStateToError

        is Result.FailedToUpdate -> mockBaseDataFragment::changeLayoutFailedUpdate
    }

}