package com.scanpang.app.data

data class StoreResponse(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val address: String = "",
    val phone: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val open_hours: String? = null,
    val homepage: String? = null,
    val image_urls: List<String> = emptyList(),
    val details: Map<String, Any?> = emptyMap(),
    val source: String? = null,
    val is_open_now: Boolean? = null,
)
