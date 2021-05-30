package com.developerkurt.developerkurtmodule


import android.app.Activity
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore by preferencesDataStore(name = "settings")

class Module constructor(val activity: Activity)
{
    private val moduleView = ModuleView(activity)
}