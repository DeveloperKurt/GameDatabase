package com.developerkurt.gamedatabase.data.model

import android.graphics.Bitmap
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
    var imageBitmap: Bitmap? = null

}

@BindingAdapter("load_image")
fun loadImage(imageView: ImageView, imageURLModel: ImageURLModel)
{
    if (imageURLModel.imageBitmap == null)
    {
        Glide.with(imageView.getContext())
            .load(imageURLModel.imageUrl)
            .apply(RequestOptions().error(R.drawable.ic_error_robot))
            .into(imageView)
    }
    else
    {
        Glide.with(imageView.getContext())
            .load(imageURLModel.imageBitmap)
            .apply(RequestOptions().error(R.drawable.ic_error_robot))
            .into(imageView)
    }
}

@BindingAdapter("load_circle_image")
fun loadCircleImage(imageView: ImageView, imageURLModel: ImageURLModel)
{
    if (imageURLModel.imageBitmap == null)
    {
        Glide.with(imageView.getContext())
            .load(imageURLModel.imageUrl)
            .apply(RequestOptions().circleCrop().error(R.drawable.ic_error_robot))
            .into(imageView)
    }
    else
    {
        Glide.with(imageView.getContext())
            .load(imageURLModel.imageBitmap)
            .apply(RequestOptions().circleCrop().error(R.drawable.ic_error_robot))
            .into(imageView)
    }
}