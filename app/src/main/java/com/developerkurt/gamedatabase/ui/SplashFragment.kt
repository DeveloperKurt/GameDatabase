package com.developerkurt.gamedatabase.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.developerkurt.gamedatabase.databinding.SplashFragmentBinding
import com.developerkurt.gamedatabase.viewmodels.SplashViewModel
import timber.log.Timber

class SplashFragment : Fragment()
{

    //TODO INJECT
    private val viewModel: SplashViewModel by viewModels()

    // This property is only valid between onCreateView and onDestroyView.
    private var _binding: SplashFragmentBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?): View?
    {
        _binding = SplashFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        viewModel.getIsLoadedLiveData().observe(viewLifecycleOwner, { isLoaded ->
            if (!isLoaded)
            {
                viewModel.load()
            }
            else
            {
                Timber.i("Navigating to GameListFragment")
                navigateToGameListFragment()
            }
        })

        Timber.d("test")
        return view
    }


    private fun navigateToGameListFragment()
    {
        val action = SplashFragmentDirections.actionSplashFragmentToGameListFragment()
        Navigation.findNavController(binding.root).navigate(action)
        setupBottomNavigationBar()
    }

    private fun setupBottomNavigationBar()
    {
        (requireActivity() as MainActivity).setupBottomNavigationBar()
    }


    override fun onDestroyView()
    {
        super.onDestroyView()
        _binding = null
    }

}