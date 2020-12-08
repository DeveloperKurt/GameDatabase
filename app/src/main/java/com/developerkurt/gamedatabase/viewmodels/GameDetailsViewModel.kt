package com.developerkurt.gamedatabase.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developerkurt.gamedatabase.data.GameRepository
import com.developerkurt.gamedatabase.data.model.GameDetails
import kotlinx.coroutines.launch

class GameDetailsViewModel @ViewModelInject internal constructor(
        private val gameRepository: GameRepository) : ViewModel()
{

    private val gameDetailsLiveData = MutableLiveData<GameDetails?>()

    fun getGameDetailsLiveData(gameId: Int): LiveData<GameDetails?>
    {

        viewModelScope.launch {
            gameDetailsLiveData.postValue(gameRepository.fetchGameDetailsOnceFromDatabase(gameId).await())
        }
        return gameDetailsLiveData

    }
}