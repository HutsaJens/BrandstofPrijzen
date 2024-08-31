package com.example.brandstofprijzen.application.adapter

import android.content.Context
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.brandstofprijzen.R
import com.example.brandstofprijzen.domain.PetrolStation
import java.time.LocalDate

class PetrolStationListAdapterManager {


    fun updatePetrolStationsColor(listView: ListView, adapter: ArrayAdapter<PetrolStation>, selectedFuel: String, currentContext: Context) {
        val textColorUnknown = ContextCompat.getColor(currentContext, R.color.colorOnbekend)
        val textColorOrange = ContextCompat.getColor(currentContext, R.color.colorOrange)

        // Set the text color of the ListView items based on the last price change date
        for (listViewIndex in 0 until listView.count) {
            val item = adapter.getItem(listViewIndex) ?: return

            // If it is price of the fuel is of todays date, continue
            if (item.lastPriceChangeDates[selectedFuel] == LocalDate.now().toString()) {
                continue
            }

            val view : View = listView.getChildAt(listViewIndex - listView.firstVisiblePosition) ?: continue
            val textView = view.findViewById<TextView>(android.R.id.text1)

            // Set the text color based on the last price change date
            if (item.lastPriceChangeDates[selectedFuel] == "Onbekend") {
                textView?.setTextColor(textColorUnknown)
            } else {
                textView?.setTextColor(textColorOrange)
            }
        }
    }
}