package com.example.brandstofprijzen

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log

import androidx.fragment.app.Fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView

import com.example.brandstofprijzen.adapter.TankstationListAdapter
import com.example.brandstofprijzen.apis.AnwbApiTankstationIds
import com.example.brandstofprijzen.apis.AnwbApiTankstations
import com.example.brandstofprijzen.model.Tankstation
import com.example.brandstofprijzen.network.OkHttpClientApiClient
import com.example.brandstofprijzen.util.readFromCacheFile

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import java.time.LocalDate

class ListFragment : Fragment() {

    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<Tankstation>

    private var selectedFuel: String? = null

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
    private val fuelTypes = mapOf(
        "Diesel (B7)" to "euro95",
        "Euro 95 (E10)" to "euro95",
        "Super Plus 98 (E5)" to "euro98",
        "Premium diesel" to "diesel_special",
        "LPG" to "autogas"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Retrieve the selected fuel string from the arguments bundle
        selectedFuel = arguments?.getString("selectedFuel")
        selectedFuel?.let { fuelType ->
            Log.e("SelectedView", fuelType)
        }

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        listView = view.findViewById(R.id.lvBrandstof)

        // Create a new instance of the custom adapter and set it as the adapter for the ListView
        adapter = TankstationListAdapter(requireContext(), ArrayList(), selectedFuel ?: "")
        listView.adapter = adapter

        // Set click listener for ListView items
        listView.setOnItemClickListener { parent, _, position, _ ->
            // Get the selected tankstation
            val selectedTankstation = parent.getItemAtPosition(position) as Tankstation

            // Update the BrandstofDetailsFragment with the selected tankstation
            updateBrandstofDetailsFragment(selectedTankstation)
        }

        val favButtonPressed = arguments?.getBoolean("favButtonPressed", false) == true

        // Load the ListView with data based on the selected fuel type
        selectedFuel?.let { fuelType ->
            fillListView(fuelType, favButtonPressed)
        }

        return view
    }

    private fun fillListView(selectedFuel: String, favButtonPressed: Boolean) {
        val scope = CoroutineScope(Dispatchers.Main)

        val longitude = arguments?.getDouble("longitude") ?: 0.0
        val latitude = arguments?.getDouble("latitude") ?: 0.0
        val localOnly = arguments?.getBoolean("local") ?: false

        val apiClient = OkHttpClientApiClient() // or any other implementation of the ApiClient interface
        val anwbApiTankstationIds = AnwbApiTankstationIds(apiClient)
        val anwbApiTankstations = AnwbApiTankstations(apiClient)

        val context = requireContext()

        scope.launch {

            val tankstationIdsList: List<String> = if (localOnly) {
                tankStations.values.toList()
            } else if (favButtonPressed) {
                println("favButtonPressed")
                getSavedTankstationIds(context)
            } else {
                val fuelType = fuelTypes[selectedFuel] ?: "diesel"
                anwbApiTankstationIds.getTankstationIds(context, longitude, latitude, fuelType)
            }
            println("ListFragment - fillListView() | Tankstation list size: ${tankstationIdsList.size}")

            if (tankstationIdsList.isEmpty()) {
                val activity = requireActivity()
                val intent = Intent(activity, MainActivity::class.java)
                intent.putExtra("toastMessage", "Geen tankstations gevonden")
                activity.startActivity(intent)
                return@launch
            }
            val tankstationList: List<Tankstation> = anwbApiTankstations.parseFuelDataList(context, tankstationIdsList)

            println("ListFragment - fillListView() | before sort")
            val sortedList = tankstationList.sortedBy {
                it.prijs[selectedFuel]?.substring(1)?.toDoubleOrNull() ?: 0.0
            }

            println("ListFragment - fillListView() | Prijs: " + tankstationList[0].prijs[selectedFuel])
            val filteredList = sortedList.filter { it.prijs[selectedFuel] != null }
                .map {
                    Tankstation(
                        it.id,
                        it.naam,
                        it.locatie,
                        it.checkDate,
                        mapOf(selectedFuel to (it.prijs[selectedFuel] ?: ""))
                    )
                }
//            println("FilteredList size: " + filteredList.size)
            // Update the adapter with the filtered data
            adapter.clear()
            adapter.addAll(filteredList)
            adapter.notifyDataSetChanged()

            updateBrandstofDetailsFragment(filteredList.firstOrNull())

            for (i in 0 until listView.count) {
                val item = adapter.getItem(i) ?: return@launch

                if (item.checkDate[selectedFuel] != LocalDate.now().toString()) {
                    val textView = listView.getChildAt(i - listView.firstVisiblePosition)?.findViewById<TextView>(android.R.id.text1)
                    if(item.checkDate[selectedFuel] == "Onbekend") {
                        textView?.setTextColor(Color.parseColor("#ED3B4D")) // Rood
                    } else {
                        textView?.setTextColor(Color.parseColor("#FFA500")) // Oranje
                    }
                }
            }
        }
    }

    private fun getSavedTankstationIds(context: Context): List<String> {
        val ids = readFromCacheFile(context) ?: return emptyList()
        return ids.split("\n").dropLast(1)
    }

    private fun updateBrandstofDetailsFragment(selectedTankstation: Tankstation?) {
        println("updateBrandstofDetailsFragment ${selectedTankstation?.id} | $selectedTankstation")

        // Find the BrandstofDetailsFragment
        val fragment = requireActivity().supportFragmentManager.findFragmentById(R.id.brandstofDetailsFragment)

        // Check if the fragment is of type BrandstofDetailsFragment
        if (fragment is BrandstofDetailsFragment && selectedTankstation != null) {
            // Pass the selected tankstation and fuel type to the fragment
                fragment.fillInfo(selectedTankstation, selectedFuel)
        } else {
            println("no fragment :(")
        }
    }
}