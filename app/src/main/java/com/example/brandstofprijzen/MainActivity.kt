package com.example.brandstofprijzen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import com.example.brandstofprijzen.location.LocationHelper
import com.example.brandstofprijzen.network.hasNetworkConnection


class MainActivity : AppCompatActivity() {

    private val fuelTypeArray =
        arrayOf("Diesel (B7)", "Euro 95 (E10)", "Super Plus 98 (E5)", "Premium diesel", "LPG")

    override fun onCreate(savedInstanceState: Bundle?) {

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val spinner: Spinner = findViewById(R.id.spinner)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, fuelTypeArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        val buttonSearchTankstation = findViewById<Button>(R.id.button_search_tankstation)
        buttonSearchTankstation.setOnClickListener {
            buttonPressed(spinner, isLocal = false, isFavButton = false)
        }

        val buttonLocal = findViewById<Button>(R.id.button_local)
        buttonLocal.setOnClickListener {
            buttonPressed(spinner, isLocal = true, isFavButton = false)
        }

        val buttonFav = findViewById<Button>(R.id.button_fav)
        buttonFav.setOnClickListener {
            favButtonPressed()
            buttonPressed(spinner, isLocal = false, isFavButton = true)
        }

        val toastMessage = intent.getStringExtra("toastMessage")
        if (toastMessage != null) {
            Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun buttonPressed(spinner: Spinner, isLocal: Boolean, isFavButton: Boolean) {

        val isConnected = hasNetworkConnection(applicationContext)
        if (!isConnected) {
            Toast.makeText(this, "Maak A.U.B. verbinding met een netwerk", Toast.LENGTH_SHORT)
                .show()
            return
        }

        val selectedFuel = spinner.selectedItem.toString()

        val intent = Intent(this, BrandstofActivity::class.java)
        intent.putExtra("selectedFuel", selectedFuel)
        intent.putExtra("local", isLocal)
        intent.putExtra("favButtonPressed", isFavButton)

        if (isLocal || isFavButton) {
            startActivity(intent)
        } else {
            val locationHelper = LocationHelper(this)

            // Call the getCurrentLocation function
            locationHelper.getLastKnownLocation { latitude, longitude ->
                Log.d("MainActivity", "Latitude: $latitude, Longitude: $longitude")

                intent.putExtra("latitude", latitude)
                intent.putExtra("longitude", longitude)
                startActivity(intent)
            }
        }
    }

    private fun favButtonPressed() {
//        println(getDistanceFromLatLonInKm(51.679135, 5.025626, 51.665323, 5.041896))

//        val lifecycleScope = CoroutineScope(Dispatchers.Main)
//        lifecycleScope.launch {
//            val apiKey = scrapeAPIKey() ?: return@launch
//
//            println("New API key: $apiKey")
//        }
    }
}