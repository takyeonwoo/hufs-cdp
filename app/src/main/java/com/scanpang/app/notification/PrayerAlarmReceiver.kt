package com.scanpang.app.notification

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.scanpang.app.MainActivity
import com.scanpang.app.R
import com.scanpang.app.data.AppSettingsPreferences
import java.util.Calendar

class PrayerAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != PrayerAlarmScheduler.ACTION_PRAYER_ALARM) return

        val prefs = AppSettingsPreferences(context)

        // 마스터 스위치 또는 기도 시간 알림이 꺼져 있으면 무시
        if (!prefs.isPushEnabled() || !prefs.isPrayerAlarmEnabled()) return

        // 방해 금지 모드: 22:00 ~ 07:00 차단
        if (prefs.isDoNotDisturbEnabled()) {
            val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            if (hour >= 22 || hour < 7) return
        }

        val prayerName = intent.getStringExtra(PrayerAlarmScheduler.EXTRA_PRAYER_NAME) ?: return
        val prayerTime = intent.getStringExtra(PrayerAlarmScheduler.EXTRA_PRAYER_TIME) ?: ""

        val tapIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(context, PrayerAlarmScheduler.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("기도 시간이에요 🕌")
            .setContentText("$prayerName  ·  $prayerTime")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("$prayerName 기도 시간입니다.\n현재 시각 $prayerTime — 잠시 멈추고 기도해보세요 🤲"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(tapIntent)
            .build()

        val nm = NotificationManagerCompat.from(context)
        if (nm.areNotificationsEnabled()) {
            nm.notify(prayerName.hashCode(), notification)
        }
    }
}
