package com.jayjohn.app.feature.presentation

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.text.bold
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.jayjohn.app.R
import com.jayjohn.app.databinding.FragmentWeatherBinding
import com.jayjohn.app.feature.domain.model.WeatherData
import com.jayjohn.app.feature.presentation.adapter.LocationAdapter
import com.jayjohn.app.feature.presentation.viewmodel.WeatherViewModel
import com.jayjohn.app.utils.getCurrentDateTime
import com.jayjohn.app.utils.hideKeyboard
import com.jayjohn.app.utils.showKeyboard
import com.jayjohn.app.utils.toDayString
import com.jayjohn.app.utils.toString
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.floor

/**
 * A fragment class for showing the weather view
 */
@AndroidEntryPoint
class WeatherFragment: Fragment() {

    private lateinit var binding: FragmentWeatherBinding
    private lateinit var adapter: LocationAdapter
    private lateinit var errorDialog: AlertDialog.Builder
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var locationPermissionLauncher: ActivityResultLauncher<Array<String>>

    private val viewModel: WeatherViewModel by viewModels()

    private var searchJob: Job? = null

    private var keyword = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWeatherBinding.inflate(inflater)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // Setup launcher for permission result
        locationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                if (!it.value) {
                    activity?.finish()
                }
            }

            getLocation()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initStateListener()
        setListeners()
        getLocation()
    }

    /**
     * A function for initializing necessary views and variables
     */
    private fun initViews() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        adapter = LocationAdapter(requireContext())
        errorDialog = AlertDialog.Builder(context)
        errorDialog.setMessage("Something went wrong. Please try again")

        with(binding) {
            val currentTime = getCurrentDateTime().toString("hh:mm a")
            val currentDate = getCurrentDateTime().toString("EEEE, dd MMM yyyy")
            timeTv.text = currentTime
            dateTv.text = currentDate

            searchView.searchRv.layoutManager = LinearLayoutManager(context)
            searchView.searchRv.adapter = adapter
        }
    }

    /**
     * A function for setting up the listeners from API response
     */
    private fun initStateListener() {
        lifecycle.coroutineScope.launch {
            viewModel.forecast.collect {
                if (it.isLoading) {
                    binding.loadingPb.visibility = View.VISIBLE
                }
                if (it.error.isNotBlank()) {
                    binding.loadingPb.visibility = View.GONE
                    errorDialog.show()
                }
                it.data?.let { weatherData ->
                    binding.loadingPb.visibility = View.GONE
                    binding.mainViewCl.visibility = View.VISIBLE
                    updateViews(data = weatherData)
                }
            }
        }

        lifecycle.coroutineScope.launch {
            viewModel.search.collect {
                if (it.isLoading) {

                }
                if (it.error.isNotBlank()) {

                }
                it.data?.let { locationData ->
                    adapter.setLocationsToList(locations = locationData)
                    binding.searchView.searchResultCl.visibility = View.VISIBLE
                }
            }
        }
    }

    /**
     * Setting up listeners such as onClicks, addTextChanged etc.
     */
    private fun setListeners() {
        with(binding) {
            searchBtn.setOnClickListener {
                searchView.root.visibility = View.VISIBLE
                searchView.searchEt.requestFocus()
                showKeyboard()
            }

            adapter.onItemClick = { coordinates ->
                viewModel.getForecastData(keyword = coordinates)
                searchView.root.visibility = View.GONE
                hideKeyboard()
            }

            with(searchView) {
                backIb.setOnClickListener {
                    searchView.root.visibility = View.GONE
                    searchView.searchResultCl.visibility = View.GONE
                    searchView.searchEt.setText("")
                }

                collapseLl.setOnClickListener {
                    if (searchRv.isVisible) {
                        arrowIv.setImageDrawable(resources.getDrawable(R.drawable.ic_arrow_down, null))
                        searchRv.visibility = View.GONE
                    } else {
                        arrowIv.setImageDrawable(resources.getDrawable(R.drawable.ic_arrow_up, null))
                        searchRv.visibility = View.VISIBLE
                    }
                }

                searchEt.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                        // do nothing
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        searchJob?.cancel()

                        if (s.toString().isEmpty()) {

                        } else {
                            searchJob = GlobalScope.launch {
                                delay(1000)
                                viewModel.searchLocation(keyword = s.toString())
                            }
                        }
                    }

                    override fun afterTextChanged(
                        s: Editable?
                    ) {
                        // do nothing
                    }
                })
            }
        }

        errorDialog.setPositiveButton("Try again") { dialog, _ ->
            viewModel.getForecastData(keyword = keyword)
            dialog.dismiss()
        }
    }

    /**
     * A function for updating the views data
     */
    private fun updateViews(data: WeatherData) {
        with(binding) {
            locationTv.text = data.location.name
            tempTv.text = SpannableStringBuilder()
                .bold { append(data.current.currentTemp.toString()) }
                .append(resources.getString(R.string.farenheit_symbol))
            weatherTv.text = data.forecast.forecastDay[0].day.condition.text
            windTv.text = "${floor(data.current.windMph).toInt()} mph"
            humidityTv.text = "${data.current.humidity}%"
            todayTempTv.text = "${data.forecast.forecastDay[0].day.minTempF}°/${data.forecast.forecastDay[0].day.maxTempF}${resources.getString(R.string.farenheit_symbol)}"
            tomorrowTempTv.text = "${data.forecast.forecastDay[1].day.minTempF}°/${data.forecast.forecastDay[1].day.maxTempF}${resources.getString(R.string.farenheit_symbol)}"
            nextnextTempTv.text = "${data.forecast.forecastDay[2].day.minTempF}°/${data.forecast.forecastDay[2].day.maxTempF}${resources.getString(R.string.farenheit_symbol)}"
            dayTv.text = data.forecast.forecastDay[2].date.toDayString()

            with(viewModel) {
                loadImage(requireContext(), data.forecast.forecastDay[0].day.condition.icon, binding.weatherIv)
                loadImage(requireContext(), data.forecast.forecastDay[0].day.condition.icon, binding.todayWeatherIv)
                loadImage(requireContext(), data.forecast.forecastDay[1].day.condition.icon, binding.tomorrowWeatherIv)
                loadImage(requireContext(), data.forecast.forecastDay[2].day.condition.icon, binding.nextnextWeatherIv)
            }
        }
    }

    /**
     * Check if app has permission for location
     */
    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    /**
     * Launch the launcher for permission request
     */
    private fun requestPermissions() {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
    }

    /**
     * Get the current location (lat and lon) for
     */
    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                val locationRequest = CurrentLocationRequest.Builder()
                    .setPriority(PRIORITY_HIGH_ACCURACY)
                    .build()

                mFusedLocationClient.getCurrentLocation(locationRequest, object : CancellationToken() {
                    override fun onCanceledRequested(p0: OnTokenCanceledListener) = CancellationTokenSource().token

                    override fun isCancellationRequested() = false
                })
                    .addOnSuccessListener { location: Location? ->
                        if (location == null)

                        else {
                            val lat = location.latitude
                            val lon = location.longitude
                            keyword = "${lat},${lon}"
                            viewModel.getForecastData(keyword = keyword)
                        }

                    }
            } else {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    /**
     * Check if location is enabled
     */
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager? = getSystemService(requireContext(), LocationManager::class.java)
        return locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true || locationManager?.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        ) == true
    }
}