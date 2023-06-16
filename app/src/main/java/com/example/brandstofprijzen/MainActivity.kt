package com.example.brandstofprijzen

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import com.example.brandstofprijzen.location.LocationHelper


class MainActivity : AppCompatActivity() {

    private val fuelTypeArray = arrayOf("Diesel (B7)", "Euro 95 (E10)", "Super Plus 98 (E5)", "LPG")
    private val fuelTypeMap = mapOf(
        "Diesel (B7)" to "diesel",
        "Euro 95 (E10)" to "euro95",
        "Super Plus 98 (E5)" to "euro98",
        "LPG" to "autogas"
    )



    private val DB_CREATED_KEY = "db_created"

    private lateinit var locationHelper: LocationHelper
    private var lastKnownLatitude: Double = 0.0
    private var lastKnownLongitude: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Check if the database has been created before
        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val dbCreated = prefs.getBoolean(DB_CREATED_KEY, false)

        // This will call onCreate if the database doesn't exist
//        if (!dbCreated) {
//            // Create the database
//            val dbHelper = DatabaseHelper(this)
//            dbHelper.writableDatabase
//
//            // Save the flag indicating that the database has been created
//            prefs.edit().putBoolean(DB_CREATED_KEY, true).apply()
//        }

        locationHelper = LocationHelper(this)

        // Call the getCurrentLocation function
        locationHelper.getLastKnownLocation { latitude, longitude ->
            lastKnownLatitude = latitude
            lastKnownLongitude = longitude

            Log.d("MainActivity", "Latitude: $latitude, Longitude: $longitude")
        }



        val spinner: Spinner = findViewById(R.id.spinner)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, fuelTypeArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        val buttonHoppa = findViewById<Button>(R.id.button_hoppa)
        buttonHoppa.setOnClickListener {
            val selectedFuel = spinner.selectedItem.toString()
            val simplifiedFuelType = fuelTypeMap[selectedFuel]

            val intent = Intent(this, BrandstofActivity::class.java)
            intent.putExtra("selectedFuel", simplifiedFuelType)
            intent.putExtra("latitude", lastKnownLatitude)
            intent.putExtra("longitude", lastKnownLongitude)
            startActivity(intent)
        }

    }
}