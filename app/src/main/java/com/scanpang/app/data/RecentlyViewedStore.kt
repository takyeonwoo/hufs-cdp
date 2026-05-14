package com.scanpang.app.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class RecentlyViewedEntry(
    val id: String,
    val name: String,
    val category: String,
    val distanceLine: String,
    val categoryKey: String,
    val viewedAt: Long = System.currentTimeMillis(),
)

class RecentlyViewedStore(context: Context) {

    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getAll(): List<RecentlyViewedEntry> {
        val raw = prefs.getString(KEY_ITEMS, null) ?: return emptyList()
        return try {
            val arr = JSONArray(raw)
            buildList {
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    add(
                        RecentlyViewedEntry(
                            id = o.getString("id"),
                            name = o.getString("name"),
                            category = o.optString("category", ""),
                            distanceLine = o.optString("distanceLine", ""),
                            categoryKey = parseCategoryKey(o),
                            viewedAt = o.optLong("viewedAt", 0L),
                        ),
                    )
                }
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun record(entry: RecentlyViewedEntry) {
        val list = getAll().toMutableList()
        list.removeAll { it.id == entry.id }
        list.add(0, entry.copy(viewedAt = System.currentTimeMillis()))
        while (list.size > MAX_ITEMS) list.removeAt(list.lastIndex)
        saveList(list)
    }

    fun remove(id: String) {
        val list = getAll().toMutableList()
        if (list.removeAll { it.id == id }) saveList(list)
    }

    fun clearAll() {
        prefs.edit().remove(KEY_ITEMS).apply()
    }

    private fun saveList(list: List<RecentlyViewedEntry>) {
        val arr = JSONArray()
        list.forEach { e ->
            arr.put(
                JSONObject().apply {
                    put("id", e.id)
                    put("name", e.name)
                    put("category", e.category)
                    put("distanceLine", e.distanceLine)
                    put("categoryKey", e.categoryKey)
                    put("viewedAt", e.viewedAt)
                },
            )
        }
        prefs.edit().putString(KEY_ITEMS, arr.toString()).apply()
    }

    companion object {
        const val MAX_ITEMS = 20
        private const val PREFS_NAME = "scanpang_recently_viewed"
        private const val KEY_ITEMS = "items_json"
    }
}

private fun parseCategoryKey(o: JSONObject): String {
    val direct = o.optString("categoryKey", "")
    if (direct.isNotBlank()) return direct
    return when (o.optString("target", "")) {
        "Restaurant" -> "restaurant"
        "PrayerRoom" -> "prayer_room"
        "TouristSpot" -> "tourist"
        "Shopping" -> "shopping"
        "ConvenienceStore" -> "convenience_store"
        "Cafe" -> "cafe"
        "Atm" -> "atm"
        "Bank" -> "bank"
        "Exchange" -> "exchange"
        "Subway" -> "subway"
        "Restroom" -> "restroom"
        "Lockers" -> "locker"
        "Hospital" -> "hospital"
        "Pharmacy" -> "pharmacy"
        else -> "restaurant"
    }
}
