package com.example.brandstofprijzen

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import com.example.brandstofprijzen.model.Brandstof
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.URL

class ListFragment : Fragment() {

    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<Brandstof>
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
            Log.i("SelectedView", selectedFuel)
        }


        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        listView = view.findViewById(R.id.lvBrandstof)

        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1)
        listView.adapter = adapter

        listView.setOnItemClickListener { parent, _, position, _ ->
            val b = parent.getItemAtPosition(position) as Brandstof
            parentFragmentManager.findFragmentById(R.id.brandstofDetailsFragment)?.let {
                (it as? BrandstofDetailsFragment)?.fillInfo(b)
            }
        }

        selectedFuel?.let { fillListView(it) }

        return view
    }

    private fun fillListView(selectedFuel: String) {
        val brandstofList = ArrayList<Brandstof>()
        val scope = CoroutineScope(Dispatchers.Main)

        // Launch coroutines for each tank station and collect the results in a list
        val deferredList = tankStations.map { (name, id) ->
            scope.async(Dispatchers.IO) {
                parseFuelData(selectedFuel, id)
            }
        }

        // Wait for all coroutines to complete and add the results to the list
        scope.launch {
            deferredList.awaitAll().forEach { brandstofList.add(it) }

            brandstofList.sortWith(compareBy { it.prijs.substring(1).toDouble() })

            // Update the adapter with the collected data
            adapter.addAll(brandstofList)
            adapter.notifyDataSetChanged()
        }
    }


    private suspend fun parseFuelData(selectedFuel: String, identifier: String): Brandstof = withContext(Dispatchers.IO) {
        val apiKey = "GttEkIuzzOn4b0sGyPw2F6cLtzd64uUH"
        val url = "https://api.anwb.nl/v2/pois/fuel/$identifier?apikey=$apiKey"
        val response = URL(url).readText()

        val data = JSONObject(response)
        val displayName = data.getString("displayName")
        val locality = data.getJSONObject("address").getString("addressLocality")
        val adres = data.getJSONObject("address").getString("streetAddress")

        val fuels = data.getJSONArray("fuels")
        var dieselValue = ""
        var checkedDate = ""
        for (i in 0 until fuels.length()) {
            val fuel = fuels.getJSONObject(i)
            if (fuel.getString("name") == selectedFuel) {
                dieselValue = fuel.getJSONObject("price").getString("value")
                dieselValue = "€${dieselValue.substring(0, 1)}.${dieselValue.substring(1)}"
                checkedDate = fuel.getString("recordDate")
                break
            }
        }

        Brandstof(displayName, locality, adres, dieselValue, checkedDate)
    }


}