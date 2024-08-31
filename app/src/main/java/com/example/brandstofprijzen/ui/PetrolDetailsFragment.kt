package com.example.brandstofprijzen.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.brandstofprijzen.R
import com.example.brandstofprijzen.domain.PetrolStation


class PetrolDetailsFragment : Fragment() {

    private lateinit var view: View
    private var chosenPetrolStation: PetrolStation? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_brandstof_details, container, false)
        return view
    }

    fun fillInfo(petrolStation: PetrolStation, selectedFuel: String?) {
        chosenPetrolStation = petrolStation
        view.findViewById<TextView>(R.id.tvNaam)?.text = petrolStation.name
        view.findViewById<TextView>(R.id.tvLocatie)?.text = petrolStation.location.locatie
        view.findViewById<TextView>(R.id.tvAdres)?.text = petrolStation.location.adres
        view.findViewById<TextView>(R.id.tvPrijs).text = petrolStation.prices[selectedFuel]
        view.findViewById<TextView>(R.id.tvDate).text = petrolStation.lastPriceChangeDates[selectedFuel]
    }

    fun getSelectedTankstation(): PetrolStation? {
        return chosenPetrolStation
    }
}