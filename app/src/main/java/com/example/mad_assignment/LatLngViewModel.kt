package com.example.week3

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mad_assignment.Poi
import com.example.mad_assignment.PoiDatabase
import com.example.mad_assignment.WebPoi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.maplibre.android.geometry.LatLng
import com.github.kittinunf.fuel.gson.responseObject
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.github.kittinunf.fuel.httpPost

class LatLngViewModel(app: Application) : AndroidViewModel(app) {

    private val db = PoiDatabase.getDatabase(app)

    val pois = MutableLiveData<List<Poi>>(emptyList())

    var latLng = LatLng(50.9079, -1.4015)
        set(newValue) {
            field = newValue
            latLngLiveData.value = newValue
        }
    var latLngLiveData = MutableLiveData<LatLng>(LatLng(50.9079, -1.4015))

    fun searchPoisByType(type: String) {
        viewModelScope.launch {
            val results = withContext(Dispatchers.IO) {
                db.poiDao().searchByType(type)
            }
            pois.value = results
        }
    }

    fun loadPois() {
        viewModelScope.launch {
            val loadedPois = withContext(Dispatchers.IO) {
                db.poiDao().getAllPois()
            }
            pois.value = loadedPois
        }
    }

    fun addPoi(poi: Poi) {
        val postData = listOf(
            "name" to poi.name,
            "type" to poi.type,
            "description" to poi.description,
            "lat" to poi.latitude,
            "lon" to poi.longitude
        )

        "http://10.0.2.2:3000/poi/create".httpPost(postData).response { request, response, result ->
            when (result) {
                is Result.Success -> {
                    val returnedId = result.get().decodeToString().toLong()

                    viewModelScope.launch {
                        withContext(Dispatchers.IO) {
                            val poiWithId = poi.copy(webId = returnedId)
                            db.poiDao().insert(poiWithId)
                        }
                        pois.value = (pois.value ?: emptyList()) + poi.copy(webId = returnedId)
                    }
                }

                is Result.Failure -> {
                    result.error.printStackTrace()
                }
            }
        }
    }

    fun loadPoisFromWeb() {
        "http://10.0.2.2:3000/poi/all".httpGet().responseObject<List<WebPoi>> { request, response, result ->
            when (result) {
                is Result.Success -> {
                    val webPois = result.get()

                    viewModelScope.launch {
                        withContext(Dispatchers.IO) {
                            webPois.forEach { webPoi ->
                                val existing = db.poiDao().getByWebId(webPoi.id)

                                if (existing == null) {
                                    val poi = Poi(
                                        webId = webPoi.id,
                                        name = webPoi.name,
                                        type = webPoi.type,
                                        description = webPoi.description,
                                        latitude = webPoi.lat,
                                        longitude = webPoi.lon
                                    )
                                    db.poiDao().insert(poi)
                                }
                            }
                            pois.postValue(db.poiDao().getAllPois())
                        }
                    }
                }

                is Result.Failure -> {
                    result.error.printStackTrace()
                }
            }
        }
    }
}






