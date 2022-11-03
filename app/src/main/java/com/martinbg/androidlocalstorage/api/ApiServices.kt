package com.martinbg.androidlocalstorage.api

import com.martinbg.androidlocalstorage.data.Country
import retrofit2.Call
import retrofit2.http.*

interface ApiServices {
    @GET("all")
    fun getCountries(): Call<List<Country>>

    @GET("name/{name}")
    fun getCountryByName(
        @Path("name") name: String,
        @Query("fullText") fullText: Boolean? = true
    ): Call<List<Country>>
}