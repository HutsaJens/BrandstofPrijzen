package com.example.brandstofprijzen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

//import android.view.View
//import android.widget.AdapterView
//import android.widget.ArrayAdapter
//import android.widget.Spinner

class BrandstofActivity : AppCompatActivity() {

    private var selectedFuel: String? = null
    private var lastKnownLatitude: Double = 0.0
    private var lastKnownLongitude: Double = 0.0

//    private val sortOptions = arrayOf("Prijs", "Tijd")


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_brandstof)

        // Get the selected fuel from the intent
        selectedFuel = intent.getStringExtra("selectedFuel")
        lastKnownLatitude = intent.getDoubleExtra("latitude", 0.0)
        lastKnownLongitude = intent.getDoubleExtra("longitude", 0.0)


        val debug = lastKnownLatitude.toString() + " " + lastKnownLongitude.toString()
        Log.d("Lat and Long", debug)

        // Replace the ListFragment with a new instance that includes the selected fuel as an argument
        val listFragment = ListFragment()
        val bundle = Bundle()
        bundle.putString("selectedFuel", selectedFuel)
        bundle.putDouble("latitude", lastKnownLatitude)
        bundle.putDouble("longitude", lastKnownLongitude)
        listFragment.arguments = bundle

        supportFragmentManager.beginTransaction().replace(R.id.brandstofListFragment, listFragment).commit()
    }
}