package com.developerkurt.gamedatabase.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.developerkurt.gamedatabase.adapters.GameListAdapter
import com.developerkurt.gamedatabase.data.model.GameData
import com.developerkurt.gamedatabase.data.source.Result
import com.developerkurt.gamedatabase.databinding.FavoriteGamesFragmentBinding
import com.developerkurt.gamedatabase.viewmodels.FavoriteGamesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteGamesFragment : BaseDataFragment(), GameListAdapter.GameClickListener
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        if (requireActivity() is MainActivity)
        {
            (requireActivity() as MainActivity).displayBottomNavBar()
        }


        val gameListAdapter = GameListAdapter(this)
        binding.recyclerViewFavoriteGames.adapter = gameListAdapter

        setupUI(gameListAdapter)
    }

    private fun setupUI(gameListAdapter: GameListAdapter)
    {
        lifecycleScope.launchWhenStarted {
            viewModel.getFavoriteGameListResultLiveData().observe(viewLifecycleOwner, { result ->
                if (result is Result.Success)
                {
                    gameListAdapter.updateList(result.data)
                }
                handleDataStateChange(result)
            })
        }
    }

    override fun changeLayoutStateToLoading()
    {
        binding.progressBar.visibility = View.VISIBLE
    }

    override fun changeLayoutStateToReady()
    {
        binding.progressBar.visibility = View.GONE
        binding.recyclerViewFavoriteGames.visibility = View.VISIBLE
        binding.incErrorLayout.errorLayout.visibility = View.GONE
    }

    override fun changeLayoutStateToError()
    {
        binding.progressBar.visibility = View.GONE
        binding.recyclerViewFavoriteGames.visibility = View.INVISIBLE
        binding.incErrorLayout.errorLayout.visibility = View.VISIBLE

    }

    override fun changeLayoutFailedUpdate()
    {
        binding.progressBar.visibility = View.GONE
    }

    override fun onDestroyView()
    {
        super.onDestroyView()
        _binding = null
    }

    override fun onGameClick(gameData: GameData)
    {
        navigateToGameDetails(gameData)
    }

    private fun navigateToGameDetails(gameData: GameData)
    {
        val direction = FavoriteGamesFragmentDirections.actionFavoriteGamesFragmentToGameDetailsFragment(gameData.id, gameData.isInFavorites)
        binding.root.findNavController().navigate(direction)
    }
}