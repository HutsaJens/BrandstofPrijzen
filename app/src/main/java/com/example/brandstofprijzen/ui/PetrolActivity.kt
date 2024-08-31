package com.example.brandstofprijzen.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.brandstofprijzen.R
import com.example.brandstofprijzen.infrastructure.CacheManager
import com.example.brandstofprijzen.domain.ButtonType
import com.example.brandstofprijzen.domain.PetrolStation


class PetrolActivity : AppCompatActivity() {

    private var selectedFuel: String? = null
    private var lastKnownLatitude: Double = 0.0
    private var lastKnownLongitude: Double = 0.0
    private var buttonType: ButtonType? = null
    private val toastManager: ToastManager = ToastManager(this)
    private lateinit var petrolDetailsFragment: PetrolDetailsFragment

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_brandstof)

        // Get the selected fuel from the intent
        selectedFuel = intent.getStringExtra("selectedFuel")
        lastKnownLatitude = intent.getDoubleExtra("latitude", 0.0)
        lastKnownLongitude = intent.getDoubleExtra("longitude", 0.0)

        val buttonTypeString: String? = intent.getStringExtra("buttonType")
        buttonType = buttonTypeString?.let { enumValueOf<ButtonType>(it) }


        petrolDetailsFragment = PetrolDetailsFragment()

        val openInMapsButton = findViewById<Button>(R.id.button_maps)
        openInMapsButton.setOnClickListener {
            openInMaps()
        }

        val buttonFavorite = findViewById<Button>(R.id.button_save)
        buttonFavorite.setOnClickListener {
            if (buttonType == ButtonType.FAV) {
                deletePetrolStationFromFavoriteCache()
            } else {
                savePetrolStationInFavoriteCache()
            }
        }

        if (buttonType == ButtonType.FAV) {
            buttonFavorite.setText(R.string.favButtonStringTwo)
        }

        println("Lat: $lastKnownLatitude - Long: $lastKnownLongitude")

        // Replace the ListFragment with a new instance that includes the selected fuel as an argument
        val listFragment = ListFragment()
        val bundle = Bundle()
        bundle.putString("selectedFuel", selectedFuel)
        bundle.putDouble("latitude", lastKnownLatitude)
        bundle.putDouble("longitude", lastKnownLongitude)
        bundle.putString("buttonType", buttonType.toString())


        listFragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(R.id.brandstofListFragment, listFragment)
            .replace(R.id.brandstofDetailsFragment, petrolDetailsFragment)
            .commit()

        // Wait for the fragment transaction to complete before accessing the adapter
        supportFragmentManager.executePendingTransactions()

    }

    private fun openInMaps() {
        val selectedPetrolStation: PetrolStation? = petrolDetailsFragment.getSelectedTankstation()

        if (selectedPetrolStation == null) {
            toastManager.showToast("Selecteer A.U.B. een tankstation")
            return
        }
        val latitude: String = selectedPetrolStation.location.latitude.toString()
        val longitude: String = selectedPetrolStation.location.longitude.toString()

        val gmmIntentUri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")

        try {
            startActivity(mapIntent)
        } catch (e: ActivityNotFoundException) {
            toastManager.showToast("Google Maps is niet geïnstalleerd")
        }

    }


    private fun handleFavorite(actionType: ActionType) {
        val selectedTankstation = petrolDetailsFragment.getSelectedTankstation()
        if (selectedTankstation == null) {
            toastManager.showToast("Selecteer A.U.B. een tankstation")
            return
        }

        val cacheManager = CacheManager(this)
        val key = selectedTankstation.id

        // Perform the action based on the ActionType
        val actionWasSuccessful = when (actionType) {
            ActionType.SAVE -> cacheManager.writeToCacheFile(key)
            ActionType.DELETE -> cacheManager.removeFromCacheFile(key)
        }

        // Show a toast message based on the success of the action
        val successMessage = "Actie succesvol"
        val errorMessage = "Er is een fout opgetreden, probeer het nogmaals"
        val message = if (actionWasSuccessful) successMessage else errorMessage
        toastManager.showToast(message)
    }

    private fun savePetrolStationInFavoriteCache() {
        handleFavorite(ActionType.SAVE)
    }

    private fun deletePetrolStationFromFavoriteCache() {
        handleFavorite(ActionType.DELETE)
    }

    enum class ActionType {
        SAVE, DELETE
    }
}