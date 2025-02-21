package fr.jg.sosalert.data

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class LocationRepositoryImpl(val context: Context) : LocationRepository {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    override fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    override suspend fun getLocation(): Location? {
        return if (hasLocationPermission()) {
            try {
                suspendCancellableCoroutine { continuation ->
                    fusedLocationClient.getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        null
                    ).addOnSuccessListener { location ->
                        continuation.resume(location)
                    }.addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
                }
            } catch (e: Exception) {
                Log.e("LocationRepository", "Failed to get location: ${e.message}")
                null
            }
        } else {
            null
        }
    }
}