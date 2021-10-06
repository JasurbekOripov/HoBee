package uz.juo.hobee.ui.branchs_on_map

import android.annotation.SuppressLint
import android.content.ContentValues
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
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
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.awaitResponse
import uz.juo.hobee.models.BranchForMap
import uz.juo.hobee.models.BranchesByMedIdPrice
import uz.juo.hobee.models.ItemMedIdPrice
import uz.juo.hobee.models.ItemXXX
import uz.juo.hobee.retrofit.ApiClient
import uz.juo.hobee.utils.NetworkHelper
import uz.juo.hobee.utils.SharedPreference
import uz.juo.hobee.viewmodel.branches_for_map.BranchesForMapViewModel
import java.lang.Exception
import kotlin.collections.ArrayList


@RequiresApi(Build.VERSION_CODES.M)
class BranchsOnMapActivity : AppCompatActivity() {
    private lateinit var helper: NetworkHelper
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var mapView: MapView? = null
    lateinit var userLocationLayer: UserLocationLayer
    var list = ArrayList<ItemXXX>()
    lateinit var viewModel: BranchesForMapViewModel


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
        var location = SharedPreference.getInstance(this).location
        var id = intent.getIntExtra("id", 1)
        cameraMoveOn(
            location.long.toDouble(),
            location.lat.toDouble()
        )
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val info = ApiClient.apiService.branchPriceForMap(location.lat, location.long, id)
                info.enqueue(object : Callback<BranchForMap> {
                    override fun onResponse(
                        call: Call<BranchForMap>,
                        response: Response<BranchForMap>
                    ) {
                        if (response.body()?.items != null) {
                            list = response.body()?.items as ArrayList<ItemXXX>
                            for (i in list) {
                                setTv(i)
                            }
                        } else {
                            Toast.makeText(
                                this@BranchsOnMapActivity,
                                "Data not Found",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }

                    override fun onFailure(call: Call<BranchForMap>, t: Throwable) {
                        Toast.makeText(this@BranchsOnMapActivity, "Error", Toast.LENGTH_SHORT)
                            .show()
                    }
                })
            }
        } catch (e: Exception) {
            list = ArrayList()
        }
    }

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    private fun setTv(data: ItemXXX) {
        if (data.latitude != null && data.longitude != null) {
            val textView = TextView(this)
            val params = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            textView.background = getDrawable(R.drawable.price_tv_back)
            textView.layoutParams = params
            textView.id = data.branch_id
            textView.isClickable = true
            textView.setPadding(15, 7, 15, 7)
            textView.setTextColor(Color.WHITE)
            textView.setTypeface(null, Typeface.BOLD);
            textView.text = "${data.price.subSequence(0, data.price.indexOf("."))} сўм"
            val viewProvider = ViewProvider(textView)

            val viewPlacemark: PlacemarkMapObject =
                mapView!!.map.mapObjects.addPlacemark(
                    Point(
                        data.latitude.toString().toDouble(),
                        data.longitude.toString().toDouble()
                    ), viewProvider
                )
            viewProvider.snapshot()
            viewPlacemark.setView(viewProvider)
            textView.setOnClickListener {
                Toast.makeText(this, " text view clicked${textView.id}", Toast.LENGTH_SHORT).show()
            }

            View.OnClickListener { v ->
                Toast.makeText(
                    v.context, "View listener " + v.tag,
                    Toast.LENGTH_SHORT
                ).show()
            }
            viewPlacemark.addTapListener { p0, p1 ->

                Toast.makeText(this, "yandex listener ${ p1.longitude.toString() }", Toast.LENGTH_SHORT).show()
                true
            }
        } else {
            Toast.makeText(this, "No Location at ${data.branch_id}", Toast.LENGTH_SHORT).show()
        }
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