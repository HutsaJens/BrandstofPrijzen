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
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.brandstofprijzen.R

import com.example.brandstofprijzen.adapter.TankstationListAdapter
import com.example.brandstofprijzen.apis.AnwbApiTankstationIds
import com.example.brandstofprijzen.apis.AnwbApiTankstations
import com.example.brandstofprijzen.model.Tankstation
import com.example.brandstofprijzen.network.OkHttpClientApiClient
import com.example.brandstofprijzen.util.CacheManager

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import java.time.LocalDate

class ListFragment : Fragment() {

    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<Tankstation>

    private var selectedFuel: String = ""

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
        selectedFuel = arguments?.getString("selectedFuel") ?: ""

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        listView = view.findViewById(R.id.lvBrandstof)

        // Create a new instance of the custom adapter and set it as the adapter for the ListView
        adapter = TankstationListAdapter(requireContext(), ArrayList(), selectedFuel)
        listView.adapter = adapter

        // Set click listener for ListView items
        listView.setOnItemClickListener { parent, _, position, _ ->
            // Get the selected tankstation
            val selectedTankstation = parent.getItemAtPosition(position) as Tankstation

            // Update the BrandstofDetailsFragment with the selected tankstation
            updateBrandstofDetailsFragment(selectedTankstation)
        }

        // Load the ListView with data based on the selected fuel type
        fillListView(selectedFuel)

        return view
    }

    private fun fillListView(selectedFuel: String) {
        val scope = CoroutineScope(Dispatchers.Main)

        val longitude = arguments?.getDouble("longitude") ?: 0.0
        val latitude = arguments?.getDouble("latitude") ?: 0.0
        val localOnly = arguments?.getBoolean("local") ?: false

        val context = requireContext()

        val apiClient = OkHttpClientApiClient() // or any other implementation of the ApiClient interface
        val anwbApiTankstationIds = AnwbApiTankstationIds(apiClient)
        val anwbApiTankstations = AnwbApiTankstations(apiClient, context)


        val isFavoriteMode  = arguments?.getBoolean("favButtonPressed", false) == true
        scope.launch {

            val tankstationIdsList: List<String> = if (localOnly) {
                tankStations.values.toList()
            } else if (isFavoriteMode ) {
                println("favButtonPressed")
                getSavedTankstationIds(context)
            } else {
                anwbApiTankstationIds.getTankstationIds(context, longitude, latitude, selectedFuel)
            }
            println("ListFragment - fillListView() | Tankstation list size: ${tankstationIdsList.size}")

            if (tankstationIdsList.isEmpty()) {
                val activity = requireActivity()
                val intent = Intent(activity, MainActivity::class.java)
                intent.putExtra("toastMessage", "Geen tankstations gevonden")
                activity.startActivity(intent)
                return@launch
            }

            val tankstationList: List<Tankstation> =
                anwbApiTankstations.parseFuelDataList(tankstationIdsList)

            println("ListFragment - fillListView() | before sort")
            val sortedGasStations = tankstationList.sortedBy {
                it.prijs[selectedFuel]?.substring(1)?.toDoubleOrNull() ?: 0.0
            }

            println("ListFragment - fillListView() | Prijs: " + tankstationList[0].prijs[selectedFuel])
            // Filter gas stations with non-null prices for the selected fuel type
            val gasStationsWithNonNullPrices = sortedGasStations.filter { gasStation -> gasStation.prijs[selectedFuel] != null }

            // Map the filtered list to create modified gas station objects
            val modifiedGasStations  = gasStationsWithNonNullPrices.map { originalGasStation ->
                val selectedFuelPrice = originalGasStation.prijs[selectedFuel] ?: ""
                Tankstation(
                    id = originalGasStation.id,
                    naam = originalGasStation.naam,
                    locatie = originalGasStation.locatie,
                    checkDate = originalGasStation.checkDate,
                    prijs = mapOf(selectedFuel to selectedFuelPrice)
                )
            }


            // Update the adapter with the filtered data
            adapter.clear()
            adapter.addAll(modifiedGasStations )
            adapter.notifyDataSetChanged()

            updateBrandstofDetailsFragment(modifiedGasStations .firstOrNull())

            val textColorOnbekend = ContextCompat.getColor(context, R.color.colorOnbekend)
            val textColorOrange = ContextCompat.getColor(context, R.color.colorOrange)

            for (i in 0 until listView.count) {
                val item = adapter.getItem(i) ?: return@launch

                // If it is price of the fuel is of todays date, continue
                if (item.checkDate[selectedFuel] == LocalDate.now().toString()) {
                    continue
                }

                val textColor = if (item.checkDate[selectedFuel] == "Onbekend") {
                    textColorOnbekend
                } else {
                    textColorOrange
                }

                val textView = listView.getChildAt(i - listView.firstVisiblePosition)
                    ?.findViewById<TextView>(android.R.id.text1)
                textView?.setTextColor(textColor)
            }
        }
    }


    private fun getSavedTankstationIds(context: Context): List<String> {
        val cacheManager = CacheManager(context)
        val ids = cacheManager.readFromCacheFile()
        return ids.split("\n").dropLast(1)
    }

    private fun updateBrandstofDetailsFragment(selectedTankstation: Tankstation?) {
        println("updateBrandstofDetailsFragment ${selectedTankstation?.id} | $selectedTankstation")

        // Find the BrandstofDetailsFragment
        val fragment =
            requireActivity().supportFragmentManager.findFragmentById(R.id.brandstofDetailsFragment)

        // Check if the fragment is of type BrandstofDetailsFragment
        if (fragment is BrandstofDetailsFragment && selectedTankstation != null) {
            // Pass the selected tankstation and fuel type to the fragment
            fragment.fillInfo(selectedTankstation, selectedFuel)
        } else {
            println("no fragment :(")
        }
    }
}