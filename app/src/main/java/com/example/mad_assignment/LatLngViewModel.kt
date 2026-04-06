package com.example.week3

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mad_assignment.Poi
import org.maplibre.android.geometry.LatLng

class LatLngViewModel : ViewModel() {
    var latLng = LatLng(50.9079, -1.4015)
        set(newValue) {
            field = newValue
            latLngLiveData.value = newValue
        }
    var latLngLiveData = MutableLiveData<LatLng>()

    val pois = MutableLiveData<List<Poi>>(emptyList())

    fun addPoi(poi: Poi) {
        pois.value = (pois.value ?: emptyList()) + poi
    }
}





