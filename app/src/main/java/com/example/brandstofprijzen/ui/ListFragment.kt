package com.example.brandstofprijzen.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle

import androidx.fragment.app.Fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.ArrayAdapter
import android.widget.ListView
import com.example.brandstofprijzen.R
import com.example.brandstofprijzen.application.PetrolCacheManager

import com.example.brandstofprijzen.application.adapter.PetrolStationListAdapter
import com.example.brandstofprijzen.application.adapter.PetrolStationListAdapterManager
import com.example.brandstofprijzen.domain.PetrolStation
import com.example.brandstofprijzen.infrastructure.httpclient.OkHttpClientApiClient
import com.example.brandstofprijzen.infrastructure.CacheManager
import com.example.brandstofprijzen.domain.ButtonType
import com.example.brandstofprijzen.infrastructure.api.anwb.AnwbApiService
import com.facebook.shimmer.ShimmerFrameLayout

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ListFragment : Fragment() {

    private lateinit var listView: ListView
    private lateinit var shimmerLayout: ShimmerFrameLayout
    private lateinit var adapter: ArrayAdapter<PetrolStation>

    private val tankStations = mapOf(
        "Argos - Sprang-Capelle" to "3430",
        "Tango - Kaatsheuvel" to "11465",
        "Tinq - Waalwijk" to "11529",
        "Texaco - Waalwijk" to "7159",
        "Esso - A27 Dorst" to "3030",
        "Argos - Moergestel" to "3719",
        "Esso - N261 Waalwijk" to "3471",
        "Shell Beerens - Sprang-Capelle" to "3483"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Retrieve the selected fuel string from the arguments bundle
        val selectedFuel = arguments?.getString("selectedFuel") ?: ""

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        listView = view.findViewById(R.id.lvBrandstof)
        shimmerLayout = view.findViewById(R.id.shimmerLayout)

        // Create a new instance of the custom adapter and set it as the adapter for the ListView
        adapter = PetrolStationListAdapter(requireContext(), ArrayList(), selectedFuel)
        listView.adapter = adapter

        // Set click listener for ListView items
        listView.setOnItemClickListener { parent, _, position, _ ->
            // Get the selected tankstation
            val selectedPetrolStation = parent.getItemAtPosition(position) as PetrolStation

            // Update the BrandstofDetailsFragment with the selected tankstation
            updateBrandstofDetailsFragment(selectedPetrolStation, selectedFuel)
        }

        // Load the ListView with data based on the selected fuel type
        fillListView(selectedFuel)

        return view
    }

    private fun fillListView(selectedFuel: String) {
        val longitude = arguments?.getDouble("longitude") ?: 0.0
        val latitude = arguments?.getDouble("latitude") ?: 0.0
        val buttonType = arguments?.getString("buttonType")?.let { enumValueOf<ButtonType>(it) }

        val anwbApiService = AnwbApiService(OkHttpClientApiClient(), requireContext())
        val petrolCacheManager = PetrolCacheManager(requireContext())

        // A list to hold valid petrol stations
        val petrolStationList = mutableListOf<PetrolStation>()

        // Start shimmer animation
        shimmerLayout.startShimmer()
        shimmerLayout.visibility = View.VISIBLE
        listView.visibility = View.GONE

        CoroutineScope(Dispatchers.IO).launch {
            val tankstationIdsList = when (buttonType) {
                ButtonType.LOCAL -> tankStations.values.toList()
                ButtonType.FAV -> getSavedTankstationIds(requireContext())
                else -> anwbApiService.getTankstationIds(longitude, latitude, selectedFuel)
            }

            if (tankstationIdsList.isEmpty()) {
                withContext(Dispatchers.Main) {
                    ToastManager(requireContext()).showToast("Geen tankstations gevonden")
                    startActivity(Intent(requireActivity(), MainActivity::class.java))
                }
                return@launch
            }

            withContext(Dispatchers.Main) {
                shimmerLayout.stopShimmer()
                shimmerLayout.visibility = View.GONE
                listView.visibility = View.VISIBLE
            }

            tankstationIdsList.forEach { petrolStationId ->
                val petrolStation =
                    petrolCacheManager.getCachedPetrolStation(petrolStationId, selectedFuel)
                        ?.takeIf {
                            it.lastPriceChangeDates[selectedFuel] == LocalDate.now()
                                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                        } ?: anwbApiService.parseFuelData(petrolStationId).also {
                        if (it.lastPriceChangeDates[selectedFuel] == LocalDate.now()
                                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                        ) {
                            petrolCacheManager.savePetrolStation(it, selectedFuel)
                        }
                    }

                petrolStation.let {
                    // Add to the list if the price is not null
                    if (it.prices[selectedFuel] != null) {
                        petrolStationList.add(it)

                        // Sort the list by price after adding each valid petrol station
                        petrolStationList.sortBy { station ->
                            station.prices[selectedFuel]?.substring(1)?.toDoubleOrNull()
                                ?: Double.MAX_VALUE
                        }

                        // Update the adapter with the filtered and sorted data
                        withContext(Dispatchers.Main) {
                            adapter.clear()
                            adapter.addAll(petrolStationList)
                            adapter.notifyDataSetChanged()

                            // get the first item in the list and update the BrandstofDetailsFragment
                            if (adapter.count > 0) {
                                updateBrandstofDetailsFragment(adapter.getItem(0)!!, selectedFuel)
                            }

                            PetrolStationListAdapterManager().updatePetrolStationsColor(
                                listView,
                                adapter,
                                selectedFuel,
                                requireContext()
                            )
                        }
                    }
                }
            }

        }
    }

    private fun getSavedTankstationIds(context: Context): List<String> {
        val cacheManager = CacheManager(context)
        val ids = cacheManager.readFromCacheFile()
        return ids.split("\n").dropLast(1).distinct()
    }

    private fun updateBrandstofDetailsFragment(selectedPetrolStation: PetrolStation, selectedFuel: String) {

        // Find the BrandstofDetailsFragment
        val fragment =
            requireActivity().supportFragmentManager.findFragmentById(R.id.brandstofDetailsFragment)

        // Check if the fragment is of type BrandstofDetailsFragment
        if (fragment is PetrolDetailsFragment) {
            // Pass the selected tankstation and fuel type to the fragment
            fragment.fillInfo(selectedPetrolStation, selectedFuel)
        } else {
            println("Where fragment?")
        }
    }
}