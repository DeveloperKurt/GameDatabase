package com.developerkurt.developerkurtmodule

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.coroutines.InternalCoroutinesApi

class ModuleDialogFragment : DialogFragment()
{

    companion object
    {
        fun newInstance() = ModuleDialogFragment()
    }

    private lateinit var viewModel: ModuleDialogViewModel

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.module_dialog_fragment, container, true)
    }

    @InternalCoroutinesApi
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog
    {
        val dialog = Dialog(requireContext(), R.style.Dialog)
        dialog.setContentView(R.layout.module_dialog_fragment)

        dialog.setCancelable(true)
        dialog.create()

        return dialog

    }

    override fun onResume()
    {
        super.onResume()
        val window = dialog!!.window ?: return
        val params = window.attributes
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        window.attributes = params
    }
}
