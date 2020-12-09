package com.developerkurt.gamedatabase.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.developerkurt.gamedatabase.R
import com.developerkurt.gamedatabase.data.model.GameData
import com.developerkurt.gamedatabase.databinding.GameListItemBinding

class GameListAdapter(val listenerCallback: GameClickListener) : RecyclerView.Adapter<GameListAdapter.GameDataViewHolder>()
{
    private var gameList: MutableList<GameData> = mutableListOf()

    fun updateList(gameList: List<GameData>)
    {
        this.gameList.clear()
        this.gameList.addAll(gameList)

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = GameDataViewHolder(
            DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.game_list_item,
                    parent,
                    false), listenerCallback)

    override fun onBindViewHolder(holder: GameDataViewHolder, position: Int)
    {
        holder.binding.gameData = gameList[position]
    }

    override fun getItemCount(): Int = gameList.size


    inner class GameDataViewHolder(val binding: GameListItemBinding, listenerCallback: GameClickListener) : RecyclerView.ViewHolder(binding.root)
    {
        init
        {
            binding.setClickListener {
                binding.gameData?.let {
                    listenerCallback.onGameClick(it)
                }
            }
        }
    }

    interface GameClickListener
    {
        fun onGameClick(gameData: GameData)
    }

}