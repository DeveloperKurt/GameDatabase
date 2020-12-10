package com.developerkurt.gamedatabase.adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.developerkurt.gamedatabase.R
import com.developerkurt.gamedatabase.data.model.ImageURLModel
import com.developerkurt.gamedatabase.databinding.ImageViewPagerItemBinding


class ImagePagerAdapter(val context: Context) : RecyclerView.Adapter<ImagePagerAdapter.ViewPagerImageViewHolder>()
{
    private val imageURLList: MutableList<String> = mutableListOf()

    fun update(imageURLList: List<String>)
    {
        this.imageURLList.clear()
        this.imageURLList.addAll(imageURLList)

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewPagerImageViewHolder(
            DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.image_view_pager_item,
                    parent,
                    false))

    override fun onBindViewHolder(holder: ViewPagerImageViewHolder, position: Int)
    {
        holder.binding.urlModel = object : ImageURLModel()
        {
            override val imageUrl: String = imageURLList[position]
        }
    }

    override fun getItemCount(): Int = imageURLList.size


    inner class ViewPagerImageViewHolder(val binding: ImageViewPagerItemBinding) : RecyclerView.ViewHolder(binding.root)


}