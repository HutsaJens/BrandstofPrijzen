package com.example.brandstofprijzen.application.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.brandstofprijzen.R
import com.example.brandstofprijzen.domain.PetrolStation
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class PetrolStationListAdapter(
    context: Context,
    petrolStationList: List<PetrolStation>,
    private val selectedFuel: String
) : ArrayAdapter<PetrolStation>(context, 0, petrolStationList) {

    private val textColorBlack = ContextCompat.getColor(context, R.color.colorPrimary)
    private val textColorUnknown = ContextCompat.getColor(context, R.color.colorOnbekend)
    private val textColorOrange = ContextCompat.getColor(context, R.color.colorOrange)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        // Get the view for the current position or inflate a new view
        val view = convertView ?: LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false)

        // Get the Tankstation object for the current position
        val petrolStation : PetrolStation = getItem(position) ?: return view!!

        // Update the text of the TextView
        val textView = view.findViewById<TextView>(android.R.id.text1)

        val displayText = "${petrolStation.name} - ${petrolStation.location.locatie} - ${petrolStation.prices[selectedFuel]}"
        textView.text = displayText

        // Set the text color to orange if checkDate[selectedFuel] is not today
        val lastPetrolPriceChangeDate = petrolStation.lastPriceChangeDates[selectedFuel]
        if(lastPetrolPriceChangeDate == null || lastPetrolPriceChangeDate == "Onbekend") {
            textView.setTextColor(textColorUnknown)
            return view
        }

        val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        if (lastPetrolPriceChangeDate == currentDate) {
            textView.setTextColor(textColorBlack)
            return view
        }

        if (lastPetrolPriceChangeDate != LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))) {
            textView.setTextColor(textColorOrange)
            return view
        }

        throw IllegalStateException("This should not happen")
    }
}

