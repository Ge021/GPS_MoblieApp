package com.example.mad_assignment

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PoiDao {

    @Query("SELECT * FROM pois")
    suspend fun getAllPois(): List<Poi>

    @Query("SELECT * FROM pois WHERE type LIKE :searchType")
    suspend fun searchByType(searchType: String): List<Poi>



    @Insert
    fun insert(poi: Poi): Long

    @Delete
    fun delete(poi: Poi): Int
}

