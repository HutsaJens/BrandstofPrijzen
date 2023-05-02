package com.example.brandstofprijzen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatDelegate

class MainActivity : AppCompatActivity() {

    private val myArray = arrayOf("Diesel (B7)", "Euro 95 (E10)", "Super Plus 98 (E5)", "LPG")

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val spinner: Spinner = findViewById(R.id.spinner)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, myArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        val buttonHoppa = findViewById<Button>(R.id.button_hoppa)
        buttonHoppa.setOnClickListener {
            val selectedFuel = spinner.selectedItem.toString()
            val intent = Intent(this, BrandstofActivity::class.java)
            intent.putExtra("selectedFuel", selectedFuel)
            startActivity(intent)
        }
    }
}