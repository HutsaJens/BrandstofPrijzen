package com.example.brandstofprijzen

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
import com.example.brandstofprijzen.apis.AnwbApiTankstationsFull
import com.example.brandstofprijzen.model.Tankstation
import kotlinx.coroutines.*

import java.time.LocalDate

class ListFragment : Fragment() {

    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<Tankstation>
//    private val tankStations = mapOf(
//        "Argos - Sprang-Capelle" to "3430",
//        "Tango - Kaatsheuvel" to "11465",
//        "Tinq - Waalwijk" to "11529",
//        "Texaco - Waalwijk" to "7159",
//        "Esso - A27 Dorst" to "3030",
//        "Argos - Moergestel" to "3719",
//        "Esso - N261 Waalwijk" to "3471",
//        "Shell Beerens - Sprang-Capelle" to "3483"
//    )


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        // Retrieve the selected fuel string from the arguments bundle
        val selectedFuel = arguments?.getString("selectedFuel")
        if (selectedFuel != null) {
            Log.e("SelectedView", selectedFuel)
        }

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        listView = view.findViewById(R.id.lvBrandstof)

        // Create a new instance of the custom adapter and set it as the adapter for the ListView
        adapter = TankstationListAdapter(requireContext(), ArrayList(), selectedFuel ?: "")
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
        val scope = CoroutineScope(Dispatchers.Main)

        val longitude = arguments?.getDouble("longitude")
        val latitude = arguments?.getDouble("latitude")

        val anwbApiTankstationsFull = AnwbApiTankstationsFull()

        scope.launch {
            try {
                val tankstationList = anwbApiTankstationsFull.parseJSON(longitude!!, latitude!!)
                System.out.println("fillListView() | Tankstation list size: " + tankstationList.size)

                val sortedList = tankstationList.sortedWith(compareBy { it.prijs[selectedFuel]?.substring(1)?.toDouble() ?: 0.0 })

                System.out.println("Prijs: " + tankstationList.get(0).prijs[selectedFuel])
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
                System.out.println("FilteredList size: " + filteredList.size)
                // Update the adapter with the filtered data
                adapter.clear()
                adapter.addAll(filteredList)
                adapter.notifyDataSetChanged()

                for (i in 0 until listView.count) {
                    val item = adapter.getItem(i)
                    if (item != null && item.checkDate[selectedFuel] != LocalDate.now().toString()) {
                        val textView = listView.getChildAt(i - listView.firstVisiblePosition)
                            ?.findViewById<TextView>(android.R.id.text1)
                        textView?.setTextColor(Color.parseColor("#FFA500"))
                    }
                }
            } catch (e: Exception) {
                Log.e("fillListView", "Error in coroutine", e)
            }
        }
    }
}