package com.developerkurt.gamedatabase.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developerkurt.gamedatabase.data.GameRepository
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class SplashViewModel @ViewModelInject @Inject internal constructor(
        private val gameRepository: GameRepository) : ViewModel()
{

    private var isLoadedLiveData = MutableLiveData(false)

    fun getIsLoadedLiveData(): LiveData<Boolean> = isLoadedLiveData

    fun prepareData()
    {
        viewModelScope.launch {
            val didPrepareData = gameRepository.ifAblePrepareGameDataList()
            Timber.i("Prepared the data: $didPrepareData")

            isLoadedLiveData.value = true
        }
    }
}