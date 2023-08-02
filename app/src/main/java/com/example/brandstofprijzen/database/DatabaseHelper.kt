package com.example.brandstofprijzen.database
//
//import android.content.ContentValues
//import android.content.ContentValues.TAG
//import android.content.Context
//import android.database.sqlite.SQLiteDatabase
//import android.database.sqlite.SQLiteOpenHelper
//import android.util.Log
//import com.example.brandstofprijzen.model.Locatie
//import com.example.brandstofprijzen.model.Tankstation
//
//const val DATABASE_NAME = "TankPrijzen.db"
//const val TABLE_NAME = "Tankstations"
//const val COL_ID = "ID"
//const val COL_NAAM = "NAAM"
//const val COL_ADRES = "ADRES"
//const val COL_LOCATIE = "LOCATIE"
//const val COL_LONGITUDE = "LONGITUDE"
//const val COL_LATITUDE = "LATITUDE"
//const val COL_DIESEL_PRIJS = "DIESELPRIJS"
//const val COL_EURO95_PRIJS = "EURO95PRIJS"
//const val COL_EURO98_PRIJS = "EURO98PRIJS"
//const val COL_LPG_PRIJS = "LPGPRIJS"
//const val COL_DIESEL_DATUM = "DIESELDATUM"
//const val COL_EURO95_DATUM = "EURO95DATUM"
//const val COL_EURO98_DATUM = "EURO98DATUM"
//const val COL_LPG_DATUM = "LPGDATUM"
//
//class DatabaseHelper(context: Context) :
//    SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {
//
//    override fun onCreate(db: SQLiteDatabase?) {
//        val createTable =
//            "CREATE TABLE $TABLE_NAME (" +
//                    "$COL_ID INTEGER PRIMARY KEY," +
//                    "$COL_NAAM TEXT," +
//                    "$COL_ADRES TEXT," +
//                    "$COL_LOCATIE TEXT," +
//                    "$COL_LONGITUDE TEXT," +
//                    "$COL_LATITUDE TEXT," +
//                    "$COL_DIESEL_PRIJS TEXT," +
//                    "$COL_EURO95_PRIJS TEXT," +
//                    "$COL_EURO98_PRIJS TEXT," +
//                    "$COL_DIESEL_DATUM TEXT," +
//                    "$COL_EURO95_DATUM TEXT," +
//                    "$COL_EURO98_DATUM TEXT," +
//                    "$COL_LPG_DATUM TEXT)"
//
//        db?.execSQL(createTable)
//    }
//
//    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
//        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
//        onCreate(db)
//    }
//
//    fun insertData(tk: Tankstation): Boolean {
//        val values = ContentValues().apply {
//            put(COL_ID, tk.id)
//            put(COL_NAAM, tk.naam)
//            put(COL_ADRES, tk.locatie.adres)
//            put(COL_LOCATIE, tk.locatie.locatie)
//            put(COL_LONGITUDE, tk.locatie.longitude)
//            put(COL_LATITUDE, tk.locatie.latitude)
//            put(COL_DIESEL_PRIJS, tk.prijs["diesel"])
//            put(COL_EURO95_PRIJS, tk.prijs["euro95"])
//            put(COL_EURO98_PRIJS, tk.prijs["euro98"])
//            put(COL_LPG_PRIJS, tk.prijs["autogas"])
//            put(COL_DIESEL_DATUM, tk.checkDate["diesel"])
//            put(COL_EURO95_DATUM, tk.checkDate["euro95"])
//            put(COL_EURO98_DATUM, tk.checkDate["euro98"])
//            put(COL_LPG_DATUM, tk.checkDate["autogas"])
//        }
//
//        val db = writableDatabase
//        val newRowId = db.insert(TABLE_NAME, null, values)
//        return newRowId != -1L
//    }
//
//    fun readData(whereClause: String): List<Tankstation> {
//        val tankstationList = mutableListOf<Tankstation>()
//
//        val selectQuery = "SELECT * FROM $TABLE_NAME WHERE $whereClause"
//        val db = this.readableDatabase
//        val cursor = db.rawQuery(selectQuery, null)
//
//        try {
//            if (cursor.moveToFirst()) {
//                do {
//                    val id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID))
//                    val naam = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAAM))
//
//                    val adres = cursor.getString(cursor.getColumnIndexOrThrow(COL_ADRES))
//                    val locatie = cursor.getString(cursor.getColumnIndexOrThrow(COL_LOCATIE))
//                    val longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_LONGITUDE))
//                    val latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_LATITUDE))
//
//                    val dieselPrijs = cursor.getString(cursor.getColumnIndexOrThrow(COL_DIESEL_PRIJS))
//                    val euro95Prijs = cursor.getString(cursor.getColumnIndexOrThrow(COL_EURO95_PRIJS))
//                    val euro98Prijs = cursor.getString(cursor.getColumnIndexOrThrow(COL_EURO98_PRIJS))
//                    val lpgPrijs = cursor.getString(cursor.getColumnIndexOrThrow(COL_LPG_PRIJS))
//
//                    val dieselDatum = cursor.getString(cursor.getColumnIndexOrThrow(COL_DIESEL_DATUM))
//                    val euro95Datum = cursor.getString(cursor.getColumnIndexOrThrow(COL_EURO95_DATUM))
//                    val euro98Datum = cursor.getString(cursor.getColumnIndexOrThrow(COL_EURO98_DATUM))
//                    val lpgDatum = cursor.getString(cursor.getColumnIndexOrThrow(COL_LPG_DATUM))
//
//                    val loc = Locatie(adres, locatie, longitude, latitude)
//                    val prijs = hashMapOf<String, String>(
//                        "diesel" to dieselPrijs,
//                        "euro95" to euro95Prijs,
//                        "euro98" to euro98Prijs,
//                        "autogas" to lpgPrijs
//                    )
//                    val datum = hashMapOf<String, String>(
//                        "diesel" to dieselDatum,
//                        "euro95" to euro95Datum,
//                        "euro98" to euro98Datum,
//                        "autogas" to lpgDatum
//                    )
//
//                    val tk = Tankstation(id, naam, loc, prijs, datum)
//
//                    tankstationList.add(tk)
//                } while (cursor.moveToNext())
//            }
//        } catch (e: IllegalArgumentException) {
//            Log.e(TAG, "Error while trying to read data from database: ${e.message}")
//        } finally {
//            cursor?.close()
//        }
//
//        return tankstationList
//    }
//}
