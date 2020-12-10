package com.developerkurt.gamedatabase.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.developerkurt.gamedatabase.adapters.GameListAdapter
import com.developerkurt.gamedatabase.adapters.ImagePagerAdapter
import com.developerkurt.gamedatabase.data.model.GameData
import com.developerkurt.gamedatabase.databinding.GameListFragmentBinding
import com.developerkurt.gamedatabase.viewmodels.GameListViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.game_list_fragment.*

@AndroidEntryPoint
class GameListFragment : BaseDataFragment(), GameListAdapter.GameClickListener
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

        (requireActivity() as MainActivity).displayBottomNavBar()

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


    private fun subscribeUi(gameListAdapter: GameListAdapter, imagePagerAdapter: ImagePagerAdapter)
    {
        viewModel.getGameListLiveData().observe(viewLifecycleOwner, {
            gameListAdapter.updateList(it)
            imagePagerAdapter.update(it.take(3).map { it.imageUrl })
        })

        viewModel.dataStateLiveData.observe(viewLifecycleOwner, {
            handleDataStateChange(it)
        })


        viewModel.getLatestGameList()


    }

    override fun changeLayoutStateToLoading()
    {
        binding.progressBar.visibility = View.VISIBLE
    }

    override fun changeLayoutStateToReady()
    {
        binding.viewPagerGameImages.visibility = View.VISIBLE
        binding.recyclerViewGameData.visibility = View.VISIBLE
        binding.tlViewPagerScroll.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
        binding.incErrorLayout.errorLayout.visibility = View.GONE
    }

    override fun changeLayoutStateToError()
    {
        binding.viewPagerGameImages.visibility = View.INVISIBLE
        binding.recyclerViewGameData.visibility = View.INVISIBLE
        binding.tlViewPagerScroll.visibility = View.INVISIBLE
        binding.incErrorLayout.errorLayout.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE

    }

    override fun changeLayoutFailedUpdate()
    {
        binding.progressBar.visibility = View.GONE
    }


    override fun onGameClick(gameData: GameData)
    {
        navigateToGameDetails(gameData)
    }

    private fun navigateToGameDetails(gameData: GameData)
    {
        val direction = GameListFragmentDirections.actionGameListFragmentToGameDetailsFragment(gameData.id, gameData.isInFavorites)
        binding.root.findNavController().navigate(direction)
    }

}