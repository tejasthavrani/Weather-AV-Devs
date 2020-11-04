package com.avdevstest.weatheravdevs

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.avdevstest.weatheravdevs.weatherapidata.WeatherHttpClient
import com.avdevstest.weatheravdevs.weatherapidata.model.Weather
import com.example.admin.kotlinvideorecyclerview.adapter.CustomAdapter
import com.example.admin.kotlinvideorecyclerview.viewmodel.DateTemperatureViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class HomeActivity : AppCompatActivity(), LocationListener {

    private var currentCityName: String? = "Vadodara" // Default location, until live location fetch
    var locationManager: LocationManager? = null
    private var recycleview: RecyclerView? = null
    private var customadapter: CustomAdapter? = null
    lateinit var dateTemperatureViewModel: DateTemperatureViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {// For change the statusbar color
            val window = this@HomeActivity.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = resources.getColor(R.color.color_top)
        }
        txt_loading.visibility = View.VISIBLE // Until live location fetch, its visible

        recycleview = findViewById(R.id.recyclerview) as RecyclerView

        dateTemperatureViewModel = ViewModelProviders.of(this).get(DateTemperatureViewModel::class.java)

        /**MutableLiveData from the ViewModel*/
        dateTemperatureViewModel.getArrayList().observe(this, Observer { dateTemperatureViewModels ->

            customadapter = CustomAdapter(this@HomeActivity, dateTemperatureViewModels!!)
            recycleview!!.setLayoutManager(LinearLayoutManager(this@HomeActivity))
            recycleview!!.setAdapter(customadapter)


        })

        /** I am using Shared Preference to store the data(date,temperature) for displaying in list and chart,
         * we can use local database or we can store it to server via api calling*/
        val allEntries: Map<String, String> =
            SharedPrefManager.getInstance(baseContext).getAllData()

        /**Get all the data which are in Shared Preference*/
        for ((key, value) in allEntries) {
            dateTemperatureViewModel.addToArrayList(key, value.toString())
        }
        /**For empty list, once list have a data, this textview be  will gone till we clear data*/
        if (allEntries.size!! > 0) {
            txt_msg.visibility = View.GONE
        } else {
            txt_msg.visibility = View.VISIBLE
        }

        /**By this button click, current date and time as well as current temperature will be store.
         * (This will be live or current, because when location change by device it will weather detail automatically.) */
        btn_save.setOnClickListener {
            if (isOnline(baseContext)) {
                Toast.makeText(baseContext, "Data successfully added to list!", Toast.LENGTH_LONG)
                    .show()
                var current_datetime =
                    getCurrentDateTime()//txt_date.text.toString() + " " + txt_time.text.toString()
                var current_temperature = txt_temperature.text.toString()

                dateTemperatureViewModel.addToArrayList(
                    current_datetime,
                    current_temperature
                )
                SharedPrefManager.getInstance(baseContext)
                    .setTimeTemperature(current_datetime, current_temperature)

                txt_msg.visibility = View.GONE
            } else {
                Toast.makeText(baseContext, "No Internet Connection!", Toast.LENGTH_LONG)
                    .show()
            }


        }

        /**Move to bar chart activity*/
        btn_chart.setOnClickListener {
            var goToMap = Intent(baseContext, MapActivity::class.java)
            startActivity(goToMap)

        }

        /**Clear all data*/
        btn_clear.setOnClickListener {
            Toast.makeText(baseContext, "Clear Successfully.", Toast.LENGTH_LONG).show()
            SharedPrefManager.getInstance(baseContext).clearAll()
            dateTemperatureViewModel.removeArrayList()
            txt_msg.visibility = View.GONE

        }
        txt_date.text = getCurrentDate()

        /**If devie has a internet connection then it will call for weather detail by a current location*/
        if (isOnline(baseContext)) {
            JSONWeatherTask(this).execute(currentCityName)
        } else {
            Toast.makeText(baseContext, "No Internet Connection!", Toast.LENGTH_LONG)
                .show()
        }

        checkPermissions()
        getLocation()
    }

    public class JSONWeatherTask(private var activity: HomeActivity?) :
        AsyncTask<String?, Void?, Weather>() {
        override fun onPreExecute() {
            super.onPreExecute()

        }

        public override fun doInBackground(vararg params: String?): Weather? {
            var weather = Weather()
            try {
                val data: String = WeatherHttpClient().getWeatherData(params[0])
                try {
                    weather =
                        com.avdevstest.weatheravdevs.weatherapidata.JSONWeatherParser.getWeather(
                            data
                        )


                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } catch (e: Exception) {
                Log.e("", "There is some issue, may be in network bandwidth!")

            }

            return weather
        }

        override fun onPostExecute(weather: Weather) {
            super.onPostExecute(weather)
            if (weather.location != null) {
                Log.e(
                    "weather---",
                    weather.location.getCity().toString() + "," + weather.location.getCountry()
                )
                activity?.txt_city?.text =
                    weather.location.getCity().toString() + "," + weather.location.getCountry()
                activity?.txt_temperature?.text =
                    "" + Math.round(weather.temperature.getTemp() - 273.15).toString() + "°C"
            } else {
                Toast.makeText(
                    activity,
                    "There is some issue, may be in network bandwidth!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    /**For getting current date*/
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrentDate(): String {
        val current = LocalDateTime.now()

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formatted = current.format(formatter)

        return formatted
    }

    /**For getting current date with time*/

    @RequiresApi(Build.VERSION_CODES.O)

    private fun getCurrentDateTime(): String {
        val current = LocalDateTime.now()

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")
        val formatted = current.format(formatter)

        return formatted
    }

    /**For checking, device has a internet connection or not*/
    fun isOnline(mContext: Context?): Boolean {
        if (mContext == null) {
            return false
        }
        val connectivityManager =
            mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }

    var alert: android.app.AlertDialog? = null
    private fun checkPermissions(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {
            val alertBuilder =
                android.app.AlertDialog.Builder(this)
            alertBuilder.setCancelable(true)
            alertBuilder.setTitle("Location permission necessary")
            alertBuilder.setMessage("Your current location is needed to allow us to display current location.")
            alertBuilder.setPositiveButton(
                android.R.string.yes
            ) { dialog, which ->
                this.alert?.dismiss()
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    2
                )
            }
            alert = alertBuilder.create()
            this.alert?.show()
            false
        }
    }


    fun getLocation() {
        try {
            locationManager =
                this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager!!.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                5000,
                5f,
                this
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    /**When location changed, it will collect the live weather data for current location */
    override fun onLocationChanged(location: Location?) {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses =
            geocoder.getFromLocation(location!!.latitude, location!!.longitude, 1)
        currentCityName = addresses[0].locality
        txt_loading.visibility = View.GONE
        if (currentCityName != null) {
            txt_city.text = currentCityName
            if (isOnline(baseContext)) {
                JSONWeatherTask(this).execute(currentCityName)
            } else {
                Toast.makeText(baseContext, "No Internet Connection!", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }

    override fun onProviderEnabled(provider: String?) {
    }

    override fun onProviderDisabled(provider: String?) {
        Toast.makeText(this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show()

    }
}

