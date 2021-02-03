package com.developerkurt.gamedatabase

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.developerkurt.gamedatabase.data.source.Result
import com.developerkurt.gamedatabase.ui.BaseDataFragment

/**
 * To fix the visibility issue for the Mockito, this class creates the public versions of the protected methods
 */
open class TestBaseDataFragmentImp : BaseDataFragment()
{
    fun dismissFailedToUpdateSnackBar() = failedToUpdateSnackBar.dismiss()

    override public fun handleDataStateChange(result: Result<*>)
    {
        super.handleDataStateChange(result)
    }

    override public fun showFailedToUpdateSnackBar()
    {
        super.showFailedToUpdateSnackBar()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.test_base_data_fragment_imp, container, false)
    }

    public override fun changeLayoutStateToLoading()
    {
    }

    public override fun changeLayoutStateToReady()
    {
    }

    public override fun changeLayoutStateToError()
    {
    }

    public override fun changeLayoutFailedUpdate()
    {
    }
}