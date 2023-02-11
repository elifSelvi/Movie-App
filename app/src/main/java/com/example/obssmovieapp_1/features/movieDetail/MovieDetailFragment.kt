package com.example.obssmovieapp_1.features.movieDetail // ktlint-disable package-name

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.obssmovieapp_1.MainActivity
import com.example.obssmovieapp_1.R
import com.example.obssmovieapp_1.adapters.MovieAppAdapter
import com.example.obssmovieapp_1.databinding.FragmentMovieDetailBinding
import com.example.obssmovieapp_1.adapters.MovieAppPagingAdapter
import com.example.obssmovieapp_1.features.movieSearch.SearchMovieFragmentArgs
import com.example.obssmovieapp_1.features.movieSearch.SearchMovieFragmentDirections
import com.example.obssmovieapp_1.models.Movie
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieDetailFragment : Fragment(R.layout.fragment_movie_detail) {

    //val args: MovieDetailFragmentArgs by navArgs()

    lateinit var binding: FragmentMovieDetailBinding

    val viewModel: MovieDetailViewModel by viewModels()
    lateinit var adapt: MovieAppAdapter

    lateinit var selectedMovie: Movie

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMovieDetailBinding.inflate(inflater, container, false)

         val args = this.arguments
        //selectedMovie = args.detailedMovie
        selectedMovie = (args?.get("movie") as Movie)


        fetchAndObserveForInfo()
        setUpRV()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.detailIsFavoriteMovie.setOnClickListener {
            if (selectedMovie.isFavorite == 1) {
                viewModel.removeFromFavorites(selectedMovie)
                binding.detailIsFavoriteMovie.setImageResource(R.drawable.not_favorite)
            } else {
                viewModel.addToFavorites(selectedMovie)
                binding.detailIsFavoriteMovie.setImageResource(R.drawable.favorite)
            }
        }
    }

    fun fetchAndObserveForInfo() {
        viewModel.fetchMovieDetails(selectedMovie)

        viewModel.movieDetailsLiveData.observe(viewLifecycleOwner) { response ->
            if (response != null) {

                viewModel.isInDB(selectedMovie)

                if (selectedMovie.isFavorite == 1) {
                    binding.detailIsFavoriteMovie.setImageResource(R.drawable.favorite)
                } else {
                    binding.detailIsFavoriteMovie.setImageResource(R.drawable.not_favorite)
                }

                if (response.backdropPath != null) {
                    Glide.with(this)
                        .load("https://image.tmdb.org/t/p/w500" + response.backdropPath)
                        .into(binding.detailMovieBGImg)
                }

                if (response.posterPath != null) {
                    Glide.with(this)
                        .load("https://image.tmdb.org/t/p/w500" + response.posterPath)
                        .into(binding.detailMovieImg)
                }

                val name = response.title
                val release = response.releaseDate.toString()
                var runtime = response.runtime.toString() + "m"

                var genres = ""
                for (i in response.genres!!) {
                    genres += i.name + ","
                }

                var final_genres = ""
                if (genres.length != 0) {
                    final_genres = genres.substring(0, genres.length - 1)
                }

                val overview = response.overview

                binding.detailMovieName.text = name
                binding.detailDateRuntime.text = release + " | " + runtime
                binding.detailGenres.text = final_genres
                binding.detailSummary.text = "Overview:\n" + overview
            }
        }
    }

    // for similar movies the same fragment
    fun setUpRV() = binding.similarsList.apply {
        adapt = MovieAppAdapter(requireActivity())

        viewModel.fetchSimilarMovies(selectedMovie)

        viewModel.similarsLiveData.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                // adapt.loadList(response.moviesList!!)
                adapt.submitList(response.moviesList)
            }
        }

        adapt.setOnItemClickListener {

            //val action = MovieDetailFragmentDirections.actionMovieDetailFragment2Self(it)
            //findNavController().navigate(action)

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

        // different design
        adapt.verticalOrHorizontal = "vertical"

        adapter = adapt
        layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
    }
}
