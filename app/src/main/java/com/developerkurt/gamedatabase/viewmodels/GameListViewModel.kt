package com.developerkurt.gamedatabase.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.developerkurt.gamedatabase.data.GameRepository
import com.developerkurt.gamedatabase.data.model.GameData
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import javax.inject.Inject

class GameListViewModel @ViewModelInject @Inject internal constructor(
        private val gameRepository: GameRepository) : ViewModel()
{
    private val gameListLiveData = MutableLiveData<List<GameData>>()

    fun getGameListLiveData(): LiveData<List<GameData>> = gameListLiveData


    suspend fun getLatestGameList()
    {
        gameRepository.getTheLatestGameList().filterNotNull().collect {
            gameListLiveData.value = it
        }
    }



}