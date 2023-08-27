package com.example.brandstofprijzen.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.brandstofprijzen.R
import com.example.brandstofprijzen.util.CacheManager


class BrandstofActivity : AppCompatActivity() {

    private var selectedFuel: String? = null
    private var lastKnownLatitude: Double = 0.0
    private var lastKnownLongitude: Double = 0.0
    private var localOnly: Boolean = false
    private lateinit var brandstofDetailsFragment: BrandstofDetailsFragment

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_brandstof)

        // Get the selected fuel from the intent
        selectedFuel = intent.getStringExtra("selectedFuel")
        lastKnownLatitude = intent.getDoubleExtra("latitude", 0.0)
        lastKnownLongitude = intent.getDoubleExtra("longitude", 0.0)
        localOnly = intent.getBooleanExtra("local", false)


        val buttonMaps = findViewById<Button>(R.id.button_maps)
        buttonMaps.setOnClickListener {
            openInMaps()
        }

        val favButtonPressed = intent.getBooleanExtra("favButtonPressed", false)
        val buttonFavorite = findViewById<Button>(R.id.button_save)
        buttonFavorite.setOnClickListener {
            if (favButtonPressed) {
                deleteFavorite()
            } else {
                saveFavorite()
            }
        }

        if (favButtonPressed) {
            buttonFavorite.setText(R.string.favButtonStringTwo)
        }

        println("Lat: $lastKnownLatitude - Long: $lastKnownLongitude")

        // Replace the ListFragment with a new instance that includes the selected fuel as an argument
        val listFragment = ListFragment()
        val bundle = Bundle()
        bundle.putString("selectedFuel", selectedFuel)
        bundle.putDouble("latitude", lastKnownLatitude)
        bundle.putDouble("longitude", lastKnownLongitude)
        bundle.putBoolean("local", localOnly)
        bundle.putBoolean("favButtonPressed", favButtonPressed)
        listFragment.arguments = bundle

        brandstofDetailsFragment = BrandstofDetailsFragment()

        supportFragmentManager.beginTransaction()
            .replace(R.id.brandstofListFragment, listFragment)
            .replace(R.id.brandstofDetailsFragment, brandstofDetailsFragment)
            .commit()

        // Wait for the fragment transaction to complete before accessing the adapter
        supportFragmentManager.executePendingTransactions()

    }

    private fun openInMaps() {
        val selectedTankstation = brandstofDetailsFragment.getSelectedTankstation()

        if (selectedTankstation != null) {
            val latitude: String = selectedTankstation.locatie.latitude.toString()
            val longitude: String = selectedTankstation.locatie.longitude.toString()

            val gmmIntentUri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")

            try {
                startActivity(mapIntent)
            } catch (e: ActivityNotFoundException) {
                showToast("Google Maps is niet geïnstalleerd")
            }
        } else {
            showToast("Selecteer A.U.B. een tankstation")
        }
    }

    private fun saveFavorite() {
        val selectedTankstation = brandstofDetailsFragment.getSelectedTankstation()
        val cacheManager = CacheManager(this)

        if (selectedTankstation == null) {
            showToast("Selecteer A.U.B. een tankstation")
            return
        }
        val key: String = selectedTankstation.id

        if(cacheManager.writeToCacheFile(key)) {
            showToast("Succesvol opgeslagen")
        } else {
            showToast("Fout tijdens opslaan")
        }

        println(cacheManager.readFromCacheFile())

    }

    private fun deleteFavorite() {
        val cacheManager = CacheManager(this)
        val selectedTankstation = brandstofDetailsFragment.getSelectedTankstation()
        if (selectedTankstation == null) {
            showToast("Selecteer A.U.B. een tankstation")
            return
        }
        val key: String = selectedTankstation.id

        if(cacheManager.removeFromCacheFile(key)) {
            showToast("Succesvol verwijderd")
        } else {
            showToast("Fout tijdens verwijderen")
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}