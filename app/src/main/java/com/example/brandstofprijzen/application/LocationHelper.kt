package com.example.brandstofprijzen.application

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.brandstofprijzen.ui.ToastManager
import com.google.android.gms.location.*


class LocationHelper(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    private var locationCallback: LocationCallback? = null
    private var locationObtained: Boolean = false

    private companion object {
        private const val LOCATION_PERMISSION_CODE = 1
    }

    fun getLastKnownLocation(callback: (latitude: Double, longitude: Double) -> Unit) {
        if (!locationObtained) {
            if (isLocationPermissionGranted()) {
                requestLastKnownLocation(callback)
            } else {
                requestLocationPermission()
            }
            return
        }

        // Location has already been obtained, return the cached location
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        callback(it.latitude, it.longitude)
                    }
                }
        } catch (e: SecurityException) {
            ToastManager(context).showToast("Location permission denied")
        }
    }

    private fun isLocationPermissionGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_CODE
        )
    }

    private fun requestLastKnownLocation(callback: (latitude: Double, longitude: Double) -> Unit) {
        val locationInterval: Long = 1000
        val locationRequest =
            LocationRequest.Builder(Priority.PRIORITY_LOW_POWER, locationInterval).build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val locations = locationResult.locations
                if (locations.isEmpty()) {
                    return
                }
                val location = locations[0]
                callback(location.latitude, location.longitude)
                locationObtained = true
                stopLocationUpdates()
            }
        }

        if (!isLocationPermissionGranted()) {
            return
        }
        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback!!,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            Log.e("SecurityException", e.message.toString())
        }

    }

    private fun stopLocationUpdates() {
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
            locationCallback = null
            locationObtained = true
        }
    }
}
