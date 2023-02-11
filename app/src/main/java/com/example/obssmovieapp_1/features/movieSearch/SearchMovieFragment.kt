package com.example.obssmovieapp_1.features.movieSearch // ktlint-disable package-name

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.example.obssmovieapp_1.MainActivity
import com.example.obssmovieapp_1.R
import com.example.obssmovieapp_1.adapters.MovieAppPagingAdapter
import com.example.obssmovieapp_1.databinding.FragmentSearchMovieBinding
import com.example.obssmovieapp_1.features.movieDetail.MovieDetailFragment
import com.example.obssmovieapp_1.features.movieList.ListMoviesFragmentDirections
import com.example.obssmovieapp_1.models.Movie
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchMovieFragment : Fragment() {

    //val args: SearchMovieFragmentArgs by navArgs()
    //selectedMovie = args.detailedMovie
    //(args?.get("movie") as Movie)

    lateinit var binding: FragmentSearchMovieBinding

    lateinit var adapt: MovieAppPagingAdapter

    val viewModel: MovieSearchViewModel by viewModels()

    var myQuery: String = ""
    var started = false

    var searchedQuery: String? = null

    // couroutine job for delay in searchview

    /*
    Coroutines can be controlled through the functions that are available on the Job interface.
     */
    var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchMovieBinding.inflate(inflater, container, false)

        val args = this.arguments

        if (args != null) {
            if (!args.equals(null)) {
                searchedQuery = (args.get("query") as String)
            }
        }

        if (myQuery.equals("") && searchedQuery != null) {
            myQuery = searchedQuery as String
            viewModel.submitQuery(myQuery)
        }



        setUpRV()

        val currList = adapt.snapshot().items
        viewModel.favoritesChanged(currList)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchData.collect{ pagingData->
                adapt.submitData(pagingData)
            }

        }



        started = true
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchBar.clearFocus()
                if (!query.equals("")) {
                    myQuery = query!!
                    // view model initialized again, no need to clear adapter data
                    // adapt.submitList(mutableListOf())
                    //initializeViewModel()
                    viewModel.submitQuery(query)
                }
                return false
            }


            override fun onQueryTextChange(newText: String?): Boolean {
                job?.cancel()
                job = MainScope().launch {
                    delay(500L)
                    if (!newText.equals("") && !newText.equals(myQuery)) {
                        myQuery = newText!!
                        viewModel.submitQuery(newText)
                    }
                }
                return false
            }
        })
    }


    fun setUpRV() = binding.searchResults.apply {
        adapt = MovieAppPagingAdapter(requireActivity())

        adapt.addLoadStateListener { loadState ->

            // control loadstates
            val refresh = loadState.source.refresh is LoadState.Loading
            val add = loadState.source.append is LoadState.Loading
            val notAdd = loadState.source.append.endOfPaginationReached
            binding.progressBarSearch.isVisible = (refresh || add) && !notAdd

        }
        adapt.setOnItemClickListener {

            //findNavController().navigate(SearchMovieFragmentDirections.actionSearchMovieFragmentToMovieDetailFragment2(it))
            val bundle = Bundle()
            bundle.putParcelable("movie", it)

            val fragmentinfo = MovieDetailFragment()

            fragmentinfo.arguments = bundle
            val transaction = (activity as MainActivity).supportFragmentManager
            transaction.beginTransaction().replace(R.id.fragment, fragmentinfo)
                .addToBackStack(null) // name
                .commit()
        }

        adapt.setFavOnItemClickListener {
            if (it.isFavorite == 1) {
                viewModel.removeFromFavorites(it)
            } else {
                viewModel.addToFavorites(it)
            }
        }

        adapter = adapt
        layoutManager = GridLayoutManager(activity, 1)
        setHasFixedSize(true)
    }

    fun initializeViewModel() {
        viewModel.clearData()

        //isLoading = true
        binding.progressBarSearch.visibility = View.VISIBLE

        viewModel.fetchMoviesByQuery(myQuery)
    }
}
