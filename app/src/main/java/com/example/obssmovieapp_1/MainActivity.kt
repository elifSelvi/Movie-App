package com.example.obssmovieapp_1 // ktlint-disable package-name

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.obssmovieapp_1.databinding.ActivityMainBinding
import com.example.obssmovieapp_1.features.favoritesList.ListFavoritesFragment
import com.example.obssmovieapp_1.features.movieList.ListMoviesFragment
import com.example.obssmovieapp_1.features.movieSearch.SearchMovieFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*
        val bottomnav = binding.bottomNavMenu
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment
        val navController = navHostFragment.navController

        bottomnav.setupWithNavController(navController)

         */
        supportFragmentManager.beginTransaction().replace(R.id.fragment, ListMoviesFragment(), "LIST")
            .commit()

        binding.bottomNavMenu.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> replaceFragment(ListMoviesFragment(), "LIST")
                R.id.search -> replaceFragment(SearchMovieFragment(), "SEARCH")
                R.id.favorites -> replaceFragment(ListFavoritesFragment(), "FAVORITES")
            }
            true
        }
    }

    // tag: String
    fun replaceFragment(fragment: Fragment, tag: String) {
        val f = supportFragmentManager.findFragmentByTag(tag)
        if (f != null && f.isVisible()) {
            // same fragment, do nothing
        } else {
            supportFragmentManager.beginTransaction().replace(R.id.fragment, fragment, tag)
                .addToBackStack(null)
                .commit()
        }
    }

  /*
      override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.fragment).navigateUp()
    }
   */
}
