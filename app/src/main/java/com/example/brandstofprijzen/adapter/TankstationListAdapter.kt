package com.example.brandstofprijzen.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.brandstofprijzen.R
import com.example.brandstofprijzen.model.Tankstation
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TankstationListAdapter(
    context: Context,
    tankstationList: List<Tankstation>,
    private val selectedFuel: String) :
    ArrayAdapter<Tankstation>(context, 0, tankstationList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView

        // If convertView is null, inflate a new view
        if (view == null) {
            view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false)
        }

        // Get the Tankstation object for the current position
        val tankstation = getItem(position)

        // Update the text of the TextView
        val textView = view!!.findViewById<TextView>(android.R.id.text1)

        val displayText = "${tankstation?.naam} - ${tankstation?.locatie?.locatie} - ${tankstation?.prijs?.get(selectedFuel)}"
        textView.text = displayText

        val textColorBlack = ContextCompat.getColor(context, R.color.colorPrimary)
        val textColorOnbekend = ContextCompat.getColor(context, R.color.colorOnbekend)
        val textColorOrange = ContextCompat.getColor(context, R.color.colorOrange)

        // Set the text color to orange if checkDate[selectedFuel] is not today
        val checkDate = tankstation?.checkDate?.get(selectedFuel)
        if (checkDate != null && checkDate != LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))) {
            if (checkDate == "Onbekend") {
                textView?.setTextColor(textColorOnbekend)

            } else {
                textView.setTextColor(textColorOrange)
            }
        } else {
            textView.setTextColor(textColorBlack)
        }

        return view
    }
}

