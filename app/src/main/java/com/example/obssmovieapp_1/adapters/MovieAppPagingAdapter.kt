package com.example.obssmovieapp_1.adapters // ktlint-disable package-name

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.obssmovieapp_1.R
import com.example.obssmovieapp_1.databinding.ItemMovieBinding
import com.example.obssmovieapp_1.models.Movie
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.roundToInt

// makes a view for each item in the data
// PagingDataAdapter from the library which is another implementation of the ListAdapter class.
class MovieAppPagingAdapter(val context: Context) :
    PagingDataAdapter<Movie, MovieAppPagingAdapter.ViewHolderHorizontal>(MovieDiffUtil()) {

    var whichposter = "bg"
    val df = DecimalFormat("#.#")

    inner class ViewHolderHorizontal(val binding: ItemMovieBinding) : RecyclerView.ViewHolder(binding.root)

    // val differ = DiffUtil.calculateDiff(differCallback)
    // AsyncListDiffer(this,differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderHorizontal {
        return ViewHolderHorizontal(
            ItemMovieBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    // holder:ViewHolder

    override fun onBindViewHolder(holder: ViewHolderHorizontal, position: Int) {
        df.roundingMode = RoundingMode.CEILING

        holder.binding.apply {
            // val movie = differ.currentList[position]
            val movie = getItem(position)

            var movieTextSize: Float? = 0f
            if (whichposter.equals("front")) {
                movieTextSize = 18f
            } else {
                movieTextSize = 22f
            }

            movieName.setTextSize(TypedValue.COMPLEX_UNIT_SP, movieTextSize)

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

            var bgOrFront: String? = ""
            if (whichposter.equals("front")) {
                bgOrFront = movie?.posterPath
            } else {
                bgOrFront = movie?.backdropPath
            }
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
    }

    var onclickListener: ((Movie) -> Unit)? = null
    var favOnclickListener: ((Movie) -> Unit)? = null


    fun setOnItemClickListener(listener: (Movie) -> Unit) {
        onclickListener = listener
    }

    fun setFavOnItemClickListener(listener: (Movie) -> Unit) {
        favOnclickListener = listener
    }
}

// diffutil class to compare old and new data
class MovieDiffUtil() : DiffUtil.ItemCallback<Movie>() {
    override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
        return oldItem.equals(newItem)
    }
}


/*
Note that DiffUtil, ListAdapter, and AsyncListDiffer require the list to not mutate while in use
 */