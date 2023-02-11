package com.example.obssmovieapp_1.adapters

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.obssmovieapp_1.R
import com.example.obssmovieapp_1.databinding.ItemMovieBinding
import com.example.obssmovieapp_1.databinding.ItemMovieVerticalBinding
import com.example.obssmovieapp_1.adapters.MovieDiffUtil
import com.example.obssmovieapp_1.models.Movie
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.roundToInt

class MovieAppAdapter(val context: Context) :
    ListAdapter<Movie, RecyclerView.ViewHolder>(MovieDiffUtil()) {

    var verticalOrHorizontal = "horizontal"
    val df = DecimalFormat("#.#")

    inner class ViewHolderHorizontal(val binding: ItemMovieBinding) : RecyclerView.ViewHolder(binding.root)
    inner class ViewHolderVertical(val binding: ItemMovieVerticalBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (verticalOrHorizontal.equals("horizontal")) {
            return ViewHolderHorizontal(
                ItemMovieBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
        return ViewHolderVertical(
            ItemMovieVerticalBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        df.roundingMode = RoundingMode.CEILING

        if (verticalOrHorizontal.equals("horizontal")) {
            (holder as ViewHolderHorizontal).binding.apply {
                // val movie = differ.currentList[position]
                val movie = getItem(position)

                movieName.text = movie?.title
                if (movie?.releaseDate != null) {
                    movieYear.text = movie.releaseDate!!.split("-")[0]
                }

                var doubleNew = ""
                if (movie?.voteAverage != null) {
                    doubleNew = ((movie.voteAverage!! * 10.0).roundToInt() / 10.0).toString()
                }
                rateText.text = doubleNew

                if (movie?.isFavorite == 1) {
                    isFavoriteMovie.setImageResource(R.drawable.favorite)
                } else {
                    isFavoriteMovie.setImageResource(R.drawable.not_favorite)
                }

                var bgOrFront = movie?.backdropPath

                Glide.with(context)
                    .load("https://image.tmdb.org/t/p/w500" + bgOrFront)
                    .fitCenter()
                    .into(movieImg)

                cardV.setOnClickListener {
                    // infoInt.getInfo(urlList[position])
                    onclickListener?.let {
                        it(movie!!)
                    }
                }

                isFavoriteMovie.setOnClickListener {
                    if (movie?.isFavorite == 1) {
                        isFavoriteMovie.setImageResource(R.drawable.not_favorite)
                    } else {
                        isFavoriteMovie.setImageResource(R.drawable.favorite)
                    }
                    // infoInt.getInfo(urlList[position])
                    favOnclickListener?.let {
                        it(movie!!)
                    }
                }

                // LoadState.Loading
            }
        } else {
            (holder as ViewHolderVertical).binding.apply {
                // val movie = differ.currentList[position]
                val movie = getItem(position)

                movieName.text = movie?.title
                if (movie?.releaseDate != null) {
                    movieYear.text = movie.releaseDate!!.split("-")[0]
                }

                // df.format(movie?.voteAverage).toDouble()
                var doubleNew = ""
                if (movie?.voteAverage != null) {
                    doubleNew = ((movie.voteAverage!! * 10.0).roundToInt() / 10.0).toString()
                }
                rateText.text = doubleNew

                if (movie?.isFavorite == 1) {
                    isFavoriteMovie.setImageResource(R.drawable.favorite)
                } else {
                    isFavoriteMovie.setImageResource(R.drawable.not_favorite)
                }

                Glide.with(context)
                    .load("https://image.tmdb.org/t/p/w500" + movie?.posterPath)
                    .fitCenter()
                    .into(movieImg)

                cardV.setOnClickListener {
                    // infoInt.getInfo(urlList[position])
                    onclickListener?.let {
                        it(movie!!)
                    }
                }

                isFavoriteMovie.setOnClickListener {
                    if (movie?.isFavorite == 1) {
                        isFavoriteMovie.setImageResource(R.drawable.not_favorite)
                    } else {
                        isFavoriteMovie.setImageResource(R.drawable.favorite)
                    }
                    // handle db process in viewmodel, send movie back to fragment
                    favOnclickListener?.let {
                        it(movie!!)
                    }
                }
            }
        }
    }

    var onclickListener: ((Movie) -> Unit)? = null
    var favOnclickListener: ((Movie) -> Unit)? = null

    // listener gets poke item and returns nothing
    fun setOnItemClickListener(listener: (Movie) -> Unit) {
        onclickListener = listener
    }

    fun setFavOnItemClickListener(listener: (Movie) -> Unit) {
        favOnclickListener = listener
    }
}
