package com.example.week3

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mad_assignment.Poi
import com.example.mad_assignment.PoiDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.maplibre.android.geometry.LatLng

class LatLngViewModel(app: Application) : AndroidViewModel(app) {

    private val db = PoiDatabase.getDatabase(app)

    val pois: LiveData<List<Poi>> = db.poiDao().getAllPois()

    var latLng = LatLng(50.9079, -1.4015)
        set(newValue) {
            field = newValue
            latLngLiveData.value = newValue
        }
    var latLngLiveData = MutableLiveData<LatLng>(LatLng(50.9079, -1.4015))

    fun addPoi(poi: Poi) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                db.poiDao().insert(poi)
            }
        }
    }
}






