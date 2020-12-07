package com.developerkurt.gamedatabase.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.developerkurt.gamedatabase.adapters.GameListAdapter
import com.developerkurt.gamedatabase.adapters.ImagePagerAdapter
import com.developerkurt.gamedatabase.databinding.GameListFragmentBinding
import com.developerkurt.gamedatabase.viewmodels.GameListViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.game_list_fragment.*
import kotlinx.coroutines.launch

//TODO the fragment container in the layout doesn't respect its bottom container; thus, last recyclerview item is barely visible
@AndroidEntryPoint
class GameListFragment : Fragment(), GameListAdapter.GameClickListener
{
    private lateinit var binding: GameListFragmentBinding
    private val viewModel: GameListViewModel by viewModels()


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View?
    {
        binding = GameListFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        val gameListAdapter = GameListAdapter(this)
        val imagePagerAdapter = ImagePagerAdapter(requireContext())

        recycler_view_game_data.adapter = gameListAdapter
        initViewPager(imagePagerAdapter)

        subscribeUi(gameListAdapter, imagePagerAdapter)

    }

    private fun initViewPager(imagePagerAdapter: ImagePagerAdapter)
    {
        binding.viewPagerGameImages.adapter = imagePagerAdapter
        TabLayoutMediator(tl_view_pager_scroll, view_pager_game_images) { tab, position ->
            view_pager_game_images.setCurrentItem(tab.position, true)
        }.attach()

    }


    //TODO Check the lifecycle's effect on LiveData, atm it doesn't emit values when navigated back from a navigationbar fragment
    private fun subscribeUi(gameListAdapter: GameListAdapter, imagePagerAdapter: ImagePagerAdapter)
    {
        viewModel.getGameListLiveData().observe(viewLifecycleOwner, {
            gameListAdapter.updateList(it)
            imagePagerAdapter.update(it.take(3).map { it.imageUrl })
        })

        lifecycleScope.launch {
            viewModel.getLatestGameList()
        }

    }

    override fun onGameClick(gameId: Int)
    {
        navigateToGameDetails(gameId)
    }

    private fun navigateToGameDetails(gameId: Int)
    {
        val direction = GameListFragmentDirections.actionGameListFragmentToGameDetailsFragment(gameId)
        binding.root.findNavController().navigate(direction)
    }

}