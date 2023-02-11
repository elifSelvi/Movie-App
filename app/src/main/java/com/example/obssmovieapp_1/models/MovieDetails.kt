package com.example.obssmovieapp_1 // ktlint-disable package-name

import com.google.gson.annotations.SerializedName

class DetailedMovie {
    var adult: Boolean? = null

    @SerializedName("backdrop_path")
    var backdropPath: String? = null

    @SerializedName("belongs_to_collection")
    var belongsToCollection: Any? = null
    var budget: Int? = null
    var genres: List<Genre>? = null
    var homepage: String? = null
    var id: Int? = null

    @SerializedName("imdb_id")
    var imdbId: String? = null

    @SerializedName("original_language")
    var originalLanguage: String? = null

    @SerializedName("original_title")
    var originalTitle: String? = null
    var overview: String? = null
    var popularity: Double? = null

    @SerializedName("poster_path")
    var posterPath: String? = null

    @SerializedName("production_companies")
    var productionCompanies: List<ProductionCompany>? = null

    @SerializedName("production_countries")
    var productionCountries: List<ProductionCountry>? = null

    @SerializedName("release_date")
    var releaseDate: String? = null
    var revenue: Int? = null
    var runtime: Int? = null

    @SerializedName("spoken_languages")
    var spokenLanguages: List<SpokenLanguage>? = null
    var status: String? = null
    var tagline: String? = null
    var title: String? = null
    var video: Boolean? = null

    @SerializedName("vote_average")
    var voteAverage: Double? = null

    @SerializedName("vote_count")
    var voteCount: Int? = null
    private val additionalProperties: MutableMap<String, Any> = HashMap()
    fun getAdditionalProperties(): Map<String, Any> {
        return additionalProperties
    }

    fun setAdditionalProperty(name: String, value: Any) {
        additionalProperties[name] = value
    }
}

class Genre {
    var id: Int? = null
    var name: String? = null
    private val additionalProperties: MutableMap<String, Any> = HashMap()
    fun getAdditionalProperties(): Map<String, Any> {
        return additionalProperties
    }

    fun setAdditionalProperty(name: String, value: Any) {
        additionalProperties[name] = value
    }
}

class ProductionCompany {
    var id: Int? = null

    @SerializedName("logo_path")
    var logoPath: Any? = null
    var name: String? = null

    @SerializedName("origin_country")
    var originCountry: String? = null
    private val additionalProperties: MutableMap<String, Any> = HashMap()
    fun getAdditionalProperties(): Map<String, Any> {
        return additionalProperties
    }

    fun setAdditionalProperty(name: String, value: Any) {
        additionalProperties[name] = value
    }
}

class ProductionCountry {
    @SerializedName("iso_3166_1")
    var iso31661: String? = null
    var name: String? = null
    private val additionalProperties: MutableMap<String, Any> = HashMap()
    fun getAdditionalProperties(): Map<String, Any> {
        return additionalProperties
    }

    fun setAdditionalProperty(name: String, value: Any) {
        additionalProperties[name] = value
    }
}

class SpokenLanguage {
    @SerializedName("english_name")
    var englishName: String? = null

    @SerializedName("iso_639_1")
    var iso6391: String? = null
    var name: String? = null
    private val additionalProperties: MutableMap<String, Any> = HashMap()
    fun getAdditionalProperties(): Map<String, Any> {
        return additionalProperties
    }

    fun setAdditionalProperty(name: String, value: Any) {
        additionalProperties[name] = value
    }
}
