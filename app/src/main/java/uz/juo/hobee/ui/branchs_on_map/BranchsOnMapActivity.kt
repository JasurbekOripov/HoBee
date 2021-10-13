package uz.juo.hobee.ui.branchs_on_map

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.yandex.runtime.ui_view.ViewProvider
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.internal.ClusterizedPlacemarkCollectionBinding
import com.yandex.runtime.image.ImageProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.awaitResponse
import uz.juo.hobee.adapters.ManufacturerAdapter
import uz.juo.hobee.models.*
import uz.juo.hobee.retrofit.ApiClient
import uz.juo.hobee.utils.Functions
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
//    lateinit var collection: ClusterizedPlacemarkCollection
    lateinit var userLocationLayer: UserLocationLayer
    var list = ArrayList<ItemXXX>()
    lateinit var viewModel: BranchesForMapViewModel
    lateinit var viewPlacemark: PlacemarkMapObject

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        helper = NetworkHelper(this)
        setContentView(R.layout.activity_branchs_on_map)
        MapKitFactory.initialize(this)
        val mapkit = MapKitFactory.getInstance()
        mapView = findViewById<View>(R.id.branchs_map) as MapView
        userLocationLayer = mapkit.createUserLocationLayer(mapView?.mapWindow!!)
        userLocationLayer.isHeadingEnabled = true

    }

    private fun loadData() {
        try {
            var location = SharedPreference.getInstance(this).location
            var id = intent.getIntExtra("id", 1)
            CoroutineScope(Dispatchers.IO).launch {
                val info = ApiClient.apiService.branchPriceForMap(location.lat, location.long, id)
                info.enqueue(object : Callback<BranchForMap> {
                    override fun onResponse(
                        call: Call<BranchForMap>,
                        response: Response<BranchForMap>
                    ) {
                        if (response.body()?.items != null) {
                            cameraMoveOn(
                                location.long.toDouble(),
                                location.lat.toDouble()
                            )
                            list = response.body()?.items as ArrayList<ItemXXX>

//                            collection = mapView!!.map.mapObjects.addClusterizedPlacemarkCollection(this@BranchsOnMapActivity)
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
            textView.textSize = 12F
            textView.setPadding(25, 12, 25, 12)
            textView.setTextColor(Color.WHITE)
            textView.setTypeface(null, Typeface.BOLD);
            textView.text = "${data.price.subSequence(0, data.price.indexOf("."))} сўм"
            val viewProvider = ViewProvider(textView)
            viewPlacemark = mapView?.map?.mapObjects?.addPlacemark(
                Point(
                    data.latitude.toString().toDouble(),
                    data.longitude.toString().toDouble()
                ), viewProvider
            )!!
            viewPlacemark.userData = data
            viewProvider.snapshot()
            viewPlacemark.setView(viewProvider)
//            collection.clusterPlacemarks(30.0, 10)
            viewPlacemark.addTapListener(object : MapObjectTapListener {
                override fun onMapObjectTap(p0: MapObject, p1: Point): Boolean {
                    try {
                        setBottomSheet(p0.userData as ItemXXX)
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@BranchsOnMapActivity,
                            "not enough data at this branch",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    return true
                }
            })

        } else {
//            Toast.makeText(this, "No Location at ${data.branch_name}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setBottomSheet(id: ItemXXX) {
        val bottomSheetDialog = BottomSheetDialog(this, R.style.MyTransparentBottomSheetDialogTheme)
        bottomSheetDialog.setContentView(R.layout.branch_info_bottom_sheet)
        val branchName = bottomSheetDialog.findViewById<TextView>(R.id.bsh_branch_name)
        val branchPrice = bottomSheetDialog.findViewById<TextView>(R.id.bsh_price)
        val branchlocation = bottomSheetDialog.findViewById<TextView>(R.id.bsh_location)
        val branchworkingTime = bottomSheetDialog.findViewById<TextView>(R.id.bsh_working_time)
        val branchDistanse = bottomSheetDialog.findViewById<TextView>(R.id.bsh_distance)
        val branchMap = bottomSheetDialog.findViewById<CardView>(R.id.bsh_location_btn)
        val branchCall = bottomSheetDialog.findViewById<CardView>(R.id.bsh_call_btn)
        bottomSheetDialog.setCanceledOnTouchOutside(true)
        lifecycleScope.launch {
            var branch = ApiClient.apiService.getPharmacyById(id.branch_id)
            branchCall?.setOnClickListener {
                if (branch.phone != "") {
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse("tel:" + branch.phone)
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        this@BranchsOnMapActivity,
                        "Phone Number not found",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                bottomSheetDialog.cancel()
            }
            branchName?.text = branch.name
            branchlocation?.text = branch.address
            if (id.price == null || id.price == "") {
                branchPrice?.text = "нет в наличии"
            } else {
                branchPrice?.text = (id.price)
            }
            branchworkingTime?.text = Functions().getWorkingTime(
                branch.start_time.toString(),
                branch.end_time.toString()
            )
            branchMap?.setOnClickListener {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("geo:<" + id.latitude.toString() + ">,<" + id.longitude.toString() + ">?q=<" + id.latitude.toString() + ">,<" + id.longitude.toString() + ">(" + id.branch_name.toString() + ")")
                )
                startActivity(intent)
                bottomSheetDialog.cancel()
            }
        }
        bottomSheetDialog.show()
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
        loadData()
    }

//    override fun onClusterAdded(p0: Cluster) {
//        p0.appearance.setIcon(ImageProvider.fromResource(this, R.drawable.blue_location))
//    }


}