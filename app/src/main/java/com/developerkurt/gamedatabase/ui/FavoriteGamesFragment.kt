package com.developerkurt.gamedatabase.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.developerkurt.gamedatabase.databinding.FavoriteGamesFragmentBinding
import com.developerkurt.gamedatabase.viewmodels.FavoriteGamesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteGamesFragment : Fragment()
{
    private val viewModel: FavoriteGamesViewModel by viewModels()


    // This property is only valid between onCreateView and onDestroyView.
    private var _binding: FavoriteGamesFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View?
    {
        _binding = FavoriteGamesFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView()
    {
        super.onDestroyView()
        _binding = null
    }
}