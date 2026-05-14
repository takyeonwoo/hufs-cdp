package com.scanpang.app.util

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

/**
 * 현재 기기 위치를 구독하는 Composable.
 * ACCESS_FINE_LOCATION 권한이 없으면 null을 유지한다.
 * 15초 간격으로 업데이트하며, 화면 진입 시 lastLocation도 즉시 반영한다.
 */
@Composable
fun rememberUserLocation(): State<Location?> {
    val context = LocalContext.current
    val state = remember { mutableStateOf<Location?>(null) }

    DisposableEffect(context) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) return@DisposableEffect onDispose {}

        val client = LocationServices.getFusedLocationProviderClient(context)

        val request = LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 15_000L)
            .setMinUpdateIntervalMillis(10_000L)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                state.value = result.lastLocation
            }
        }

        try {
            client.lastLocation.addOnSuccessListener { loc -> if (loc != null) state.value = loc }
            client.requestLocationUpdates(request, callback, Looper.getMainLooper())
        } catch (_: SecurityException) { }

        onDispose { client.removeLocationUpdates(callback) }
    }

    return state
}

/**
 * 두 좌표 사이 직선 거리를 "Xm" 또는 "X.Xkm" 형식 문자열로 반환한다.
 */
fun distanceText(userLat: Double, userLng: Double, targetLat: Double, targetLng: Double): String {
    val results = FloatArray(1)
    Location.distanceBetween(userLat, userLng, targetLat, targetLng, results)
    val meters = results[0].toInt()
    return if (meters < 1000) "${meters}m" else "${"%.1f".format(meters / 1000.0)}km"
}
