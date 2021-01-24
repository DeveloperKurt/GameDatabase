package com.developerkurt.gamedatabase.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.navGraphViewModels
import androidx.viewpager2.widget.ViewPager2
import com.developerkurt.gamedatabase.R
import com.developerkurt.gamedatabase.adapters.GameListAdapter
import com.developerkurt.gamedatabase.adapters.ImagePagerAdapter
import com.developerkurt.gamedatabase.data.model.GameData
import com.developerkurt.gamedatabase.data.source.Result
import com.developerkurt.gamedatabase.databinding.GameListFragmentBinding
import com.developerkurt.gamedatabase.util.hideKeyboard
import com.developerkurt.gamedatabase.viewmodels.GameListViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.game_list_fragment.*
import timber.log.Timber


@AndroidEntryPoint
class GameListFragment : BaseDataFragment(), GameListAdapter.GameClickListener
{
    private lateinit var binding: GameListFragmentBinding
    private val viewModel: GameListViewModel by navGraphViewModels(R.id.game_list) { defaultViewModelProviderFactory }

    private lateinit var gameListAdapter: GameListAdapter
    private lateinit var imagePagerAdapter: ImagePagerAdapter


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View?
    {
        binding = GameListFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        Timber.d("onCreateView")
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)


        if (requireActivity() is MainActivity)
        {
            (requireActivity() as MainActivity).displayBottomNavBar()
        }

        initSearchBar()

        gameListAdapter = GameListAdapter(this)
        imagePagerAdapter = ImagePagerAdapter(requireContext())

        recycler_view_game_data.adapter = gameListAdapter
        initViewPager(imagePagerAdapter)

    }


    override fun onResume()
    {
        super.onResume()
        subscribeUi(gameListAdapter, imagePagerAdapter)
    }

    override fun onDestroyView()
    {
        super.onDestroyView()
        viewModel.isViewPagerCreationHandled = false
    }

    private fun initViewPager(imagePagerAdapter: ImagePagerAdapter)
    {
        binding.viewPagerGameImages.setOffscreenPageLimit(2)
        binding.viewPagerGameImages.adapter = imagePagerAdapter


        binding.viewPagerGameImages.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback()
        {

            override fun onPageSelected(position: Int)
            {
                super.onPageSelected(position)

                var smoothScroll = false
                if (viewModel.isViewPagerCreationHandled)
                {
                    viewModel.viewPagerPosition = position
                    smoothScroll = true
                }
                else
                {
                    viewModel.isViewPagerCreationHandled = true
                }

                binding.viewPagerGameImages.setCurrentItem(viewModel.viewPagerPosition, smoothScroll)
            }
        })

        TabLayoutMediator(tl_view_pager_scroll, view_pager_game_images) { tab, position -> }.attach()

    }


    private fun subscribeUi(gameListAdapter: GameListAdapter, imagePagerAdapter: ImagePagerAdapter)
    {
        lifecycleScope.launchWhenStarted {
            viewModel.getGameListResultLiveData().observe(viewLifecycleOwner, { result ->

                if (result is Result.Success)
                {
                    gameListAdapter.updateList(result.data)
                    imagePagerAdapter.update(result.data.take(3).map { it.imageUrl })
                }

                handleDataStateChange(result)
            })
        }
    }

    override fun handleDataStateChange(result: Result<*>)
    {
        when (result)
        {

            is Result.Loading -> changeLayoutStateToLoading()
            is Result.Error -> changeLayoutStateToError()

            is Result.Success ->
            {
                //If there was a searched term, keep displaying its results
                if (viewModel.searchedTerm.isBlank())
                {
                    changeLayoutStateToReady()
                }
                else
                {
                    changeLayoutStateToSearchResults(gameListAdapter.filterByName(viewModel.searchedTerm))
                }

                displayedErrorSnackbar = false
            }

            is Result.FailedToUpdate ->
            {
                if (!displayedErrorSnackbar)
                {
                    showFailedToUpdateSnackBar()
                    displayedErrorSnackbar = true
                }

                changeLayoutFailedUpdate()
            }

        }
    }

    private fun initSearchBar()
    {

        binding.layoutSearchBar.etSearch.addTextChangedListener(object : TextWatcher
        {
            override fun afterTextChanged(s: Editable)
            {
                if (s.length >= 3)
                {
                    changeLayoutStateToSearchResults(gameListAdapter.filterByName(s.toString()))
                    viewModel.searchedTerm = s.toString()

                }
                else if (s.length < 3)
                {
                    gameListAdapter.removeFilter()
                    changeLayoutStateToReady()
                    viewModel.searchedTerm = ""
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int)
            {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int)
            {
            }
        })

        binding.layoutSearchBar.imgBtnSearch.setOnClickListener {
            hideKeyboard()
            binding.layoutSearchBar.etSearch.clearFocus()
        }


    }

    private fun changeLayoutStateToSearchResults(didFindResults: Boolean)
    {
        binding.viewPagerGameImages.visibility = View.GONE
        binding.tlViewPagerScroll.visibility = View.GONE

        if (!didFindResults)
            binding.tvNoResults.visibility = View.VISIBLE
        else
            binding.tvNoResults.visibility = View.INVISIBLE

    }

    override fun changeLayoutStateToLoading()
    {
        binding.progressBar.visibility = View.VISIBLE
        binding.tvNoResults.visibility = View.GONE

    }

    override fun changeLayoutStateToReady()
    {
        binding.viewPagerGameImages.visibility = View.VISIBLE
        binding.recyclerViewGameData.visibility = View.VISIBLE
        binding.tlViewPagerScroll.visibility = View.VISIBLE
        binding.tvNoResults.visibility = View.INVISIBLE
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
        binding.tvNoResults.visibility = View.GONE

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