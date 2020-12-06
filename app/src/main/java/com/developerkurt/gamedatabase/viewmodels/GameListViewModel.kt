package com.developerkurt.gamedatabase.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.developerkurt.gamedatabase.data.GameRepository
import com.developerkurt.gamedatabase.data.model.GameData
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class GameListViewModel @ViewModelInject @Inject internal constructor(private val gameRepository: GameRepository) : ViewModel()
{
    lateinit var gameListLiveData: LiveData<List<GameData>>
        private set

    private lateinit var networkingJob: Job


    fun onFragmentVisible()
    {
        networkingJob = viewModelScope.launch {
            gameListLiveData = gameRepository.getTheLatestGameList().asLiveData(coroutineContext)
        }
    }

    fun onFragmentNoLongerVisible()
    {
        networkingJob.cancel()

    }

}