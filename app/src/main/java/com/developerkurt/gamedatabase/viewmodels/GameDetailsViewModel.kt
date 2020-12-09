package com.developerkurt.gamedatabase.viewmodels

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.developerkurt.gamedatabase.data.GameRepository
import com.developerkurt.gamedatabase.data.model.GameDetails
import com.developerkurt.gamedatabase.util.stripHtml
import kotlinx.coroutines.launch

class GameDetailsViewModel @ViewModelInject internal constructor(
        @Assisted private val savedStateHandle: SavedStateHandle,
        private val gameRepository: GameRepository) : ViewModel()
{

    val gameId: Int = savedStateHandle.get<Int>("gameId")!!

    private val gameDetailsLiveData = MutableLiveData<GameDetails?>()
    private val isFavoriteLiveData = MutableLiveData(savedStateHandle.get<Boolean>("isInFavorites")!!)

    fun getIsFavoriteLive(): LiveData<Boolean> = isFavoriteLiveData

    fun getGameDetailsLiveData(gameId: Int): LiveData<GameDetails?>
    {

        viewModelScope.launch {

            gameDetailsLiveData.postValue(
                    gameRepository.fetchGameDetailsOnceFromDatabase(gameId).await()
                        .also {
                            if (it != null)
                            {
                                it.description = it.description.stripHtml()
                            }
                        })
        }
        return gameDetailsLiveData

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