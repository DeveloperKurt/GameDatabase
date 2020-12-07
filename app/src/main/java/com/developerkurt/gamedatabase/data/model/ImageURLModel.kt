package com.developerkurt.gamedatabase.data.model

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.developerkurt.gamedatabase.R


/**
 * Provides asynchronous image downloading functionality for the models that need it
 */
abstract class ImageURLModel
{
    abstract val imageUrl: String

}

@BindingAdapter("load_image")
fun loadImage(imageView: ImageView, imageUrl: String)
{
    Glide.with(imageView.getContext())
        .load(imageUrl)
        .apply(RequestOptions().error(R.drawable.ic_error_robot))
        .into(imageView)
}

@BindingAdapter("load_circle_image")
fun loadCircleImage(imageView: ImageView, imageUrl: String)
{
    Glide.with(imageView.getContext())
        .load(imageUrl)
        .apply(RequestOptions().circleCrop().error(R.drawable.ic_error_robot))
        .into(imageView)
}