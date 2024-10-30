package ca.unb.mobiledev.showmetheroom

import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat.getCurrentLocation
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.GroundOverlayOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.TileProvider
import com.google.android.gms.maps.model.UrlTileProvider
import java.net.URL

class MainActivity : AppCompatActivity(), OnMapReadyCallback{
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_main)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (checkLocationPermission()){
            getCurrentLocation()
        }
        else{
            requestLocationPermission()
        }
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        if (checkLocationPermissions()) {
            getCurrentLocation()
        } else {
            requestLocationPermission()
        }
    }

    private fun checkLocationPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun checkLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }


    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission()
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    // Access the latitude and longitude
                    val latitude = location.latitude
                    val longitude = location.longitude
                    Toast.makeText(this, "Lat: $latitude, Lon: $longitude", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Unable to find location", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to get location", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            } else {
                // Handle permission denied
            }
        }


    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Set a custom location (e.g., a university campus or building location)
        val location = LatLng(45.964497, -66.641194)  // Example coordinates

        // Add a marker at the location
        mMap.addMarker(MarkerOptions().position(location).title("Custom Building"))

        // Move the camera to the location and set a zoom level
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17f))

        // Add a GroundOverlay (blueprint or custom map)
        addOverlay(location)
    }

    private fun addOverlay(location: LatLng) {
        // Load the overlay image from the drawable folder
        val overlayImage = BitmapDescriptorFactory.fromResource(R.drawable.blueprint_image)

        // Create a GroundOverlayOptions object
        val groundOverlayOptions = GroundOverlayOptions()
            .image(overlayImage)
            .position(location, 100f)  // Specify width and height (in meters)
            .transparency(0.4f)        // Set transparency (0.0 is fully opaque, 1.0 is fully transparent)

        // Add the overlay to the map
        mMap.addGroundOverlay(groundOverlayOptions)
    }
}
}