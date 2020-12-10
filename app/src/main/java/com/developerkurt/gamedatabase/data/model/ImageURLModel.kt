package com.developerkurt.gamedatabase.data.model

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.developerkurt.gamedatabase.GlideApp
import com.developerkurt.gamedatabase.R


/**
 * Provides asynchronous image downloading functionality for the models that need it
 */
abstract class ImageURLModel
{
    abstract val imageUrl: String
}

/**
 * Loads the bitmap into the ImageView. If there's a cached bitmap related to this URL
 * returns that otherwise gets it by making a network request
 */
@BindingAdapter("load_image")
fun loadImage(imageView: ImageView, imageURLModel: ImageURLModel?)
{

    GlideApp.with(imageView.getContext())
        .load(imageURLModel?.imageUrl)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .apply(RequestOptions().error(R.drawable.ic_error_robot))
        .into(imageView)

}

/**
 * Exactly like the [loadImage] but loads it as a circle image
 */
@BindingAdapter("load_circle_image")
fun loadCircleImage(imageView: ImageView, imageURLModel: ImageURLModel?)
{
    GlideApp.with(imageView.getContext())
        .load(imageURLModel?.imageUrl)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .apply(RequestOptions().circleCrop().error(R.drawable.ic_error_robot))
        .into(imageView)
}