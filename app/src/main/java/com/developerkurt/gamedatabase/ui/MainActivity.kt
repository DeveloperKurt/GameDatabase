package com.developerkurt.gamedatabase.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.developerkurt.gamedatabase.R
import com.developerkurt.gamedatabase.databinding.ActivityMainBinding
import com.developerkurt.gamedatabase.util.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity()
{

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null)
        {
            setupBottomNavigationBar()
        } // Else, need to wait for onRestoreInstanceState
    }


    override fun onRestoreInstanceState(savedInstanceState: Bundle)
    {
        super.onRestoreInstanceState(savedInstanceState)
        // Now that BottomNavigationBar has restored its instance state
        // and its selectedItemId, we can proceed with setting up the
        // BottomNavigationBar with Navigation
        setupBottomNavigationBar()
    }

    fun setupBottomNavigationBar()
    {
        displayBottomNavBar()

        val navGraphIds = listOf(R.navigation.game_list, R.navigation.fav_games)


        // Setup the bottom navigation view with a list of navigation graphs
        binding.bottomNavView.setupWithNavController(
                navGraphIds = navGraphIds,
                fragmentManager = supportFragmentManager,
                containerId = R.id.fragment_container,
                intent = intent)

    }

    fun displayBottomNavBar()
    {
        binding.bottomNavView.visibility = View.VISIBLE
    }

    fun hideBottomNavBar()
    {
        binding.bottomNavView.visibility = View.GONE
    }
}
