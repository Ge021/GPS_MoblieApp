package com.example.mad_assignment
import org.maplibre.android.geometry.LatLng

data class Poi(
    val name: String,
    val type: String,
    val description: String,
    val latLng: LatLng
)

