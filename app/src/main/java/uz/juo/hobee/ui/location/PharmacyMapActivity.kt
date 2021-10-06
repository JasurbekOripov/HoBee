package uz.juo.hobee.ui.location

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.location.Location
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.text.TextPaint
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.runtime.image.ImageProvider
import uz.juo.hobee.R
import uz.juo.hobee.utils.SharedPreference
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.runtime.ui_view.ViewProvider
import uz.juo.hobee.models.Item
import uz.juo.hobee.models.ItemMedIdPrice
import java.lang.Exception


class PharmacyMapActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var mapView: MapView? = null
    lateinit var userLocationLayer: UserLocationLayer
    var data = ItemMedIdPrice()

    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.initialize(this)
        val mapkit = MapKitFactory.getInstance()
        setContentView(R.layout.activity_pharmacy_map)
        mapView = findViewById<View>(R.id.mapPharmacy) as MapView
        userLocationLayer = mapkit.createUserLocationLayer(mapView?.mapWindow!!)
        userLocationLayer.isHeadingEnabled = true

        data = intent.getSerializableExtra("lat") as ItemMedIdPrice
        val getCurrent_btn = findViewById<ImageView>(R.id.getCurrentLocationPharmacy)
        val textView = TextView(this)
        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        textView.background = getDrawable(R.drawable.price_tv_back)
        textView.layoutParams = params
        textView.text = data.name
//        if (data.name.length > 20) {
//            textView.ellipsize = TextUtils.TruncateAt.END
//        }
        textView.setPadding(15, 7, 15, 7)
        textView.setTextColor(Color.WHITE)
        textView.setTypeface(null, Typeface.BOLD);
        val viewProvider = ViewProvider(textView)
        if (data.latitude != null && data.longitude != null) {
            val viewPlacemark: PlacemarkMapObject =
                mapView!!.map.mapObjects.addPlacemark(
                    Point(
                        data.latitude.toString().toDouble(),
                        data.longitude.toString().toDouble()
                    ), viewProvider
                )
            viewProvider.snapshot()
            viewPlacemark.setView(viewProvider)
            getCurrent_btn.setOnClickListener {
                cameraMoveOn(data.latitude as Double, data.longitude as Double)
            }
            cameraMoveOn(data.latitude as Double, data.longitude as Double)
        } else {
            Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cameraMoveOn(lat: Double, long: Double) {
        try {
            mapView!!.map.move(
                CameraPosition(
                    Point(lat, long), 15f, 0.0f, 0.0f
                ),
                Animation(Animation.Type.SMOOTH, 2F), null
            )
        } catch (e: Exception) {
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
}