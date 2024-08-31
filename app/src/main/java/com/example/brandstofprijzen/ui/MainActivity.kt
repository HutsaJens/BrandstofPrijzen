package com.example.brandstofprijzen.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.brandstofprijzen.R
import com.example.brandstofprijzen.application.LocationHelper
import com.example.brandstofprijzen.application.PetrolCacheManager
import com.example.brandstofprijzen.domain.ButtonType
import com.example.brandstofprijzen.infrastructure.hasNetworkConnection


class MainActivity : AppCompatActivity() {

    private val fuelTypeArray =
        arrayOf("Diesel (B7)", "Euro 95 (E10)", "Super Plus 98 (E5)", "Premium diesel", "LPG")

    override fun onCreate(savedInstanceState: Bundle?) {

        val cacheManager = PetrolCacheManager(this)
        cacheManager.removeOutdatedEntries()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val spinner: Spinner = findViewById(R.id.spinner)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, fuelTypeArray)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // set the default value of the spinner to the value in the cache if it exists, else set to the first value in the array
        val sharedPref = getSharedPreferences("sharedPref", MODE_PRIVATE)
        val selectedFuel = sharedPref.getString("selectedFuel", fuelTypeArray[0])

        spinner.setSelection(adapter.getPosition(selectedFuel))

        val buttonSearchTankstation = findViewById<Button>(R.id.button_search_tankstation)
        buttonSearchTankstation.setOnClickListener {
            buttonPressed(spinner.selectedItem.toString(), ButtonType.SEARCH)
        }

        val buttonLocal = findViewById<Button>(R.id.button_local)
        buttonLocal.setOnClickListener {
            buttonPressed(spinner.selectedItem.toString(), ButtonType.LOCAL)
        }

        val buttonFav = findViewById<Button>(R.id.button_fav)
        buttonFav.setOnClickListener {
            buttonPressed(spinner.selectedItem.toString(), ButtonType.FAV)
        }

    }

    private fun buttonPressed(selectedFuel: String, buttonType: ButtonType) {

        // cache the selectedFuel
        val sharedPref = getSharedPreferences("sharedPref", MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("selectedFuel", selectedFuel)
        editor.apply()

        val isConnected = hasNetworkConnection(applicationContext)
        if (!isConnected) {
            ToastManager(this).showToast("Maak A.U.B. verbinding met een netwerk")
            return
        }

        val intent = Intent(this, PetrolActivity::class.java).apply {
            putExtra("selectedFuel", selectedFuel)
            putExtra("buttonType", buttonType.toString())
        }

        when (buttonType) {
            ButtonType.LOCAL, ButtonType.FAV -> {
                startActivity(intent)
            }
            ButtonType.SEARCH -> {
                val locationHelper = LocationHelper(this)
                locationHelper.getLastKnownLocation { latitude, longitude ->
                    Log.d("MainActivity", "Latitude: $latitude, Longitude: $longitude")
                    intent.putExtra("latitude", latitude)
                    intent.putExtra("longitude", longitude)
                    startActivity(intent)
                }
            }
        }
    }
}