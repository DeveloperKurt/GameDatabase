package com.developerkurt.gamedatabase.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.developerkurt.gamedatabase.R
import com.developerkurt.gamedatabase.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity()
{

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        //Enable the line below to force the dark mode
        //   AppCompatDelegate.setDefaultNightMode( AppCompatDelegate.MODE_NIGHT_YES)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }


    fun setupBottomNavigationBar()
    {
        binding.bottomNavView.visibility = View.VISIBLE

        val navController = Navigation.findNavController(this, R.id.fragment_container)
        NavigationUI.setupWithNavController(binding.bottomNavView, navController)
    }
}
