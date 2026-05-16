package com.scanpang.app.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.scanpang.app.qibla.PrayerScheduleEntry

object PrayerAlarmScheduler {

    const val CHANNEL_ID = "prayer_times"
    const val ACTION_PRAYER_ALARM = "com.scanpang.app.PRAYER_ALARM"
    const val EXTRA_PRAYER_NAME = "prayer_name"
    const val EXTRA_PRAYER_TIME = "prayer_time"

    fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "기도 시간 알림",
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = "이슬람 기도 시간을 알려드립니다"
            enableVibration(true)
        }
        context.getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)
    }

    fun schedule(context: Context, entries: List<PrayerScheduleEntry>) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val now = System.currentTimeMillis()
        entries.forEach { entry ->
            if (entry.timeMillis <= now) return@forEach
            val pi = buildPendingIntent(context, entry.name, entry.displayTime)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, entry.timeMillis, pi)
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, entry.timeMillis, pi)
            }
        }
    }

    fun cancelAll(context: Context) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        listOf("Fajr", "Dhuhr", "Asr", "Maghrib", "Isha").forEach { name ->
            val pi = PendingIntent.getBroadcast(
                context,
                name.hashCode(),
                Intent(context, PrayerAlarmReceiver::class.java).apply {
                    action = ACTION_PRAYER_ALARM
                },
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE,
            ) ?: return@forEach
            alarmManager.cancel(pi)
        }
    }

    private fun buildPendingIntent(
        context: Context,
        prayerName: String,
        displayTime: String,
    ): PendingIntent = PendingIntent.getBroadcast(
        context,
        prayerName.hashCode(),
        Intent(context, PrayerAlarmReceiver::class.java).apply {
            action = ACTION_PRAYER_ALARM
            putExtra(EXTRA_PRAYER_NAME, prayerName)
            putExtra(EXTRA_PRAYER_TIME, displayTime)
        },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )
}
