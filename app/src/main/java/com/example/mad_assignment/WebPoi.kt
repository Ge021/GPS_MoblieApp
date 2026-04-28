package com.example.mad_assignment

data class WebPoi(
    val id: Long,
    val name: String,
    val type: String,
    val country: String,
    val region: String,
    val lon: Double,
    val lat: Double,
    val description: String,
    val recommendations: String
)

