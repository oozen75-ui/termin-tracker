package com.termintracker.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class NominatimResponse(
    val place_id: Long? = null,
    val licence: String? = null,
    val lat: String? = null,
    val lon: String? = null,
    val display_name: String? = null,
    val type: String? = null,
    val address: NominatimAddress? = null
)

@Serializable
data class NominatimAddress(
    val road: String? = null,
    val house_number: String? = null,
    val postcode: String? = null,
    val city: String? = null,
    val town: String? = null,
    val village: String? = null,
    val suburb: String? = null,
    val neighbourhood: String? = null,
    val state: String? = null,
    val country: String? = null
)

data class AddressResult(
    val street: String,
    val houseNumber: String,
    val postalCode: String,
    val city: String,
    val district: String,
    val fullAddress: String
)

@Serializable
data class AddressSearchResult(
    val displayName: String,
    val latitude: Double,
    val longitude: Double,
    val postalCode: String? = null
)

class OpenStreetMapService {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true; isLenient = true })
        }
    }
    
    private val baseUrl = "https://nominatim.openstreetmap.org"
    
    suspend fun searchAddress(query: String): List<AddressResult> {
        return try {
            val response: List<NominatimResponse> = client.get("$baseUrl/search") {
                parameter("q", query)
                parameter("format", "json")
                parameter("addressdetails", "1")
                parameter("limit", "5")
                parameter("countrycodes", "de")
            }.body()
            
            response.mapNotNull { result ->
                val address = result.address ?: return@mapNotNull null
                
                AddressResult(
                    street = address.road ?: "",
                    houseNumber = address.house_number ?: "",
                    postalCode = address.postcode ?: "",
                    city = address.city ?: address.town ?: address.village ?: "",
                    district = address.suburb ?: address.neighbourhood ?: "",
                    fullAddress = result.display_name ?: ""
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun getDistrictsForCity(city: String): List<String> {
        return try {
            val response: List<NominatimResponse> = client.get("$baseUrl/search") {
                parameter("q", city)
                parameter("format", "json")
                parameter("addressdetails", "1")
                parameter("limit", "50")
                parameter("countrycodes", "de")
                parameter("featuretype", "city")
            }.body()
            
            // Extract unique suburbs/districts from results
            val districts = response.mapNotNull { it.address?.suburb }
                .distinct()
                .sorted()
            
            if (districts.isEmpty()) {
                // Fallback to predefined districts for major cities
                getFallbackDistricts(city)
            } else {
                districts
            }
        } catch (e: Exception) {
            getFallbackDistricts(city)
        }
    }
    
    private fun getFallbackDistricts(city: String): List<String> {
        return when (city) {
            "Berlin" -> listOf("Mitte", "Charlottenburg-Wilmersdorf", "Friedrichshain-Kreuzberg", "Pankow", "Neukölln", "Treptow-Köpenick", "Steglitz-Zehlendorf", "Tempelhof-Schöneberg", "Spandau", "Marzahn-Hellersdorf", "Lichtenberg", "Reinickendorf")
            "Hamburg" -> listOf("Hamburg-Mitte", "Altona", "Eimsbüttel", "Hamburg-Nord", "Wandsbek", "Bergedorf", "Harburg")
            "München" -> listOf("Altstadt-Lehel", "Ludwigsvorstadt-Isarvorstadt", "Maxvorstadt", "Schwabing-West", "Au-Haidhausen", "Sendling", "Laim")
            "Köln" -> listOf("Innenstadt", "Nippes", "Ehrenfeld", "Kalk", "Mülheim", "Porz", "Rodenkirchen")
            "Frankfurt" -> listOf("Innenstadt", "Westend", "Sachsenhausen", "Nordend-Ostend", "Bornheim", "Bockenheim")
            "Düsseldorf" -> listOf("Stadtbezirk 01", "Stadtbezirk 02", "Stadtbezirk 03", "Stadtbezirk 04", "Stadtbezirk 05", "Stadtbezirk 06", "Stadtbezirk 07", "Stadtbezirk 08", "Stadtbezirk 09", "Stadtbezirk 10")
            "Stuttgart" -> listOf("Mitte", "Bad Cannstatt", "Degerloch", "Feuerbach", "Hedelfingen", "Möhringen", "Plieningen")
            else -> listOf("Zentrum", "Nord", "Ost", "Süd", "West")
        }
    }
    
    suspend fun searchByCoordinates(lat: Double, lon: Double): AddressResult? {
        return try {
            val response: NominatimResponse = client.get("$baseUrl/reverse") {
                parameter("lat", lat.toString())
                parameter("lon", lon.toString())
                parameter("format", "json")
                parameter("addressdetails", "1")
            }.body()
            
            val address = response.address ?: return null
            
            AddressResult(
                street = address.road ?: "",
                houseNumber = address.house_number ?: "",
                postalCode = address.postcode ?: "",
                city = address.city ?: address.town ?: address.village ?: "",
                district = address.suburb ?: address.neighbourhood ?: "",
                fullAddress = response.display_name ?: ""
            )
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun searchCities(query: String): List<AddressSearchResult> {
        return try {
            val response: List<NominatimResponse> = client.get("$baseUrl/search") {
                parameter("q", query)
                parameter("format", "json")
                parameter("addressdetails", "1")
                parameter("limit", "10")
                parameter("countrycodes", "de")
                parameter("featuretype", "city")
            }.body()

            response.mapNotNull { result ->
                result.display_name?.let {
                    AddressSearchResult(
                        displayName = it,
                        latitude = result.lat?.toDoubleOrNull() ?: 0.0,
                        longitude = result.lon?.toDoubleOrNull() ?: 0.0
                    )
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun searchDistricts(city: String, query: String): List<AddressSearchResult> {
        return try {
            val fullQuery = "$city $query"
            val response: List<NominatimResponse> = client.get("$baseUrl/search") {
                parameter("q", fullQuery)
                parameter("format", "json")
                parameter("addressdetails", "1")
                parameter("limit", "10")
                parameter("countrycodes", "de")
            }.body()

            response.mapNotNull { result ->
                result.display_name?.let {
                    AddressSearchResult(
                        displayName = it,
                        latitude = result.lat?.toDoubleOrNull() ?: 0.0,
                        longitude = result.lon?.toDoubleOrNull() ?: 0.0
                    )
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun searchPostalCodes(query: String): List<AddressSearchResult> {
        return try {
            val response: List<NominatimResponse> = client.get("$baseUrl/search") {
                parameter("q", query)
                parameter("format", "json")
                parameter("addressdetails", "1")
                parameter("limit", "10")
                parameter("countrycodes", "de")
            }.body()

            response.mapNotNull { result ->
                val postcode = result.address?.postcode
                result.display_name?.let {
                    AddressSearchResult(
                        displayName = it,
                        latitude = result.lat?.toDoubleOrNull() ?: 0.0,
                        longitude = result.lon?.toDoubleOrNull() ?: 0.0,
                        postalCode = postcode
                    )
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun searchAddresses(query: String): List<AddressSearchResult> {
        return try {
            val response: List<NominatimResponse> = client.get("$baseUrl/search") {
                parameter("q", query)
                parameter("format", "json")
                parameter("addressdetails", "1")
                parameter("limit", "10")
                parameter("countrycodes", "de")
            }.body()

            response.mapNotNull { result ->
                result.display_name?.let {
                    AddressSearchResult(
                        displayName = it,
                        latitude = result.lat?.toDoubleOrNull() ?: 0.0,
                        longitude = result.lon?.toDoubleOrNull() ?: 0.0
                    )
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun close() {
        client.close()
    }
}
