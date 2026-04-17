package com.termintracker.service

import com.termintracker.model.Address
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class OpenStreetMapService {
    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }
    private val baseUrl = "https://nominatim.openstreetmap.org"

    @Serializable
    data class NominatimResponse(
        val place_id: Long? = null,
        val licence: String? = null,
        val osm_type: String? = null,
        val osm_id: Long? = null,
        val lat: String? = null,
        val lon: String? = null,
        val display_name: String? = null,
        val address: AddressResponse? = null,
        val boundingbox: List<String>? = null
    )

    @Serializable
    data class AddressResponse(
        val house_number: String? = null,
        val road: String? = null,
        val suburb: String? = null,
        val city: String? = null,
        val town: String? = null,
        val municipality: String? = null,
        val county: String? = null,
        val state: String? = null,
        val postcode: String? = null,
        val country: String? = null,
        val country_code: String? = null
    )

    suspend fun searchAddress(query: String, limit: Int = 5): List<AddressResult> {
        val encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString())
        val url = "${baseUrl}/search?format=json&q=${encodedQuery}&limit=${limit}&addressdetails=1"

        val request = Request.Builder()
            .url(url)
            .header("User-Agent", "TerminTracker/1.0")
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    if (!body.isNullOrEmpty()) {
                        val results = json.decodeFromString<List<NominatimResponse>>(body)
                        results.map { mapToAddressResult(it) }
                    } else {
                        emptyList()
                    }
                } else {
                    emptyList()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun geocodeAddress(
        street: String,
        houseNumber: String,
        city: String,
        postalCode: String
    ): List<AddressResult> {
        val query = buildString {
            if (street.isNotBlank()) {
                append(street)
                if (houseNumber.isNotBlank()) append(" $houseNumber")
            }
            if (postalCode.isNotBlank()) {
                if (isNotEmpty()) append(", ")
                append(postalCode)
            }
            if (city.isNotBlank()) {
                if (isNotEmpty()) append(" ")
                append(city)
            }
            append(", Deutschland")
        }
        return searchAddress(query, 3)
    }

    suspend fun reverseGeocode(latitude: Double, longitude: Double): AddressResult? {
        val url = "${baseUrl}/reverse?format=json&lat=${latitude}&lon=${longitude}&addressdetails=1"

        val request = Request.Builder()
            .url(url)
            .header("User-Agent", "TerminTracker/1.0")
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    if (!body.isNullOrEmpty()) {
                        val result = json.decodeFromString<NominatimResponse>(body)
                        mapToAddressResult(result)
                    } else {
                        null
                    }
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun mapToAddressResult(response: NominatimResponse): AddressResult {
        val addr = response.address
        return AddressResult(
            address = Address(
                street = addr?.road ?: "",
                houseNumber = addr?.house_number ?: "",
                postalCode = addr?.postcode ?: "",
                city = addr?.city ?: addr?.town ?: addr?.municipality ?: "",
                district = addr?.suburb ?: addr?.county ?: "",
                country = addr?.country ?: "Deutschland",
                latitude = response.lat?.toDoubleOrNull(),
                longitude = response.lon?.toDoubleOrNull()
            ),
            displayName = response.display_name ?: "",
            placeId = response.place_id ?: 0
        )
    }

    data class AddressResult(
        val address: Address,
        val displayName: String,
        val placeId: Long
    )
}
