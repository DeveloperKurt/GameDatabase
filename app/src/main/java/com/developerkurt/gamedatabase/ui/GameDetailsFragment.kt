package com.developerkurt.gamedatabase.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels

import com.developerkurt.gamedatabase.databinding.GameDetailsFragmentMotionSceneStartBinding
import com.developerkurt.gamedatabase.viewmodels.GameDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GameDetailsFragment : Fragment()
{

    private val viewModel: GameDetailsViewModel by viewModels()


    // This property is only valid between onCreateView and onDestroyView.
    private var _binding: GameDetailsFragmentMotionSceneStartBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View?
    {
        _binding = GameDetailsFragmentMotionSceneStartBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView()
    {
        super.onDestroyView()
        _binding = null
    }

}