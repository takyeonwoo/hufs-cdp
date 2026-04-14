package com.scanpang.app.qibla

/**
 * 기도 시간. 추후 API 연동 시 이 함수 본문만 교체하면 됩니다.
 */
data class PrayerTimes(
    val nextPrayerName: String,
    val nextPrayerTime: String,
    val remainingLabel: String,
)

fun getPrayerTimes(): PrayerTimes =
    PrayerTimes(
        nextPrayerName = "Dhuhr",
        nextPrayerTime = "12:15",
        remainingLabel = "2시간 34분 남음",
    )

/**
 * 북(0°) 기준 시계방향 키블라 방위각(도). 추후 위·경도 기반 API로 교체.
 */
fun getQiblaDirection(): Float = 292f

/**
 * 메카까지 거리(km). 추후 API로 교체.
 */
fun getMeccaDistanceKm(): Float = 8565f
