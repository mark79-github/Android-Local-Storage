package com.martinbg.androidlocalstorage.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import coil.load
import coil.size.Scale
import com.martinbg.androidlocalstorage.R
import com.martinbg.androidlocalstorage.api.ApiClient
import com.martinbg.androidlocalstorage.api.ApiServices
import com.martinbg.androidlocalstorage.data.Country
import com.martinbg.androidlocalstorage.databinding.ActivityCountryDetailsBinding
import com.martinbg.androidlocalstorage.db.CountryDatabase
import com.martinbg.androidlocalstorage.utils.Prefs
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat


class CountryDetailsActivity : AppCompatActivity() {

    lateinit var binding: ActivityCountryDetailsBinding

    private val api: ApiServices by lazy {
        ApiClient().getClient().create(ApiServices::class.java)
    }

    override fun onResume() {
        super.onResume()

        val countryName: String = intent.getStringExtra("name").toString()
        val isConnected: Boolean = Prefs["isConnected"]
        when (isConnected) {
            true -> {
                binding.apply {
                    progressBar.visibility = View.VISIBLE
                    val countriesApi = api.getCountryByName(countryName)
                    countriesApi.enqueue(object : Callback<List<Country>> {
                        override fun onResponse(
                            call: Call<List<Country>>,
                            response: Response<List<Country>>
                        ) {
                            progressBar.visibility = View.GONE
                            when (response.code()) {
                                in 200..299 -> {
                                    response.body()?.let { body ->
                                        val country = body[0]
                                        val flag = country.flags.png
                                        imgCountryFlag.load(flag) {
                                            crossfade(true)
                                            placeholder(R.drawable.flag_placeholder)
                                            scale(Scale.FILL)
                                        }
                                        imgCountryFlagBackground.load(flag) {
                                            crossfade(true)
                                            placeholder(R.drawable.flag_placeholder)
                                            scale(Scale.FILL)
                                        }

                                        tvCountryName.text = country.name
                                        tvCountryCapital.text = country.capital
                                        tvRegionData.text = country.region
                                        tvSubregionData.text = country.subregion
                                        tvPopulationData.text = country.population.toString()
                                        tvAreaData.text = calculateAreaToString(country.area)
                                        tvNativeNameData.text = country.nativeName
                                        tvNumericCodeData.text = country.numericCode
                                        tvAcronymData.text = country.cioc
                                    }
                                }

                                in 300..399 -> {
                                    Log.d(
                                        "Response Code",
                                        " Redirection messages : ${response.code()}"
                                    )
                                }
                                in 400..499 -> {
                                    Log.d(
                                        "Response Code",
                                        " Client error responses : ${response.code()}"
                                    )
                                }
                                in 500..599 -> {
                                    Log.d(
                                        "Response Code",
                                        " Server error responses : ${response.code()}"
                                    )
                                }

                            }
                        }

                        override fun onFailure(call: Call<List<Country>>, t: Throwable) {
                            progressBar.visibility = View.GONE
                            Log.e("onFailure", "Err : ${t.message}")
                        }
                    })
                }
            }
            false -> {
                binding.progressBar.visibility = View.VISIBLE

                val dao = CountryDatabase.getDatabase(this.applicationContext).countryDao()
                val country = dao.getCountryByName(countryName)
                binding.apply {
                    progressBar.visibility = View.GONE
                    imgCountryFlag.load(R.drawable.flag_placeholder) {
                        crossfade(true)
                        placeholder(R.drawable.flag_placeholder)
                        scale(Scale.FILL)
                    }
                    tvCountryName.text = country.name
                    tvCountryCapital.text = country.capital
                    tvRegionData.text = country.region
                    tvSubregionData.text = country.subregion
                    tvPopulationData.text = country.population.toString()
                    tvAreaData.text = calculateAreaToString(country.area)
                    tvNativeNameData.text = country.nativeName
                    tvNumericCodeData.text = country.numericCode
                    tvAcronymData.text = country.cioc
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCountryDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun calculateAreaToString(area: String?): String {
        if (area == null) return "No data"
        val df = DecimalFormat("#.00")
        return df.format(area.toFloat()).toString()
    }
}