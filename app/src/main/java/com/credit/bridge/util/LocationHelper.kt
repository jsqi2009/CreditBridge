package com.credit.bridge.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import java.util.concurrent.atomic.AtomicBoolean

object LocationHelper {

    private const val TAG = "LocationHelper"
    private const val MAX_AGE_MS = 5 * 60 * 1000L
    private const val FETCH_TIMEOUT_MS = 8000L

    @Volatile
    var cachedLocation: Location? = null
        private set

    fun fetchLocation(context: Context, onResult: (Location?) -> Unit) {
        val appContext = context.applicationContext
        logEnvironment(appContext)

        if (!hasLocationPermission(appContext)) {
            Log.w(TAG, "fetchLocation: location permission missing")
            onResult(null)
            return
        }

        if (!isLocationEnabled(appContext)) {
            Log.w(TAG, "fetchLocation: system location disabled")
            onResult(null)
            return
        }

        val lastKnown = readLastKnown(appContext)
        if (lastKnown != null && isFresh(lastKnown)) {
            cachedLocation = lastKnown
            Log.i(
                TAG,
                "fetchLocation: use fresh lastKnown lat=${lastKnown.latitude}, lng=${lastKnown.longitude}, " +
                    "provider=${lastKnown.provider}, ageMs=${locationAgeMs(lastKnown)}"
            )
            onResult(lastKnown)
            return
        }

        Log.i(
            TAG,
            "fetchLocation: request fused location, lastKnown=${formatLocation(lastKnown)}"
        )

        val delivered = AtomicBoolean(false)
        val cancellation = CancellationTokenSource()
        val client = LocationServices.getFusedLocationProviderClient(appContext)

        fun deliverOnce(location: Location?, source: String) {
            if (!delivered.compareAndSet(false, true)) return
            if (location != null) {
                cachedLocation = location
                Log.i(
                    TAG,
                    "fetchLocation: got location from $source lat=${location.latitude}, lng=${location.longitude}, " +
                        "provider=${location.provider}, accuracy=${location.accuracy}"
                )
            } else {
                Log.w(TAG, "fetchLocation: no location from $source")
            }
            onResult(location)
        }

        client.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, cancellation.token)
            .addOnSuccessListener { location ->
                deliverOnce(location ?: lastKnown ?: readLastKnown(appContext), "fused")
            }
            .addOnFailureListener { error ->
                Log.w(TAG, "fetchLocation: fused failed", error)
                deliverOnce(lastKnown ?: readLastKnown(appContext), "fused-fallback")
            }

        Handler(Looper.getMainLooper()).postDelayed({
            if (!delivered.compareAndSet(false, true)) return@postDelayed
            cancellation.cancel()
            val fallback = lastKnown ?: readLastKnown(appContext)
            if (fallback != null) {
                cachedLocation = fallback
                Log.w(
                    TAG,
                    "fetchLocation: timeout after ${FETCH_TIMEOUT_MS}ms, use fallback " +
                        formatLocation(fallback)
                )
            } else {
                Log.w(TAG, "fetchLocation: timeout after ${FETCH_TIMEOUT_MS}ms, no location")
            }
            onResult(fallback)
        }, FETCH_TIMEOUT_MS)
    }

    fun hasLocationPermission(context: Context): Boolean {
        val coarse = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val fine = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return coarse && fine
    }

    fun isLocationEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    @SuppressLint("MissingPermission")
    fun readLastKnown(context: Context): Location? {
        if (!hasLocationPermission(context)) {
            Log.w(TAG, "readLastKnown: permission missing")
            return null
        }
        return try {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val providers = locationManager.getProviders(true)
            Log.d(TAG, "readLastKnown: providers=$providers")
            var bestLocation: Location? = null
            for (provider in providers) {
                val lastKnownLocation = try {
                    locationManager.getLastKnownLocation(provider)
                } catch (e: SecurityException) {
                    Log.w(TAG, "readLastKnown: SecurityException provider=$provider", e)
                    null
                }
                Log.d(TAG, "readLastKnown: provider=$provider location=${formatLocation(lastKnownLocation)}")
                if (lastKnownLocation == null) continue
                if (bestLocation == null || lastKnownLocation.accuracy < bestLocation.accuracy) {
                    bestLocation = lastKnownLocation
                }
            }
            bestLocation
        } catch (e: Exception) {
            Log.w(TAG, "readLastKnown: failed", e)
            null
        }
    }

    private fun logEnvironment(context: Context) {
        Log.d(
            TAG,
            "environment: permission=${hasLocationPermission(context)}, " +
                "locationEnabled=${isLocationEnabled(context)}, " +
                "cached=${formatLocation(cachedLocation)}"
        )
    }

    private fun isFresh(location: Location): Boolean {
        return locationAgeMs(location) <= MAX_AGE_MS
    }

    private fun locationAgeMs(location: Location): Long {
        return (System.currentTimeMillis() - location.time).coerceAtLeast(0L)
    }

    private fun formatLocation(location: Location?): String {
        return if (location == null) {
            "null"
        } else {
            "lat=${location.latitude}, lng=${location.longitude}, provider=${location.provider}"
        }
    }
}
