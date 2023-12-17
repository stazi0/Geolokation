package com.example.googlemaps

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.constraintlayout.motion.widget.Debug.getLocation
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat.isLocationEnabled
import com.google.android.gms.common.api.GoogleApi
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.provider.Settings
import android.util.Log
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import java.util.Locale

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    lateinit var mMap: GoogleMap
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    var latitude =  0.000000000000001
    var longitude = 0.000000000000001
    private val permissionId = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        //Log.d("getLocation_forGetLoc", "latitude: ${latitude}")
        //Log.d("getLocation_forGetLoc", "longitude: ${longitude}")
        getLocation()
        //Log.d("getLocation_afterGetLoc", "latitude: ${latitude}")
        //Log.d("getLocation_afterGetLoc", "longitude: ${longitude}")

        var mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap){
        mMap = googleMap
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation( ) {
        //Log.d("getLocation", "start: ${latitude}")
        if (checkPermissions()) {
            //Log.d("getLocation", "checkPermissions: ${latitude}")
            if (isLocationEnabled()) {
                //Log.d("getLocation", "isLocationEnabled: ${latitude}")
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    //Log.d("getLocation", "mFusedLocationClient start: ${latitude}")
                    if (location != null) {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val list: List<Address>? =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        latitude = list?.get(0)!!.latitude
                        longitude = list?.get(0)!!.longitude
                        //Log.d("getLocation", "for addMarker lat: ${latitude}")
                        //Log.d("getLocation", "for addMarker long: ${longitude}")
                        addMarkerGeoLocation()
                        //Log.d("getLocation", "aft addMark lat: ${latitude}")
                        //Log.d("getLocation", "aft addMark long: ${longitude}")
                    }
                }
            } else {
                //Log.d("getLocation", "isLocationEnabled else: ${latitude}")
                Toast.makeText(this, "Увімкніть геолокацію", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            //Log.d("getLocation", "checkPermissions else: ${latitude}")
            requestPermissions() }
    }

    private fun addMarkerGeoLocation(){
        //Log.d("getLocation", "in addMarker lat: ${latitude}")
        //Log.d("getLocation", "in addMarker long: ${longitude}")
        var point = LatLng(latitude, longitude)
        mMap.addMarker(MarkerOptions().position(point).title("Я тут"))
        mMap.isBuildingsEnabled = true
        mMap.isIndoorEnabled = true
        mMap.moveCamera(CameraUpdateFactory.newLatLng(point))
    }


    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
            )
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }

    private fun checkPermissions(): Boolean {
           if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                return true
            }
            return false
    }

}