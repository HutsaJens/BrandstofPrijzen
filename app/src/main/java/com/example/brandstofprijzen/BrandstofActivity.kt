package com.example.brandstofprijzen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
//import android.view.View
//import android.widget.AdapterView
//import android.widget.ArrayAdapter
//import android.widget.Spinner

class BrandstofActivity : AppCompatActivity() {

    private var selectedFuel: String? = null
//    private val sortOptions = arrayOf("Prijs", "Tijd")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_brandstof)

        // Get the selected fuel from the intent
        selectedFuel = intent.getStringExtra("selectedFuel")

//        val spinnerSort = findViewById<Spinner>(R.id.spinnerSort)
//        spinnerSort.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, sortOptions).apply {
//            setDropDownViewResource(R.layout.spinner_item)
//        }

        // Replace the ListFragment with a new instance that includes the selected fuel as an argument
        val listFragment = ListFragment()
        val bundle = Bundle()
        bundle.putString("selectedFuel", selectedFuel)
        listFragment.arguments = bundle

        supportFragmentManager.beginTransaction().replace(R.id.brandstofListFragment, listFragment).commit()
    }
}