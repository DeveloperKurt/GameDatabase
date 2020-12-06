package com.developerkurt.gamedatabase.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SplashViewModel @ViewModelInject internal constructor() : ViewModel()
{

    private var isLoadedLiveData = MutableLiveData(true)

    fun getIsLoadedLiveData(): LiveData<Boolean> = isLoadedLiveData

    fun load()
    {
        //TODO change this to the actual implementation
/*
        val timer = Timer()
        timer.schedule(object : TimerTask()
        {
            override fun run()
            {
                Timber.i("Display-Data is loaded")

                isLoadedLiveData.postValue(true)

            }

        }, 2000)*/
    }
}