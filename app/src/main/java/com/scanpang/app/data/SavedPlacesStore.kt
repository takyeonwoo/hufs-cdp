package com.scanpang.app.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class SavedPlaceEntry(
    val id: String,
    val name: String,
    val category: String,
    val distanceLine: String,
    val tags: List<String>,
    val categoryKey: String,
    val savedOrder: Long = System.currentTimeMillis(),
)

class SavedPlacesStore(context: Context) {

    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getAll(): List<SavedPlaceEntry> {
        val raw = prefs.getString(KEY_PLACES, null) ?: return emptyList()
        return try {
            val arr = JSONArray(raw)
            buildList {
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    add(
                        SavedPlaceEntry(
                            id = o.getString("id"),
                            name = o.getString("name"),
                            category = o.getString("category"),
                            distanceLine = o.optString("distanceLine", o.optString("distance", "")),
                            tags = o.optJSONArray("tags")?.toStringList().orEmpty(),
                            categoryKey = parseCategoryKey(o),
                            savedOrder = o.optLong("savedOrder", 0L),
                        ),
                    )
                }
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun isSaved(id: String): Boolean = getAll().any { it.id == id }

    fun save(entry: SavedPlaceEntry) {
        val list = getAll().toMutableList()
        list.removeAll { it.id == entry.id }
        list.add(0, entry.copy(savedOrder = System.currentTimeMillis()))
        saveList(list)
    }

    fun remove(id: String) {
        val list = getAll().toMutableList()
        if (list.removeAll { it.id == id }) saveList(list)
    }

    private fun saveList(list: List<SavedPlaceEntry>) {
        val arr = JSONArray()
        list.forEach { e ->
            arr.put(
                JSONObject().apply {
                    put("id", e.id)
                    put("name", e.name)
                    put("category", e.category)
                    put("distanceLine", e.distanceLine)
                    put("tags", JSONArray(e.tags))
                    put("categoryKey", e.categoryKey)
                    put("savedOrder", e.savedOrder)
                },
            )
        }
        prefs.edit().putString(KEY_PLACES, arr.toString()).apply()
    }

    private fun JSONArray.toStringList(): List<String> = buildList {
        for (i in 0 until length()) add(getString(i))
    }

    companion object {
        private const val PREFS_NAME = "scanpang_saved_places"
        private const val KEY_PLACES = "places_json"
    }
}

/** Read new categoryKey field; fall back to mapping old SavedPlaceNavTarget enum name for backward compat. */
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
