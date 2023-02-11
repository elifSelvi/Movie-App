package com.example.obssmovieapp_1.features.movieList // ktlint-disable package-name

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.example.obssmovieapp_1.MainActivity
import com.example.obssmovieapp_1.R
import com.example.obssmovieapp_1.adapters.MovieAppPagingAdapter
import com.example.obssmovieapp_1.databinding.FragmentListMoviesBinding
import com.example.obssmovieapp_1.features.movieDetail.MovieDetailFragment
import com.example.obssmovieapp_1.features.movieSearch.SearchMovieFragment
import com.example.obssmovieapp_1.models.Movie
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ListMoviesFragment : Fragment(R.layout.fragment_list_movies) {

    lateinit var binding: FragmentListMoviesBinding
    val viewModel: MovieListViewModel by viewModels()
    lateinit var adapt: MovieAppPagingAdapter

    @Inject
    lateinit var apiKey: String

    var viewType = "linear"
    var favList = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentListMoviesBinding.inflate(inflater, container, false)


        /*
        LiveData considers an observer, which is represented by the Observer class,
        to be in an active state if its lifecycle is in the STARTED or RESUMED state.
        LiveData only notifies active observers about updates.
        You don't need to update the UI every time the app data changes
        because the observer does it for you.
         */


        setUpRV()


        viewLifecycleOwner.lifecycleScope.launch{
            viewModel.listData.collect{ pagingData->
                adapt.submitData(pagingData)
            }

        }

        val currList = adapt.snapshot().items
        viewModel.favoritesChanged(currList)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // moves on from where it's left
        binding.viewTypeBtn.setOnClickListener {
            if (viewType.equals("linear")) {
                (binding.movieList.layoutManager as GridLayoutManager).spanCount = 2
                viewType = "grid"
                binding.viewTypeBtn.setImageResource(R.drawable.listview)
                adapt.whichposter = "front"
                adapt.notifyDataSetChanged()
            } else {
                // binding.movieList.layoutManager = LinearLayoutManager(activity)
                (binding.movieList.layoutManager as GridLayoutManager).spanCount = 1
                viewType = "linear"
                binding.viewTypeBtn.setImageResource(R.drawable.gridview)
                adapt.whichposter = "bg"
                adapt.notifyDataSetChanged()

            }
        }

        binding.listSearchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.listSearchBar.clearFocus()

                //val action = ListMoviesFragmentDirections.actionListMoviesFragmentToSearchMovieFragment(query)
                //findNavController().navigate(action)
                val bundle = Bundle()
                bundle.putString("query",query)

                val fragmentinfo = SearchMovieFragment()

                fragmentinfo.arguments = bundle
                val transaction = (activity as MainActivity).supportFragmentManager
                transaction.beginTransaction().replace(R.id.fragment, fragmentinfo, "SEARCH")
                    .addToBackStack(null) // name
                    .commit()

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    fun setUpRV() = binding.movieList.apply {
        adapt = MovieAppPagingAdapter(requireActivity())

        adapt.addLoadStateListener { loadState ->

            val refresh = loadState.source.refresh is LoadState.Loading
            val add = loadState.source.append is LoadState.Loading
            val notAdd = loadState.source.append.endOfPaginationReached
            // binding.progressBar.isVisible = refresh || add
            binding.progressBar.isVisible = (refresh || add) && !notAdd

        }

        adapt.setOnItemClickListener {

            //val action = ListMoviesFragmentDirections.actionListMoviesFragmentToMovieDetailFragment2(it)
            //findNavController().navigate(action)
            val bundle = Bundle()
            bundle.putParcelable("movie",it)

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

        // start as linearlm
        layoutManager = GridLayoutManager(activity, 1)

        setHasFixedSize(true)
    }

}
