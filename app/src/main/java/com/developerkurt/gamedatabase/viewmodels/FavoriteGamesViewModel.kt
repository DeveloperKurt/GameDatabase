package com.developerkurt.gamedatabase.viewmodels

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.developerkurt.gamedatabase.data.BaseRepository
import com.developerkurt.gamedatabase.data.GameRepository
import com.developerkurt.gamedatabase.data.model.GameData
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject

class FavoriteGamesViewModel @ViewModelInject @Inject internal constructor(
        application: Application,
        private val gameRepository: GameRepository) : BaseViewModel(application)
{
    private val favoriteGameListLiveData = MutableLiveData<List<GameData>>()
    val dataStateLiveData: LiveData<BaseRepository.DataState> = gameRepository.gameDataStateFlow().asLiveData(coroutineContext)

    fun getFavoriteGameListLiveData(): LiveData<List<GameData>> = favoriteGameListLiveData

    fun fetchTheList()
    {

        launch {
            gameRepository.startGettingGameDataUpdates()

        }

        launch {
            gameRepository.getGameDataFlow().filterNotNull().collect {
                favoriteGameListLiveData.value = it.filter { it.isInFavorites }
            }
        }

    }

}