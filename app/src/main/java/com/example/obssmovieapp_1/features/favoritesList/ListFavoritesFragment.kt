package com.example.obssmovieapp_1.features.favoritesList // ktlint-disable package-name

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.obssmovieapp_1.MainActivity
import com.example.obssmovieapp_1.R
import com.example.obssmovieapp_1.adapters.MovieAppAdapter
import com.example.obssmovieapp_1.databinding.FragmentListFavoritesBinding
import com.example.obssmovieapp_1.features.movieDetail.MovieDetailFragment
import com.example.obssmovieapp_1.adapters.MovieAppPagingAdapter
import com.example.obssmovieapp_1.features.movieSearch.SearchMovieFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ListFavoritesFragment : Fragment(R.layout.fragment_list_favorites) {

    lateinit var binding: FragmentListFavoritesBinding
    val viewModel: FavoritesViewModel by viewModels()
    lateinit var adapt: MovieAppAdapter

    @Inject
    lateinit var apiKey: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListFavoritesBinding.inflate(inflater, container, false)

        viewModel.fetchFavoriteMovies()

        viewModel.favsLiveData.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                // the best way to infinite scroll ? differ vs notifydatasetchange
                // viewModel.favoritesChanged(response)
                // adapt.loadList(response)
                adapt.submitList(response)
            }
        }

        setUpRV()
        return binding.root
    }

    fun setUpRV() = binding.favoritesList.apply {
        adapt = MovieAppAdapter(requireActivity())

        adapt.setOnItemClickListener {

            //findNavController().navigate(ListFavoritesFragmentDirections.actionListFavoritesFragmentToMovieDetailFragment2(it))
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
    }
}
