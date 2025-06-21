package com.example.projectmanagerapp.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale

suspend fun getAddressFromCoordinates(
    context: Context,
    latitude: Double,
    longitude: Double
): Address? {
    val geocoder = Geocoder(context, Locale("vi"))

    return withContext(Dispatchers.IO) {
        try {
            // Android T (API 33) trở lên yêu cầu một listener
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                var address: Address? = null
                geocoder.getFromLocation(latitude, longitude, 1) { addresses ->
                    address = addresses.firstOrNull()
                }
                kotlinx.coroutines.delay(500)
                address
            } else {
                @Suppress("DEPRECATION")
                geocoder.getFromLocation(latitude, longitude, 1)?.firstOrNull()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            null
        }
    }
}