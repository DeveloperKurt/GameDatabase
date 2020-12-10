package com.developerkurt.gamedatabase.ui

import android.widget.Toast
import androidx.fragment.app.Fragment
import com.developerkurt.gamedatabase.R
import com.developerkurt.gamedatabase.data.BaseRepository

abstract class BaseDataFragment : Fragment()
{
    private var displayedErrorToast = false

    abstract protected fun changeLayoutStateToLoading()
    abstract protected fun changeLayoutStateToReady()
    abstract protected fun changeLayoutStateToError()
    abstract protected fun changeLayoutFailedUpdate()

    fun handleDataStateChange(dataState: BaseRepository.DataState?)
    {
        when (dataState)
        {

            BaseRepository.DataState.UNKNOWN -> changeLayoutStateToLoading()

            BaseRepository.DataState.SUCCESS -> changeLayoutStateToReady()

            BaseRepository.DataState.FAILED, null -> changeLayoutStateToError()

            BaseRepository.DataState.FAILED_TO_UPDATE ->
            {
                if (!displayedErrorToast)
                {
                    Toast.makeText(requireContext(), getString(R.string.data_update_fail), Toast.LENGTH_SHORT).show()
                    displayedErrorToast = true
                }
                changeLayoutFailedUpdate()
            }
        }
    }
}