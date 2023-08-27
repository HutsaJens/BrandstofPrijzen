package com.example.brandstofprijzen.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.brandstofprijzen.R
import com.example.brandstofprijzen.model.Tankstation


class BrandstofDetailsFragment : Fragment() {

    private lateinit var view: View
    private var tankstationChosen: Tankstation? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_brandstof_details, container, false)
        return view
    }

    fun fillInfo(tankstation: Tankstation, selectedFuel: String?) {
        tankstationChosen = tankstation
        view.findViewById<TextView>(R.id.tvNaam)?.text = tankstation.naam
        view.findViewById<TextView>(R.id.tvLocatie)?.text = tankstation.locatie.locatie
        view.findViewById<TextView>(R.id.tvAdres)?.text = tankstation.locatie.adres
        view.findViewById<TextView>(R.id.tvPrijs).text = tankstation.prijs[selectedFuel]
        view.findViewById<TextView>(R.id.tvDate).text = tankstation.checkDate[selectedFuel]
    }

    fun getSelectedTankstation(): Tankstation? {
        return tankstationChosen
    }
}