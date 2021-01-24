package com.developerkurt.gamedatabase.viewmodels


import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.developerkurt.gamedatabase.data.model.GameData
import com.developerkurt.gamedatabase.data.source.GameRepository
import com.developerkurt.gamedatabase.data.source.Result


class GameListViewModel @ViewModelInject internal constructor(private val gameRepository: GameRepository) : ViewModel()
{
    var isViewPagerCreationHandled = false
    var viewPagerPosition = 0
    var searchedTerm = ""

    suspend fun getGameListResultLiveData(): LiveData<Result<List<GameData>>> = gameRepository.observeGameDataList()
}