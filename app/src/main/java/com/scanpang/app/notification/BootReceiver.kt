package com.scanpang.app.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.scanpang.app.data.AppSettingsPreferences
import com.scanpang.app.qibla.getPrayerTimes

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val prefs = AppSettingsPreferences(context)
        if (!prefs.isPushEnabled() || !prefs.isPrayerAlarmEnabled()) return

        // 재부팅 후 오늘 기도 시간 알람 재등록
        PrayerAlarmScheduler.schedule(context, getPrayerTimes().todaySchedule)
    }
}
