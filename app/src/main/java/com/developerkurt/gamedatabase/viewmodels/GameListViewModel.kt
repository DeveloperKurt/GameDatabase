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

class GameListViewModel @ViewModelInject @Inject internal constructor(
        application: Application,
        private val gameRepository: GameRepository) : BaseViewModel(application)
{
    private val gameListLiveData = MutableLiveData<List<GameData>>()
    val dataStateLiveData: LiveData<BaseRepository.DataState> = gameRepository.gameDataStateFlow().asLiveData(coroutineContext)


    fun getGameListLiveData(): LiveData<List<GameData>> = gameListLiveData


    fun getLatestGameList()
    {
        launch {
            gameRepository.startGettingGameDataUpdates()

        }

        launch {
            gameRepository.getGameDataFlow().filterNotNull().collect {
                gameListLiveData.value = it
            }
        }

    }

}