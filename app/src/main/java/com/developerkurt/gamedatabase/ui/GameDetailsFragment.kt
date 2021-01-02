package com.developerkurt.gamedatabase.ui

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.palette.graphics.Palette
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.developerkurt.gamedatabase.GlideApp
import com.developerkurt.gamedatabase.R
import com.developerkurt.gamedatabase.databinding.GameDetailsFragmentMotionSceneStartBinding
import com.developerkurt.gamedatabase.viewmodels.GameDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class GameDetailsFragment : BaseDataFragment(), View.OnClickListener
{

    private val viewModel: GameDetailsViewModel by viewModels()


    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private var _binding: GameDetailsFragmentMotionSceneStartBinding? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        _binding = GameDetailsFragmentMotionSceneStartBinding.inflate(inflater, container, false)
        val view = binding.root

        if (requireActivity() is MainActivity)
        {
            (requireActivity() as MainActivity).hideBottomNavBar()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        binding.imgBtnAddToFavs.setOnClickListener(this)

        binding.tvDescription.setMovementMethod(ScrollingMovementMethod())

        viewModel.getDataStateLiveData().observe(viewLifecycleOwner, {
            handleDataStateChange(it)
        })

        viewModel.getIsFavoriteLiveData().observe(viewLifecycleOwner, {
            setUIFavoriteState(it)
        })

        viewModel.getGameDetailsLiveData(viewModel.gameId).observe(viewLifecycleOwner, {
            if (it != null)
            {
                binding.gameDetails = it

                // changeBackgroundToDominantImageColor(it.imageUrl)
            }

        })

    }


    override fun onDestroyView()
    {
        super.onDestroyView()
        _binding = null
    }

    private fun setUIFavoriteState(isFavorite: Boolean)
    {
        if (isFavorite)
        {
            binding.imgBtnAddToFavs.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_selected_favorite))
        }
        else
        {
            binding.imgBtnAddToFavs.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_favorite))

        }
    }

    override fun onClick(view: View?)
    {
        when (view)
        {
            binding.imgBtnAddToFavs ->
            {
                viewModel.favoriteStateChanged()
            }
        }
    }


    override fun changeLayoutStateToLoading()
    {
        binding.progressBar.visibility = View.VISIBLE
        binding.detailsLayout.visibility = View.INVISIBLE
        binding.ivGameImage.visibility = View.INVISIBLE
        binding.incErrorLayout.errorLayout.visibility = View.GONE

    }

    override fun changeLayoutStateToReady()
    {
        binding.progressBar.visibility = View.GONE
        binding.detailsLayout.visibility = View.VISIBLE
        binding.ivGameImage.visibility = View.VISIBLE
        binding.incErrorLayout.errorLayout.visibility = View.GONE
        binding.mainLayout.isInteractionEnabled = true

    }

    override fun changeLayoutStateToError()
    {
        binding.progressBar.visibility = View.GONE
        binding.detailsLayout.visibility = View.INVISIBLE
        binding.ivGameImage.visibility = View.INVISIBLE
        binding.incErrorLayout.errorLayout.visibility = View.VISIBLE
        binding.mainLayout.isInteractionEnabled = false

    }

    override fun changeLayoutFailedUpdate()
    {
        changeLayoutStateToError()
    }

    //TODO [Improvement] Get a dark and a light tone of the image to create a gradient instead of a singular color
    /**
     * Decided to not to use since I didn't like the end result. Keeping a consistent theme seems to be
     * looking way better. But feel free to try it.
     */
    private fun changeBackgroundToDominantImageColor(imageURL: String)
    {

        //Get the cached bitmap to find its dominant color
        GlideApp.with(this)
            .asBitmap()
            .load(imageURL)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(object : CustomTarget<Bitmap>()
            {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?)
                {
                    Palette.from(resource).generate({
                        it?.getDominantColor(R.attr.colorSurface)?.let { color ->
                            binding.mainLayout.setBackgroundColor(color)
                        }
                    })
                }

                override fun onLoadCleared(placeholder: Drawable?)
                {
                }
            })
    }

}