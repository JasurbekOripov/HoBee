package uz.juo.hobee.ui.location

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.IntentSender
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.Animation
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.yandex.mapkit.user_location.UserLocationLayer
import uz.juo.hobee.utils.Functions
import uz.juo.hobee.utils.NetworkHelper
import java.util.*
import com.yandex.mapkit.location.LocationStatus

import com.yandex.mapkit.location.LocationListener
import uz.juo.hobee.MainActivity
import uz.juo.hobee.R
import uz.juo.hobee.utils.SharedPreference


class MapActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var mapView: MapView? = null
    lateinit var userLocationLayer: UserLocationLayer
    var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {

        MapKitFactory.initialize(this)
        var mapkit = MapKitFactory.getInstance()
        setContentView(R.layout.activity_map)
        super.onCreate(savedInstanceState)
        mapView = findViewById<View>(R.id.map_userLocation) as MapView
        userLocationLayer = mapkit.createUserLocationLayer(mapView?.mapWindow!!)
        userLocationLayer.isHeadingEnabled = true;
        var location = SharedPreference.getInstance(this).location
        cameraMoveOn(location.long.toDouble(), location.lat.toDouble())
        val save_btn = findViewById<TextView>(R.id.save)
        val getCurrent_btn = findViewById<ImageView>(R.id.getCurrentLocation)
        save_btn.setOnClickListener {
            if (NetworkHelper(this).isNetworkConnected()) {
                SharedPreference.getInstance(this).hasLocation = true
                var a = mapView!!.mapWindow.map.cameraPosition.target
                try {
                    var name = Functions().getLocationName(
                        this,
                        a.latitude,
                        a.longitude
                    )
                    SharedPreference.getInstance(this)
                        .setLocation(
                            "${a.latitude}", "${a.longitude}", name
                        )
                } catch (e: java.lang.Exception) {
                    Toast.makeText(this, "Location Error", Toast.LENGTH_SHORT).show()
                }
                this.finish()
            } else {
                Toast.makeText(
                    this,
                    "You can not change location.Please check internet connection !",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        getCurrent_btn.setOnClickListener {
            getUserLocation()
        }
//        mapkit.createLocationManager().requestSingleUpdate(object : LocationListener {
//            override fun onLocationUpdated(p0: com.yandex.mapkit.location.Location) {
//                mapView!!.map.move(
//                    CameraPosition(p0.position, 20.0f, 0.0f, 0.0f),
//                    Animation(Animation.Type.SMOOTH, 1.0F), null
//                )
//            }
//
//            override fun onLocationStatusUpdated(locationStatus: LocationStatus) {
//                Log.d("TAG", "getUserLocation121212:status update ")
//            }
//        })
    }

    private fun getUserLocation() {
        showLocationPrompt()
        try {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@MapActivity)
            if (Functions().checkPermission(this)) {
                count = 0
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    Log.d(TAG, "getUserLocation121212:  $location")
                    try {
                        if (location != null && location.latitude > 0 && (location.longitude) > 0) {
                            var name = Functions().getLocationName(
                                this,
                                location.latitude,
                                location.longitude
                            )
                            SharedPreference.getInstance(this)
                                .setLocation(
                                    "${location.latitude}", "${location.longitude}", name
                                )
                            SharedPreference.getInstance(this).hasLocation = true
                            cameraMoveOn(location.latitude, location.longitude)
                        } else {
                            Log.d(TAG, "getUserLocatio:  null or min then 0")
                            setDefoultLocation()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                    }
                }

            } else {
                Log.d(TAG, "getUserLocation121212:  permisson denied")
                if (count == 2) {
                    Toast.makeText(
                        this,
                        "Please grant permission Default location",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }
                count++
                setDefoultLocation()
            }
        } catch (e: Exception) {
            Log.d(TAG, "getUserLocation: ${e.message}")
        }
    }

    private fun setDefoultLocation() {
        Log.d(TAG, "getUserLocation121212:  default location get")
        var lat = 41.311081
        var long = 69.240562
        var name = Functions().getLocationName(
            this,
            lat,
            long
        )
        SharedPreference.getInstance(this).setLocation("$lat", "$long", name)
        SharedPreference.getInstance(this).hasLocation = (true)
        cameraMoveOn(41.311081, 69.240562)
    }

    fun cameraMoveOn(lat: Double, long: Double) {
        try {
            mapView!!.map.move(
                CameraPosition(
                    Point(lat, long), 15.0f, 0.0f, 0.0f
                ),
                Animation(Animation.Type.SMOOTH, 2F), null
            )
        } catch (e: java.lang.Exception) {
            Toast.makeText(this, "Internet Error", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onStop() {
        mapView!!.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView!!.onStart()
    }

    private fun showLocationPrompt() {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val result: Task<LocationSettingsResponse> =
            LocationServices.getSettingsClient(this).checkLocationSettings(builder.build())

        result.addOnCompleteListener { task ->
            try {
                val response = task.getResult(ApiException::class.java)
            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        try {
                            val resolvable: ResolvableApiException =
                                exception as ResolvableApiException
                            resolvable.startResolutionForResult(
                                this, LocationRequest.PRIORITY_HIGH_ACCURACY
                            )
                        } catch (e: IntentSender.SendIntentException) {
                            // Ignore the error.
                        } catch (e: ClassCastException) {
                            // Ignore, should be an impossible error.
                        }
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        Log.d(TAG, "showLocationPrompt: ")
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            LocationRequest.PRIORITY_HIGH_ACCURACY -> {
                if (resultCode == Activity.RESULT_OK) {
                    getUserLocation()
                } else {
                    Toast.makeText(this, "No GPS Connection", Toast.LENGTH_SHORT).show()
                    setDefoultLocation()
                }
            }
        }
    }
}