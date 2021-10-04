package uz.juo.hobee.ui.branchs_on_map

import android.annotation.SuppressLint
import android.content.ContentValues
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import uz.juo.hobee.R
import com.yandex.mapkit.Animation
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.runtime.ui_view.ViewProvider
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import uz.juo.hobee.models.ItemMedIdPrice
import uz.juo.hobee.utils.NetworkHelper
import uz.juo.hobee.utils.SharedPreference
import uz.juo.hobee.viewmodel.branches_for_map.BranchesForMapViewModel
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList


class BranchsOnMapActivity : AppCompatActivity() {
    private lateinit var helper: NetworkHelper
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var mapView: MapView? = null
    lateinit var userLocationLayer: UserLocationLayer
    lateinit var viewModel: BranchesForMapViewModel
    var lat = 59.945933
    var long = 30.320045


    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        helper = NetworkHelper(this)
        setContentView(R.layout.activity_branchs_on_map)
        MapKitFactory.initialize(this)
        val mapkit = MapKitFactory.getInstance()
        mapView = findViewById<View>(R.id.branchs_map) as MapView
        userLocationLayer = mapkit.createUserLocationLayer(mapView?.mapWindow!!)
        userLocationLayer.isHeadingEnabled = true;
//        getData()
        setTv("5600")
    }

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    private fun setTv(text: String) {
        val textView = TextView(this)
        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        textView.background = getDrawable(R.drawable.toolbar_back)
        textView.layoutParams = params
        textView.setPadding(10, 5, 10, 5)
        textView.setTextColor(Color.WHITE)
        textView.text = "Branch Price"
        val viewProvider = ViewProvider(textView)
        var location = SharedPreference.getInstance(this).location
        val viewPlacemark: PlacemarkMapObject =
            mapView!!.map.mapObjects.addPlacemark(
                Point(
                    location.long.toDouble(),
                    location.lat.toDouble()
                ), viewProvider
            )
        cameraMoveOn(
            location.long.toDouble(),
            location.lat.toDouble()
        )
        viewProvider.snapshot()
        viewPlacemark.setView(viewProvider)
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun checkInternet() {
        Log.d(ContentValues.TAG, "checkInternet: worked ")
        if (helper.isNetworkConnected()) {
//            getData()
        } else {
            Toast.makeText(this, "No Internet connection", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        checkInternet()
    }

    private fun getData() {
        lifecycleScope.launch {
            var sh = SharedPreference.getInstance(this@BranchsOnMapActivity)
            viewModel =
                ViewModelProvider(this@BranchsOnMapActivity)[BranchesForMapViewModel::class.java]
//            viewModel.branches(sh.lang.toInt(), sh.location.lat, sh.location.long)
//                .observe(this@BranchsOnMapActivity, {
//                })


        }
    }

    fun cameraMoveOn(lat: Double, long: Double) {
        try {
            mapView!!.map.move(
                CameraPosition(
                    Point(lat, long), 10.0f, 0.0f, 0.0f
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