package com.scanpang.app.qibla

import java.util.Calendar

data class PrayerScheduleEntry(
    val name: String,
    val displayTime: String,
    val timeMillis: Long,
)

data class PrayerTimes(
    val nextPrayerName: String,
    val nextPrayerTime: String,
    val remainingLabel: String,
    val todaySchedule: List<PrayerScheduleEntry> = emptyList(),
)

/**
 * 기도 시간. API 연동 시 이 함수 본문만 교체하면 됩니다.
 * todaySchedule 에 오늘 5개 기도 시간(epoch ms)을 채워주면
 * PrayerAlarmScheduler 가 자동으로 AlarmManager 에 등록합니다.
 */
fun getPrayerTimes(): PrayerTimes {
    val schedule = buildTodaySchedule(
        "Fajr"    to "05:12",
        "Dhuhr"   to "12:15",
        "Asr"     to "15:45",
        "Maghrib" to "18:32",
        "Isha"    to "20:05",
    )
    val now = System.currentTimeMillis()
    val next = schedule.firstOrNull { it.timeMillis > now } ?: schedule.last()
    val remaining = if (next.timeMillis > now) {
        val diffMs = next.timeMillis - now
        val hours = diffMs / 3_600_000L
        val minutes = (diffMs % 3_600_000L) / 60_000L
        if (hours > 0L) "${hours}시간 ${minutes}분 남음" else "${minutes}분 남음"
    } else {
        "오늘 기도 완료"
    }
    return PrayerTimes(
        nextPrayerName = next.name,
        nextPrayerTime = next.displayTime,
        remainingLabel = remaining,
        todaySchedule  = schedule,
    )
}

private fun buildTodaySchedule(vararg pairs: Pair<String, String>): List<PrayerScheduleEntry> =
    pairs.map { (name, hhmm) ->
        val (h, m) = hhmm.split(":").map { it.toInt() }
        val ms = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, h)
            set(Calendar.MINUTE, m)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        PrayerScheduleEntry(name = name, displayTime = hhmm, timeMillis = ms)
    }

/** 북(0°) 기준 시계방향 키블라 방위각(도). 추후 위·경도 기반 API로 교체. */
fun getQiblaDirection(): Float = 292f

/** 메카까지 거리(km). 추후 API로 교체. */
fun getMeccaDistanceKm(): Float = 8565f
