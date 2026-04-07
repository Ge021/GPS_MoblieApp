package com.example.mad_assignment

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PoiDao {

    @Query("SELECT * FROM pois")
    fun getAllPois(): LiveData<List<Poi>>

    @Insert
    fun insert(poi: Poi): Long

    @Delete
    fun delete(poi: Poi): Int
}

