package com.example.mad_assignment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.mad_assignment.ui.theme.MAD_AssignmentTheme
import com.example.week3.LatLngViewModel
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.Style
import org.ramani.compose.CameraPosition
import kotlin.getValue
import org.ramani.compose.MapLibre

class MainActivity : ComponentActivity(), LocationListener {
    val styleBuilder = Style.Builder().fromUri("https://tiles.openfreemap.org/styles/bright")
    val latLngViewModel: LatLngViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermissions()
        enableEdgeToEdge()
        setContent {
            MAD_AssignmentTheme {
                var currentLatLngState by remember { mutableStateOf(LatLng(0.0, 0.0)) }
                var zoomState by remember { mutableStateOf(14.00) }

                latLngViewModel.latLngLiveData.observe(this) { latLng ->
                    currentLatLngState = latLng
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {
                        MapLibre(
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                            styleBuilder = styleBuilder,
                            cameraPosition = CameraPosition(
                                target = currentLatLngState,
                                zoom = zoomState
                            )
                        )
                    }
                }
            }
        }
    }

    fun checkPermissions() {
        val requirePermission = Manifest.permission.ACCESS_FINE_LOCATION
        if (checkSelfPermission(requirePermission) == PackageManager.PERMISSION_GRANTED) {
            startGPS()
        } else {
            val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    startGPS()
                } else {
                    Toast.makeText(this, "GPS permissions not granted", Toast.LENGTH_LONG).show()
                }
            }
            permissionLauncher.launch(requirePermission)
        }

    }
    @SuppressLint("MissingPermission")
    fun startGPS() {
        val mgr = getSystemService(LOCATION_SERVICE) as LocationManager
        mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 5f, this )
    }

    override fun onLocationChanged(location: Location) {
        latLngViewModel.latLng = LatLng(location.latitude, location.longitude)
    }

}


