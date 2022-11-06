package com.martinbg.androidlocalstorage.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.martinbg.androidlocalstorage.R
import com.martinbg.androidlocalstorage.api.ApiClient
import com.martinbg.androidlocalstorage.api.ApiServices
import com.martinbg.androidlocalstorage.data.Country
import com.martinbg.androidlocalstorage.databinding.ActivityCountryDetailsBinding
import com.martinbg.androidlocalstorage.db.CountryDao
import com.martinbg.androidlocalstorage.db.CountryDatabase
import com.martinbg.androidlocalstorage.utils.Prefs
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat


class CountryDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCountryDetailsBinding

    private val api: ApiServices by lazy {
        ApiClient().getClient().create(ApiServices::class.java)
    }

    private val dao: CountryDao by lazy {
        CountryDatabase.getDatabase(this).countryDao()
    }

    private var hasInternetConnection: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCountryDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()

        val countryName =
            intent.getStringExtra(R.string.intent_extra_attribute_country_name.toString())
                .toString()
        hasInternetConnection = Prefs[R.string.has_internet_connection.toString()]
        when (hasInternetConnection) {
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
                                        showImage(imgCountryFlag, flag)
                                        showImage(imgCountryFlagBackground, flag)
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
                                in 300..599 -> {
                                    Log.d(
                                        "Response Code",
                                        "Response code error : ${response.code()}"
                                    )
                                }

                            }
                        }

                        override fun onFailure(call: Call<List<Country>>, t: Throwable) {
                            progressBar.visibility = View.GONE
                            Log.e("onFailure", "Error : ${t.message}")
                        }
                    })
                }
            }
            false -> {
                binding.progressBar.visibility = View.VISIBLE
                val country = dao.getCountryByName(countryName)
                binding.apply {
                    progressBar.visibility = View.GONE
                    showImage(imgCountryFlag, R.drawable.flag_placeholder)
                    tvCountryName.text = country.name
                    tvCountryCapital.text = country.capital
                    tvRegionData.text = country.region
                    tvSubregionData.text = country.subregion
                    tvPopulationData.text = country.population.toString()
                    tvAreaData.text = calculateAreaToString(country.area)
                    tvNativeNameData.text = country.nativeName
                    tvNumericCodeData.text = country.numericCode
                    tvAcronymData.text = country.cioc
                    progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun calculateAreaToString(area: String?): String {
        if (area == null) return "No data"
        val df = DecimalFormat("#.00")
        return df.format(area.toFloat()).toString()
    }

    private fun showImage(img: ImageView, resource: Int) {
        img.load(resource)
    }

    private fun showImage(img: ImageView, resource: String) {
        img.load(resource)
    }
}