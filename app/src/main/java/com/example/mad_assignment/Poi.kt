package com.example.mad_assignment

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pois")
data class Poi(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val webId: Long? = null,
    val name: String,
    val type: String,
    val description: String,
    val latitude: Double,
    val longitude: Double
)
