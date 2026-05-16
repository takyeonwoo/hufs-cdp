package com.scanpang.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.scanpang.app.data.AppSettingsPreferences
import com.scanpang.app.navigation.ScanPangApp
import com.scanpang.app.notification.PrayerAlarmScheduler
import com.scanpang.app.qibla.getPrayerTimes
import com.scanpang.app.ui.theme.ScanPangTheme

class MainActivity : ComponentActivity() {

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) scheduleAlarmsIfEnabled()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        PrayerAlarmScheduler.createNotificationChannel(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                scheduleAlarmsIfEnabled()
            }
        } else {
            scheduleAlarmsIfEnabled()
        }

        setContent {
            ScanPangTheme {
                ScanPangApp(modifier = Modifier.fillMaxSize())
            }
        }
    }

    private fun scheduleAlarmsIfEnabled() {
        val prefs = AppSettingsPreferences(this)
        if (prefs.isPushEnabled() && prefs.isPrayerAlarmEnabled()) {
            PrayerAlarmScheduler.schedule(this, getPrayerTimes().todaySchedule)
        }
    }
}
