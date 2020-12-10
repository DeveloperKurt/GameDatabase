package com.developerkurt.gamedatabase.viewmodels

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.developerkurt.gamedatabase.data.BaseRepository
import com.developerkurt.gamedatabase.data.GameRepository
import com.developerkurt.gamedatabase.data.model.GameDetails
import com.developerkurt.gamedatabase.util.stripHtml
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class GameDetailsViewModel @ViewModelInject internal constructor(
        @Assisted private val savedStateHandle: SavedStateHandle,
        private val gameRepository: GameRepository) : ViewModel()
{

    val gameId: Int = savedStateHandle.get<Int>("gameId")!!
    var imageUrl: String? = null

    private val gameDetailsLiveData = MutableLiveData<GameDetails?>()
    private val isFavoriteLiveData = MutableLiveData(savedStateHandle.get<Boolean>("isInFavorites")!!)
    private val mutableDataStateLiveData = MutableLiveData<BaseRepository.DataState>(BaseRepository.DataState.UNKNOWN)

    fun getDataStateLiveData(): LiveData<BaseRepository.DataState> = mutableDataStateLiveData
    fun getIsFavoriteLiveData(): LiveData<Boolean> = isFavoriteLiveData

    fun getGameDetailsLiveData(gameId: Int): LiveData<GameDetails?>
    {

        viewModelScope.launch {


            gameDetailsLiveData.postValue(
                    gameRepository.fetchGameDetailsOnceFromDatabase(gameId).await()
                        .also {
                            if (it != null)
                            {
                                it.description = it.description.stripHtml()
                                mutableDataStateLiveData.postValue(BaseRepository.DataState.SUCCESS)

                            }
                            else
                            {
                                mutableDataStateLiveData.postValue(BaseRepository.DataState.FAILED)
                            }
                        })

            gameRepository.gameDetailsStateFlow().collect {
                mutableDataStateLiveData.postValue(it)
            }
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