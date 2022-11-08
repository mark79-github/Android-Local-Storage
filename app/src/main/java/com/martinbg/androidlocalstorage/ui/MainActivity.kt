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
import com.google.android.material.snackbar.Snackbar
import com.martinbg.androidlocalstorage.R
import com.martinbg.androidlocalstorage.adapter.CountryAdapter
import com.martinbg.androidlocalstorage.api.ApiClient
import com.martinbg.androidlocalstorage.api.ApiServices
import com.martinbg.androidlocalstorage.data.Country
import com.martinbg.androidlocalstorage.data.Info
import com.martinbg.androidlocalstorage.databinding.ActivityMainBinding
import com.martinbg.androidlocalstorage.db.CountryDao
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

    private val dao: CountryDao by lazy {
        CountryDatabase.getDatabase(this).countryDao()
    }

    private val networkRequest: NetworkRequest by lazy {
        NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()
    }

    private val connectivityManager: ConnectivityManager by lazy {
        getSystemService(ConnectivityManager::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        connectivityManager.requestNetwork(networkRequest, networkCallback)
        Prefs.init(applicationContext)

        Prefs[R.string.has_internet_connection.toString()] =
            connectivityManager.activeNetwork != null

        binding.progressBar.visibility = View.VISIBLE
        binding.layoutInfo.visibility = View.GONE

        if (Prefs[R.string.has_internet_connection.toString()]) {
            binding.apply {
                val countries = api.getCountries()
                countries.enqueue(object : Callback<List<Country>> {
                    override fun onResponse(
                        call: Call<List<Country>>,
                        response: Response<List<Country>>
                    ) {
                        progressBar.visibility = View.GONE
                        when (response.code()) {
                            in 200..299 -> {
                                response.body()?.let { body ->
                                    body.let { data ->
                                        if (data.isNotEmpty()) {
                                            countryAdapter.differ.submitList(data)
                                            recyclerview.apply {
                                                layoutManager =
                                                    LinearLayoutManager(this@MainActivity)
                                                adapter = countryAdapter
                                            }
                                            dao.insertAll(data)
                                            val strDate: String = getCurrentDateTimeAsString()
                                            dao.insertInfo(Info(strDate))
                                            tvLastFetchedDatetime.text = dao.getLastInfo()!!.date
                                            tvRecordsCount.text = data.size.toString()
                                            binding.layoutInfo.visibility = View.VISIBLE
                                        }
                                    }
                                }
                            }
                            else -> {
                                Log.d(
                                    "Response Code",
                                    "Response Code : ${response.code()}"
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
        } else {
            val data = dao.getAll()
            if (data.isEmpty()) {
                Toast.makeText(
                    this,
                    R.string.error_message_no_connection,
                    Toast.LENGTH_LONG
                ).show()
                finish()
            } else {
                countryAdapter.differ.submitList(data)
                binding.apply {
                    progressBar.visibility = View.GONE
                    recyclerview.apply {
                        layoutManager =
                            LinearLayoutManager(this@MainActivity)
                        adapter = countryAdapter
                    }
                    val info = dao.getLastInfo()
                    tvLastFetchedDatetime.text = info?.date
                    tvRecordsCount.text = data.size.toString()
                    layoutInfo.visibility = View.VISIBLE
                }
                Snackbar.make(
                    binding.root,
                    R.string.error_message_outdated_data,
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            Prefs[R.string.has_internet_connection.toString()] = true
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            Prefs[R.string.has_internet_connection.toString()] = false
        }
    }

    private fun getCurrentDateTimeAsString(): String {
        val date: Date = Calendar.getInstance().time
        val dateFormat: DateFormat =
            SimpleDateFormat(
                "dd MMM yyyy HH:mm:ss",
                Locale.ROOT
            )
        return dateFormat.format(date)
    }
}

