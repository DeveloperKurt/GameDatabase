package com.developerkurt.gamedatabase.ui

import androidx.fragment.app.Fragment
import com.developerkurt.gamedatabase.R
import com.developerkurt.gamedatabase.data.source.Result
import com.developerkurt.gamedatabase.data.source.Result.*
import com.google.android.material.snackbar.Snackbar

abstract class BaseDataFragment : Fragment()
{
    private var displayedErrorSnackbar = false
    protected val failedToUpdateSnackBar by lazy { Snackbar.make(this.requireView(), R.string.data_update_fail, Snackbar.LENGTH_LONG) }

    abstract protected fun changeLayoutStateToLoading()
    abstract protected fun changeLayoutStateToReady()
    abstract protected fun changeLayoutStateToError()
    abstract protected fun changeLayoutFailedUpdate()

    open protected fun showFailedToUpdateSnackBar() = failedToUpdateSnackBar.show()

    fun handleDataStateChange(result: Result<*>)
    {
        when (result)
        {

            is Loading -> changeLayoutStateToLoading()
            is Error -> changeLayoutStateToError()

            is Success ->
            {
                changeLayoutStateToReady()
                displayedErrorSnackbar = false
            }

            is FailedToUpdate ->
            {
                if (!displayedErrorSnackbar)
                {
                    showFailedToUpdateSnackBar()
                    displayedErrorSnackbar = true
                }

                changeLayoutFailedUpdate()
            }

        }
    }
}
