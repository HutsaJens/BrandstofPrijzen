package com.example.brandstofprijzen

import androidx.test.platform.app.InstrumentationRegistry
import com.example.brandstofprijzen.apis.AnwbApiTankstations
import com.example.brandstofprijzen.model.Locatie
import com.example.brandstofprijzen.model.Tankstation
import com.example.brandstofprijzen.network.ApiClient
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class AnwbApitankstationsTest {

    @Test
    fun testParseFuelData() = runBlocking {
        // Mock API response
        val mockResponse = """
            {
                "displayName": "Fuel Station",
                "name": "Station",
                "address": {
                    "addressLocality": "City",
                    "streetAddress": "123 Street"
                },
                "geo": {
                    "longitude": 1.23456,
                    "latitude": 2.34567
                },
                "fuels": [
                    {
                        "name": "Diesel",
                        "recordDate": "2023-09-10",
                        "price": {
                            "value": "150"
                        }
                    },
                    {
                        "name": "Euro95",
                        "recordDate": "2023-09-10",
                        "price": {
                            "value": "170"
                        }
                    }
                ]
            }
        """

        val mockApiClient = object : ApiClient {
            override suspend fun makeApiRequest(url: String): String {
                // Return the mock response
                return mockResponse
            }
        }

        val anwbApitankstations = AnwbApiTankstations(mockApiClient, InstrumentationRegistry.getInstrumentation().targetContext)
        val tankstation = anwbApitankstations.parseFuelData("123")

        // Assert the expected values
        val expectedTankstation = Tankstation(
            "123",
            "Fuel Station",
            Locatie("123 Street", "City", 1.23456, 2.34567),
            mapOf("Diesel" to "€1.50", "Euro95" to "€1.70"),
            mapOf("Diesel" to "10-09-2023", "Euro95" to "10-09-2023")
        )
        assertEquals(expectedTankstation, tankstation)
    }
}
