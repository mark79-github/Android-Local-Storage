package com.martinbg.androidlocalstorage.ui

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.martinbg.androidlocalstorage.adapter.CountryAdapter
import com.martinbg.androidlocalstorage.api.ApiClient
import com.martinbg.androidlocalstorage.api.ApiServices
import com.martinbg.androidlocalstorage.data.Country
import com.martinbg.androidlocalstorage.data.Info
import com.martinbg.androidlocalstorage.databinding.ActivityMainBinding
import com.martinbg.androidlocalstorage.db.CountryDatabase
import com.martinbg.androidlocalstorage.utils.Prefs
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val countryAdapter by lazy { CountryAdapter() }

    private val api: ApiServices by lazy {
        ApiClient().getClient().create(ApiServices::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Prefs.init(applicationContext)

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

        val connectivityManager =
            getSystemService(ConnectivityManager::class.java) as ConnectivityManager
        connectivityManager.requestNetwork(networkRequest, networkCallback)
        if (connectivityManager.activeNetwork == null) {
            Prefs["isConnected"] = false
        }

        binding.tvLastFetchedDatetime.visibility = View.GONE
        binding.tvRecordsCount.visibility = View.GONE

        if (Prefs["isConnected"]) {
            binding.apply {
                progressBar.visibility = View.VISIBLE
                val countries = api.getCountries()
                countries.enqueue(object : Callback<List<Country>> {
                    override fun onResponse(
                        call: Call<List<Country>>,
                        response: Response<List<Country>>
                    ) {
                        progressBar.visibility = View.GONE
                        when (response.code()) {
                            in 200..299 -> {
                                Log.d("Response Code", " success messages : ${response.code()}")
                                response.body()?.let { body ->
                                    body.let { data ->
                                        if (data.isNotEmpty()) {
                                            countryAdapter.differ.submitList(data)
                                            recyclerview.apply {
                                                layoutManager =
                                                    LinearLayoutManager(this@MainActivity)
                                                adapter = countryAdapter
                                            }
                                            val dao =
                                                CountryDatabase.getDatabase(applicationContext)
                                                    .countryDao()
                                            dao.insertAll(data)
                                            val date: Date = Calendar.getInstance().time
                                            val dateFormat: DateFormat =
                                                SimpleDateFormat(
                                                    "dd MMM yyyy HH:mm:ss",
                                                    Locale.ROOT
                                                )
                                            val strDate: String = dateFormat.format(date)

                                            dao.insertInfo(Info(strDate))
                                            tvLastFetchedDatetime.text = dao.getLastInfo()!!.date
                                            tvRecordsCount.text = data.size.toString()
                                            binding.tvLastFetchedDatetime.visibility = View.VISIBLE
                                            binding.tvRecordsCount.visibility = View.VISIBLE
                                        }
                                    }
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
        } else {
            val dao = CountryDatabase.getDatabase(this).countryDao()
            val data = dao.getAll()

            when (data.size) {
                0 -> {
                    Toast.makeText(
                        this,
                        "Application exiting, need an active internet connection ..",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
                else -> {
                    Toast.makeText(
                        this,
                        "No network connection found, data may be outdated ...",
                        Toast.LENGTH_LONG
                    ).show()

                }
            }

            countryAdapter.differ.submitList(data)
            binding.progressBar.visibility = View.GONE
            binding.recyclerview.apply {
                layoutManager =
                    LinearLayoutManager(this@MainActivity)
                adapter = countryAdapter
            }
            val info = dao.getLastInfo()
            binding.tvLastFetchedDatetime.text = info?.date ?: "No data available"
            binding.tvRecordsCount.text = data.size.toString()
            binding.tvLastFetchedDatetime.visibility = View.VISIBLE
            binding.tvRecordsCount.visibility = View.VISIBLE
        }
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            Prefs["isConnected"] = true
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            Prefs["isConnected"] = false
        }
    }
}

