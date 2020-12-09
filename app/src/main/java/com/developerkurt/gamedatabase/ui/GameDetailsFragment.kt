package com.developerkurt.gamedatabase.ui

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.developerkurt.gamedatabase.GlideApp
import com.developerkurt.gamedatabase.R
import com.developerkurt.gamedatabase.databinding.GameDetailsFragmentMotionSceneStartBinding
import com.developerkurt.gamedatabase.viewmodels.GameDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class GameDetailsFragment : Fragment() {

    private val viewModel: GameDetailsViewModel by viewModels()


    // This property is only valid between onCreateView and onDestroyView.
    private var _binding: GameDetailsFragmentMotionSceneStartBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = GameDetailsFragmentMotionSceneStartBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        changeLayoutStateToLoading()
        val gameId = (requireArguments().get("gameId") as Int)
        viewModel.getGameDetailsLiveData(gameId).observe(viewLifecycleOwner, {
            if (it != null) {
                binding.gameDetails = it
                changeLayoutStateToReady(it.imageUrl)

            } else {
                changeLayoutStateToError()
            }
        })
    }

    //TODO motion layout ignores the visibility changes
    private fun changeLayoutStateToLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.detailsLayout.visibility = View.INVISIBLE
        binding.ivGameImage.visibility = View.INVISIBLE
        binding.incErrorLayout.errorLayout.visibility = View.GONE

    }

    private fun changeLayoutStateToReady(imageURL: String) {
        binding.progressBar.visibility = View.GONE
        binding.detailsLayout.visibility = View.VISIBLE
        binding.ivGameImage.visibility = View.VISIBLE
        binding.incErrorLayout.errorLayout.visibility = View.GONE

        // changeBackgroundToDominantImageColor(imageURL)
    }

    /**
     * Decided to not to use since I didn't like the end result. Keeping a consistent theme seems to be
     * looking way better. But feel free to try it.
     */
    private fun changeBackgroundToDominantImageColor(imageURL: String) {

        //Get the cached bitmap to find its dominant color
        GlideApp.with(this)
            .asBitmap()
            .load(imageURL)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    Palette.from(resource).generate({
                        it?.getDominantColor(R.attr.colorSurface)?.let { color ->
                            binding.mainLayout.setBackgroundColor(color)
                        }
                    })
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

    private fun changeLayoutStateToError() {
        binding.progressBar.visibility = View.GONE
        binding.detailsLayout.visibility = View.INVISIBLE
        binding.ivGameImage.visibility = View.INVISIBLE
        binding.incErrorLayout.errorLayout.visibility = View.VISIBLE

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}