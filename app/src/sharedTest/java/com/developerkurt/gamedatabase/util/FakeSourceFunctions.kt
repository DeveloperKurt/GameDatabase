package com.developerkurt.gamedatabase.util

import com.developerkurt.gamedatabase.data.model.GameData
import com.developerkurt.gamedatabase.data.model.GameDetails
import com.developerkurt.gamedatabase.data.source.Result

fun mapGameDataListToResult(gameList: MutableList<GameData>?): Result<List<GameData>>
{
    return if (gameList.isNullOrEmpty())
    {
        Result.Error()
    }
    else
    {
        Result.Success(gameList)
    }
}

fun mapGameDetailsToResult(gameDetails: GameDetails?, shouldItBeLoading: Boolean = false): Result<GameDetails>
{
    if (shouldItBeLoading) return Result.Loading

    return if (gameDetails == null)
    {
        Result.Error()
    }
    else
    {
        Result.Success(gameDetails)
    }
}