package com.developerkurt.developerkurtmodule

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager


class ModuleView(context: Context, attrs: AttributeSet, defStyle: Int) : ConstraintLayout(context, attrs, defStyle)
{
    companion object
    {
        fun display(fragmentManager: FragmentManager)
        {
            ModuleDialogFragment.newInstance().show(fragmentManager, "ModuleDialogFragment")
        }
    }
}