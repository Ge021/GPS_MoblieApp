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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mad_assignment.ui.theme.MAD_AssignmentTheme
import com.example.week3.LatLngViewModel
import kotlinx.coroutines.launch
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.Style
import org.ramani.compose.CameraPosition
import org.ramani.compose.MapLibre
import org.ramani.compose.Symbol

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity(), LocationListener {
    val styleBuilder = Style.Builder().fromUri("https://tiles.openfreemap.org/styles/bright")
    val latLngViewModel: LatLngViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermissions()
        enableEdgeToEdge()
        setContent {
            MAD_AssignmentTheme {
                val navController = rememberNavController()
                var currentLatLngState by remember { mutableStateOf(latLngViewModel.latLng) }
                var poisState by remember { mutableStateOf<List<Poi>>(emptyList()) }
                var selectedPoi by remember { mutableStateOf<Poi?>(null) }
                var searchType by remember { mutableStateOf("") }
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val coroutineScope = rememberCoroutineScope()

                latLngViewModel.latLngLiveData.observe(this) { latLng ->
                    currentLatLngState = latLng
                }
                latLngViewModel.pois.observe(this) { list ->
                    poisState = list
                }

                selectedPoi?.let { poi ->
                    AlertDialog(
                        onDismissRequest = { selectedPoi = null },
                        title = { Text(poi.name) },
                        text = {
                            Column {
                                Text("Type: ${poi.type}")
                                Text("Description: ${poi.description}")
                            }
                        },
                        confirmButton = {
                            Button(onClick = { selectedPoi = null }) {
                                Text("Close")
                            }
                        }
                    )
                }

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet {
                            NavigationDrawerItem(
                                selected = false,
                                label = { Text("Map") },
                                icon = { Icon(Icons.Filled.Home, "Map") },
                                onClick = {
                                    coroutineScope.launch { drawerState.close() }
                                    navController.navigate("map")
                                }
                            )
                            NavigationDrawerItem(
                                selected = false,
                                label = { Text("Add Point of Interest") },
                                icon = { Icon(Icons.Filled.Add, "Add POI") },
                                onClick = {
                                    coroutineScope.launch { drawerState.close() }
                                    navController.navigate("addPoi")
                                }
                            )
                            NavigationDrawerItem(
                                selected = false,
                                label = { Text("Download POIs from Web") },
                                icon = { Icon(Icons.Filled.Menu, "Download") },
                                onClick = {
                                    coroutineScope.launch { drawerState.close() }
                                    latLngViewModel.loadPoisFromWeb()
                                }
                            )
                        }
                    }
                ) {
                    NavHost(navController = navController, startDestination = "map") {

                        composable("map") {
                            Scaffold(
                                modifier = Modifier.fillMaxSize(),
                                topBar = {
                                    TopAppBar(
                                        colors = TopAppBarDefaults.topAppBarColors(
                                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                                            titleContentColor = MaterialTheme.colorScheme.primary
                                        ),
                                        title = { Text("Points of Interest") },
                                        actions = {
                                            IconButton(onClick = {
                                                coroutineScope.launch {
                                                    if (drawerState.isClosed) {
                                                        drawerState.open()
                                                    } else {
                                                        drawerState.close()
                                                    }
                                                }
                                            }) {
                                                Icon(Icons.Filled.Menu, contentDescription = "Menu")
                                            }
                                        }
                                    )
                                },
                                bottomBar = {
                                    NavigationBar {
                                        NavigationBarItem(
                                            icon = { Icon(Icons.Filled.Home, "Map") },
                                            label = { Text("Map") },
                                            onClick = { navController.navigate("map") },
                                            selected = true
                                        )
                                        NavigationBarItem(
                                            icon = { Icon(Icons.Filled.Add, "Add POI") },
                                            label = { Text("Add POI") },
                                            onClick = { navController.navigate("addPoi") },
                                            selected = false
                                        )
                                    }
                                },
                                floatingActionButton = {
                                    FloatingActionButton(
                                        onClick = { navController.navigate("addPoi") }
                                    ) {
                                        Icon(Icons.Filled.Add, contentDescription = "Add POI")
                                    }
                                }
                            ) { innerPadding ->
                                Column(modifier = Modifier.padding(innerPadding)) {
                                    Button(
                                        onClick = { latLngViewModel.loadPois() },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Load Points of Interest")
                                    }
                                    OutlinedTextField(
                                        value = searchType,
                                        onValueChange = { searchType = it },
                                        label = { Text("Search by type") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Button(
                                        onClick = { latLngViewModel.searchPoisByType(searchType) },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Search")
                                    }
                                    MapLibre(
                                        modifier = Modifier.weight(1f).fillMaxWidth(),
                                        styleBuilder = styleBuilder,
                                        cameraPosition = CameraPosition(
                                            target = currentLatLngState,
                                            zoom = 14.0
                                        )
                                    ) {
                                        poisState.forEach { poi ->
                                            Symbol(
                                                center = LatLng(poi.latitude, poi.longitude),
                                                size = 1f,
                                                onClick = { selectedPoi = poi }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        composable("addPoi") {
                            AddPoiScreen(
                                currentLatLng = currentLatLngState,
                                onPoiAdded = { poi ->
                                    latLngViewModel.addPoi(poi)
                                },
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
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
        mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 5f, this)
    }

    override fun onLocationChanged(location: Location) {
        latLngViewModel.latLng = LatLng(location.latitude, location.longitude)
    }
}