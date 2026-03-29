package com.example.week3

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.maplibre.android.geometry.LatLng

class LatLngViewModel: ViewModel() {
    var latLng = LatLng(50.9079, -1.4015)
        set(newValue) {
            field = newValue
            latLngLiveData.value = newValue
        }
    var latLngLiveData = MutableLiveData<LatLng>()

}