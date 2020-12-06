package com.developerkurt.gamedatabase.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.developerkurt.gamedatabase.databinding.GameListFragmentBinding
import com.developerkurt.gamedatabase.viewmodels.GameListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GameListFragment : Fragment()
{

    private val viewModel: GameListViewModel by viewModels()

    // This property is only valid between onCreateView and onDestroyView.
    private var _binding: GameListFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View?
    {
        _binding = GameListFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onStart()
    {
        super.onStart()
        viewModel.onFragmentVisible()
        viewModel.gameListLiveData.observe(viewLifecycleOwner, {

        })
    }

    override fun onStop()
    {
        super.onStop()
        viewModel.onFragmentNoLongerVisible()
    }

    override fun onDestroyView()
    {
        super.onDestroyView()
        _binding = null
    }

}