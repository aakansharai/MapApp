package com.allindia.mapsassignment

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.GoogleMap
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.libraries.places.api.net.FetchPlaceRequest

import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.widget.AutocompleteActivity

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var placesClient: PlacesClient
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
//        AIzaSyClBymYB1baWpauebx4pG0r1SuLzsVKvjA

        // Initialize the Places API
        Places.initialize(applicationContext, "AIzaSyClBymYB1baWpauebx4pG0r1SuLzsVKvjA")
        placesClient = Places.createClient(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ContextCompat.checkSelfPermission(this@MainActivity, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            getUserLocation()
        }


        val startLocationEditText = findViewById<AutoCompleteTextView>(R.id.startLocation)
        val endLocationEditText = findViewById<EditText>(R.id.endLocation)
        val submitButton = findViewById<TextView>(R.id.submitButton)

        startLocationEditText.setOnItemClickListener { _, _, position, _ ->
            val item = startLocationEditText.adapter.getItem(position) as AutocompletePrediction
            val placeId = item.placeId

            // Fetch place details and move the map camera
            val placeFields = listOf(Place.Field.ID, Place.Field.LAT_LNG)
            val request = FetchPlaceRequest.builder(placeId, placeFields).build()

            placesClient.fetchPlace(request).addOnSuccessListener { response ->
                val place = response.place
                val latLng = place.latLng

                if (latLng != null) {
                    mMap.addMarker(MarkerOptions().position(latLng).title("Start Location"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                }
            }
        }

        startLocationEditText.setOnClickListener {
            val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .build(this)
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }

        submitButton.setOnClickListener {
            val startLocation = startLocationEditText.text.toString()
            val endLocation = endLocationEditText.text.toString()

            Log.e("LOCATIONSS", "$startLocation $endLocation")

            if (startLocation.isNotBlank() && endLocation.isNotBlank()) {
                // Handle the input (e.g., start a new activity, save to database, etc.)
                var intent = Intent(this, ShowList::class.java)
                intent.putExtra("startLocation", startLocation)
                intent.putExtra("endLocation", endLocation)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please enter both locations", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    private fun getUserLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    val currentLatLng = LatLng(it.latitude, it.longitude)
                    mMap.addMarker(MarkerOptions().position(currentLatLng).title("Current Location"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                val latLng = place.latLng
                if (latLng != null) {
                    mMap.addMarker(MarkerOptions().position(latLng).title("Start Location"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                    val startLocationEditText = findViewById<AutoCompleteTextView>(R.id.startLocation)
                    startLocationEditText.setText(place.name)
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // Handle the error.
                val status = Autocomplete.getStatusFromIntent(data!!)
                Toast.makeText(this, status.statusMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getUserLocation()
            }
        }
    }


    companion object {
        private const val AUTOCOMPLETE_REQUEST_CODE = 1
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}