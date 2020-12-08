package com.developerkurt.gamedatabase.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.developerkurt.gamedatabase.data.BaseRepository
import com.developerkurt.gamedatabase.data.GameRepository
import com.developerkurt.gamedatabase.data.model.GameData
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import javax.inject.Inject

class GameListViewModel @ViewModelInject @Inject internal constructor(
        private val gameRepository: GameRepository) : ViewModel()
{
    private val gameListLiveData = MutableLiveData<List<GameData>>()
    private val errorLiveData = MutableLiveData<Boolean>()


    fun getGameListLiveData(): LiveData<List<GameData>> = gameListLiveData
    fun getErrorLiveData(): LiveData<Boolean> = errorLiveData


    suspend fun getLatestGameList()
    {
        gameRepository.getTheLatestGameList(object : BaseRepository.ErrorListener
        {
            override fun onError()
            {
                errorLiveData.postValue(true)
            }

        }).filterNotNull().collect {
            gameListLiveData.value = it
            errorLiveData.value = false
        }
    }



}