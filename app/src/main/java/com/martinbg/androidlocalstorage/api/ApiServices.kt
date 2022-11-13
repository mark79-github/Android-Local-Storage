package com.martinbg.androidlocalstorage.api

import com.martinbg.androidlocalstorage.data.CountryModel
import retrofit2.Call
import retrofit2.http.*

interface ApiServices {
    @GET("all")
    fun getCountries(): Call<List<CountryModel>>

    @GET("name/{name}")
    fun getCountryByName(
        @Path("name") name: String,
        @Query("fullText") fullText: Boolean? = true
    ): Call<List<CountryModel>>
}