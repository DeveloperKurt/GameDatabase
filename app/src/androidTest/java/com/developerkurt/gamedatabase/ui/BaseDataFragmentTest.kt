package com.developerkurt.gamedatabase.ui


import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@SmallTest
class BaseDataFragmentTest
{
    //TODO - refactor
 /*   @Mock lateinit var mockBaseDataFragment: TestBaseDataFragmentImp

    private val context = InstrumentationRegistry.getInstrumentation().getTargetContext()
    private val failedToUpdateToastString: String = context.resources.getString(com.developerkurt.gamedatabase.R.string.data_update_fail)

    private val fragmentScenario by lazy { launchFragmentInContainer<TestBaseDataFragmentImp>(Bundle(), R.style.Theme_AppCompat) }
    private val baseTransientBottomBarFadeAnimationDuration = 180L

    @Before
    fun init()
    {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun handlingTheUNKNOWNDataStateProperly()
    {
        mockBaseDataFragment.handleDataStateChange(BaseRepository.DataState.UNKNOWN)
        assertInvocationsTimesForDataStateCallbacks(BaseRepository.DataState.UNKNOWN)
    }

    @Test
    @UiThreadTest
    fun handlingTheFAILED_TO_UPDATEDataStateProperly()
    {
        val mockitoSpy = spy(TestBaseDataFragmentImp::class.java)
        doNothing().`when`(mockitoSpy).showFailedToUpdateSnackBar()
        mockBaseDataFragment.handleDataStateChange(BaseRepository.DataState.FAILED_TO_UPDATE)
        assertInvocationsTimesForDataStateCallbacks(BaseRepository.DataState.FAILED_TO_UPDATE)
    }

    @Test
    fun handlingTheFAILEDataStateProperly()
    {
        mockBaseDataFragment.handleDataStateChange(BaseRepository.DataState.FAILED)
        assertInvocationsTimesForDataStateCallbacks(BaseRepository.DataState.FAILED)
    }

    @Test
    fun handlingTheSUCCESSDataStateProperly()
    {
        mockBaseDataFragment.handleDataStateChange(BaseRepository.DataState.SUCCESS)
        assertInvocationsTimesForDataStateCallbacks(BaseRepository.DataState.SUCCESS)
    }

    @Test
    fun handlingTheNULLDataStateProperly()
    {
        mockBaseDataFragment.handleDataStateChange(null)
        assertInvocationsTimesForDataStateCallbacks(null)
    }

    @Test
    fun isDisplayingSnackbarWhenFAILED_TO_UPDATE()
    {
        fragmentScenario.onFragment {
            it.handleDataStateChange(BaseRepository.DataState.FAILED_TO_UPDATE)

        }
        onView(withId(com.google.android.material.R.id.snackbar_text)).check(matches(withText(failedToUpdateToastString)))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }


    */
    /**
     * Makes sure the Snackbar is not displayed repeatedly when data requests keeps on failing to update.
     *//*
    @Test
    fun isDisplayingSnackbarOnlyOnceWhenFAILED_TO_UPDATE()
    {
        fragmentScenario.onFragment {
            it.handleDataStateChange(BaseRepository.DataState.FAILED_TO_UPDATE)
        }

        onView(withId(com.google.android.material.R.id.snackbar_text)).check(matches(withText(failedToUpdateToastString)))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))


        fragmentScenario.onFragment {
            it.dismissFailedToUpdateSnackBar()
            it.handleDataStateChange(BaseRepository.DataState.FAILED_TO_UPDATE)
        }

        Thread.sleep(baseTransientBottomBarFadeAnimationDuration * 2)

        onView(withText(failedToUpdateToastString)).check(doesNotExist())

    }


    */
    /**
     * Makes sure the Snackbar is displayed after these DataState changes happen:
     * FAILED_TO_UPDATE -> SUCCESS -> FAILED_TO_UPDATE
     *
     *//*
    @Test
    fun isDisplayingToastAfterRecoveredStateFailsAgain()
    {
        fragmentScenario.onFragment {
            it.handleDataStateChange(BaseRepository.DataState.FAILED_TO_UPDATE)
        }

        onView(withId(com.google.android.material.R.id.snackbar_text)).check(matches(withText(failedToUpdateToastString)))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))


        fragmentScenario.onFragment { it.dismissFailedToUpdateSnackBar() }

        Thread.sleep(baseTransientBottomBarFadeAnimationDuration * 2)


        fragmentScenario.onFragment {
            it.handleDataStateChange(BaseRepository.DataState.SUCCESS)
            it.handleDataStateChange(BaseRepository.DataState.FAILED_TO_UPDATE)
        }

        onView(withId(com.google.android.material.R.id.snackbar_text)).check(matches(withText(failedToUpdateToastString)))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    fun assertInvocationsTimesForDataStateCallbacks(dataState: BaseRepository.DataState?)
    {
        val givenDataStateFunction = getCorrespondingFunctionToDataState(dataState)


        enumValues<BaseRepository.DataState>().forEach {
            val currentDataStateFunction = getCorrespondingFunctionToDataState(it)

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


    fun getCorrespondingFunctionToDataState(dataState: BaseRepository.DataState?): KFunction<Unit> = when (dataState)
    {
        BaseRepository.DataState.UNKNOWN -> mockBaseDataFragment::changeLayoutStateToLoading

        BaseRepository.DataState.SUCCESS -> mockBaseDataFragment::changeLayoutStateToReady

        BaseRepository.DataState.FAILED, null -> mockBaseDataFragment::changeLayoutStateToError

        BaseRepository.DataState.FAILED_TO_UPDATE -> mockBaseDataFragment::changeLayoutFailedUpdate
    }*/

}