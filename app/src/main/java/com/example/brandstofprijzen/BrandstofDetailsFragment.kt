package com.example.brandstofprijzen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.brandstofprijzen.model.Tankstation


class BrandstofDetailsFragment : Fragment() {

    private lateinit var view: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_brandstof_details, container, false)
        return view
    }

    fun fillInfo(tankstation: Tankstation, selectedFuel: String?) {
        view.findViewById<TextView>(R.id.tvNaam)?.text = tankstation.naam
        view.findViewById<TextView>(R.id.tvLocatie)?.text = tankstation.locatie.locatie
        view.findViewById<TextView>(R.id.tvAdres)?.text = tankstation.locatie.adres

        val fuelTypeArray = arrayOf("Diesel (B7)", "Euro 95 (E10)", "Super Plus 98 (E5)", "LPG")

        val fuelType = when (selectedFuel) {
            fuelTypeArray[1] -> "Euro 95 (E10)"
            fuelTypeArray[2] -> "Super Plus 98 (E5)"
            fuelTypeArray[3] -> "LPG"
            else -> "Diesel (B7)"
        }

        view.findViewById<TextView>(R.id.tvPrijs).text = tankstation.prijs[selectedFuel]
        view.findViewById<TextView>(R.id.tvDate).text = tankstation.checkDate[selectedFuel]
    }
}