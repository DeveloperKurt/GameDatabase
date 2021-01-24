package com.developerkurt.gamedatabase.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.developerkurt.gamedatabase.data.source.GameRepository
import timber.log.Timber

class SplashViewModel @ViewModelInject internal constructor(
        private val gameRepository: GameRepository) : ViewModel()
{

    private var isLoadedLiveData = MutableLiveData(false)

    fun getIsLoadedLiveData(): LiveData<Boolean> = isLoadedLiveData

    suspend fun prepareData()
    {

        val didPrepareData = gameRepository.prepareGameDataList()
        Timber.i("Prepared the data: $didPrepareData")

        isLoadedLiveData.value = true

    }
}