package com.developerkurt.gamedatabase.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.developerkurt.gamedatabase.data.model.GameData
import com.developerkurt.gamedatabase.data.source.GameRepository
import com.developerkurt.gamedatabase.data.source.Result

class FavoriteGamesViewModel @ViewModelInject internal constructor(private val gameRepository: GameRepository) : ViewModel()
{

    suspend fun getFavoriteGameListResultLiveData(): LiveData<Result<List<GameData>>>
    {

        return Transformations.map(gameRepository.observeGameDataList(), {
            return@map if (it is Result.Success)
            {
                Result.Success(it.data.filter { it.isInFavorites })
            }
            else
            {
                it
            }
        })
    }


}