package com.developerkurt.gamedatabase.viewmodels

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.developerkurt.gamedatabase.data.model.GameDetails
import com.developerkurt.gamedatabase.data.source.GameRepository
import com.developerkurt.gamedatabase.data.source.Result
import kotlinx.coroutines.launch

class GameDetailsViewModel @ViewModelInject internal constructor(
        @Assisted private val savedStateHandle: SavedStateHandle,
        private val gameRepository: GameRepository) : ViewModel()
{

    val gameId: Int = savedStateHandle.get<Int>("gameId")!!


    private val gameDetailsResultLiveData = MutableLiveData<Result<GameDetails>>(Result.Loading)
    private val isFavoriteLiveData = MutableLiveData(savedStateHandle.get<Boolean>("isInFavorites")!!)

    fun getIsFavoriteLiveData(): LiveData<Boolean> = isFavoriteLiveData

    fun getGameDetailsResultLiveData(gameId: Int): LiveData<Result<GameDetails>>
    {
        viewModelScope.launch {
            gameDetailsResultLiveData.postValue(gameRepository.getGameDetails(gameId))
        }

        return gameDetailsResultLiveData
    }

    fun favoriteStateChanged()
    {
        val favStateChangedTo = !isFavoriteLiveData.value!!
        isFavoriteLiveData.value = favStateChangedTo

        viewModelScope.launch {
            gameRepository.updateIsFavorite(gameId, favStateChangedTo)
        }
    }
}