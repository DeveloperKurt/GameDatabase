package com.developerkurt.gamedatabase.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.developerkurt.gamedatabase.data.GameRepository

class GameDetailsViewModel @ViewModelInject internal constructor(
        private val gameRepository: GameRepository) : ViewModel()
{

}