package com.example.brandstofprijzen

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import com.example.brandstofprijzen.model.Tankstation
import com.example.brandstofprijzen.model.Locatie
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.*
import org.json.JSONObject

import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ListFragment : Fragment() {

    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<Tankstation>
    private val tankStations = mapOf(
        "Argos - Sprang-Capelle" to "3430",
        "Tango - Kaatsheuvel" to "11465",
        "Tinq - Waalwijk" to "11529",
        "Texaco - Waalwijk" to "7159",
        "Esso - A27 Dorst" to "3030",
        "Argos - Moergestel" to "3719",
        "Esso - N261 Waalwijk" to "3471",
        "Shell Beerens - Sprang-Capelle" to "3483")



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        // Retrieve the selected fuel string from the arguments bundle
        val selectedFuel = arguments?.getString("selectedFuel")
        if (selectedFuel != null) {
            Log.e("SelectedView", selectedFuel)
        }


        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        listView = view.findViewById(R.id.lvBrandstof)

        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1)
        listView.adapter = adapter

        listView.setOnItemClickListener { parent, _, position, _ ->
            val b = parent.getItemAtPosition(position) as Tankstation
            parentFragmentManager.findFragmentById(R.id.brandstofDetailsFragment)?.let {
                (it as? BrandstofDetailsFragment)?.fillInfo(b, selectedFuel)
            }
        }

        selectedFuel?.let { fillListView(it) }

        return view
    }

    private fun fillListView(selectedFuel: String) {
        val tankstationList = ArrayList<Tankstation>()
        val scope = CoroutineScope(Dispatchers.Main)

        // Launch coroutines for each tank station and collect the results in a list
        val deferredList = tankStations.map { (name, id) ->
            scope.async(Dispatchers.IO) {
                parseFuelData(id)
            }
        }



        // Wait for all coroutines to complete and add the results to the list
        scope.launch {
            try {
                deferredList.awaitAll().forEach { tankstationList.add(it) }
                tankstationList.sortWith(compareBy { it.prijs[selectedFuel]?.substring(1)?.toDouble() })

                // Filter out stations where the selected fuel price is null
                val filteredList = tankstationList.filter { it.prijs[selectedFuel]?.isNotBlank() == true }

                // Update the adapter with the filtered data
                adapter.clear()
                adapter.addAll(filteredList)
                adapter.notifyDataSetChanged()
            } catch (e: Exception) {
                Log.e("fillListView", "Error in coroutine", e)
            }
        }
    }

    private suspend fun parseFuelData(identifier: String): Tankstation = withContext(Dispatchers.IO) {
        val dotenv = dotenv {
            directory = "/assets"
            filename = "env" // instead of '.env', use 'env'
        }
        val apiKey = dotenv["API_KEY"]
        val url = "https://api.anwb.nl/v2/pois/fuel/$identifier?apikey=$apiKey"
        val response = URL(url).readText()

        val data = JSONObject(response)
        val displayName = data.getString("displayName")

        // Adres gegevens
        val locality = data.getJSONObject("address").getString("addressLocality")
        val adres = data.getJSONObject("address").getString("streetAddress")
        val longitude = data.getJSONObject("geo").getString("longitude").toDouble()
        val latitude = data.getJSONObject("geo").getString("latitude").toDouble()

        val fuels = data.getJSONArray("fuels")
        val prices = mutableMapOf<String, String>()
        val datums = mutableMapOf<String, String>()

        for (i in 0 until fuels.length()) {
            val fuel = fuels.getJSONObject(i)

            val fuelType = fuel.getString("name")
            if (fuel.has("recordDate")) {
                val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                val date = LocalDate.parse(fuel.getString("recordDate"))
                datums[fuelType] = date.format(formatter)
            } else {
                datums[fuelType] = "Onbekend"
            }



            val value = fuel.getJSONObject("price").getString("value")
            val formattedValue = "€${value.substring(0, 1)}.${value.substring(1)}"
            prices[fuelType] = formattedValue
        }

        val locatie = Locatie(adres, locality, longitude, latitude)

        Tankstation(Integer.parseInt(identifier), displayName, locatie, datums, prices)
    }
}