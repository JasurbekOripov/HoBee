package uz.juo.hobee.ui.location

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.runtime.image.ImageProvider
import uz.juo.hobee.R
import uz.juo.hobee.utils.SharedPreference
import com.yandex.mapkit.map.PlacemarkMapObject


class PharmacyMapActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var mapView: MapView? = null
    lateinit var userLocationLayer: UserLocationLayer
    var lat = 59.945933
    var long = 30.320045
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.initialize(this)
        val mapkit = MapKitFactory.getInstance()
        setContentView(R.layout.activity_pharmacy_map)
        mapView = findViewById<View>(R.id.mapPharmacy) as MapView
        userLocationLayer = mapkit.createUserLocationLayer(mapView?.mapWindow!!)
        userLocationLayer.isHeadingEnabled = true;
        var intent = getIntent()
        lat = intent.getDoubleExtra("lat", 0.0)
        long = intent.getDoubleExtra("long", 0.0)
        val getCurrent_btn = findViewById<ImageView>(R.id.getCurrentLocationPharmacy)
        getCurrent_btn.setOnClickListener {
            showBranch()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val mark: PlacemarkMapObject = mapView!!.map.mapObjects.addPlacemark(Point(lat, long))
            mark.opacity = 0.5f
//            mark.setIcon(ImageProvider.fromResource(this, R.drawable.ic_pin))
            mark.isDraggable = true
        }
        showBranch()
    }

    private fun showBranch() {
        mapView!!.map.move(
            CameraPosition(
                Point(lat, long), 14.0f,
                0.0f, 0.0f
            ), Animation(Animation.Type.SMOOTH, 5F),
            null
        )
    }

    private fun showUserLocation() {
        showLocationPrompt()
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this@PharmacyMapActivity)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null && location.latitude > 0 && (location.longitude) > 0) {
                cameraMoveOn(location.latitude, location.longitude)
            } else {
                Log.d(ContentValues.TAG, "getUserLocation121212:  null or min then 0")
                setDefoultLocation()
            }
        }
    }

    private fun setDefoultLocation() {
        Log.d(ContentValues.TAG, "getUserLocation121212:  default location get")
        SharedPreference.getInstance(this).setLocation("${41.311081}", "${69.240562}")
        SharedPreference.getInstance(this).hasLocation = (true)
        cameraMoveOn(41.311081, 69.240562)
    }

    fun cameraMoveOn(lat: Double, long: Double) {
        mapView!!.map.move(
            CameraPosition(
                Point(lat, long), 4.0f, 0.0f, 0.0f
            ),
            Animation(Animation.Type.SMOOTH, 2F), null
        )
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
                // All location settings are satisfied. The client can initialize location
                // requests here.
                response
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
                        Log.d(ContentValues.TAG, "showLocationPrompt: ")
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
//                    showUserLocation()
                } else {
                    Toast.makeText(this, "No GPS Connection", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}