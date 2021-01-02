package com.developerkurt.gamedatabase.util

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import timber.log.Timber

fun Fragment.hideKeyboard()
{
    try
    {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }
    catch (e: Exception)
    {
        Timber.w(e, "Caught en exception while trying to hide the keyboard")
    }
}