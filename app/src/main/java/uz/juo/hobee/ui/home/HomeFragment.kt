package uz.juo.hobee.ui.home

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.FadingCircle
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch
import okhttp3.internal.wait
import uz.juo.hobee.MainActivity
import uz.juo.hobee.R
import uz.juo.hobee.adapters.HomeBranchAdapter
import uz.juo.hobee.adapters.HomeMediacamentAdapter
import uz.juo.hobee.adapters.HomeRvBannerAdapter
import uz.juo.hobee.adapters.ViewPagerAdapter
import uz.juo.hobee.databinding.FragmentHomeBinding
import uz.juo.hobee.models.Medicament
import uz.juo.hobee.models.NeariestPharmcy
import uz.juo.hobee.retrofit.ApiClient
import uz.juo.hobee.room.AppDataBase
import uz.juo.hobee.room.entity.FavoritesEntity
import uz.juo.hobee.ui.location.MapActivity
import uz.juo.hobee.utils.*
import java.lang.Exception
import java.lang.Math.abs
import java.util.*
import kotlin.collections.ArrayList

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    var connectivty = false
    private lateinit var helper: NetworkHelper
    private lateinit var nearByBranchAdapter: HomeBranchAdapter
    lateinit var bestAdapter: HomeMediacamentAdapter
    private var param1: String? = null
    private var param2: String? = null
    var handler = Handler(Looper.getMainLooper())
    lateinit var viewPagerAdapter: ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        helper = NetworkHelper((requireContext()))
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewPagerAdapter = ViewPagerAdapter(requireActivity())
        val progressBar = binding.spinKit as ProgressBar
        val doubleBounce: Sprite = FadingCircle()
        progressBar.indeterminateDrawable = doubleBounce
        binding.card.setOnClickListener {
            findNavController().navigate(R.id.searchMedicamentFragment)
        }
        if (helper.isNetworkConnected()) {
            lifecycleScope.launch {
                getBestMed()
            }
        }
        checkInternet()
        askPermission()
        binding.sendLocationIcon.setOnClickListener {
            var i = Intent(requireContext(), MapActivity::class.java)
            startActivity(i)
        }
        return binding.root
    }

    private fun seViewPager() {
        binding.viewPager.adapter = viewPagerAdapter
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.viewPager.setPageTransformer(object : ViewPager2.PageTransformer {
                    override fun transformPage(page: View, pos: Float) {
//                            val rotation = -180f * pos
//                            page.alpha = if (rotation > 90f || rotation < -90f) 0f else 1f
//                                    page.pivotX = page.width * 0.5f
//                                page.pivotY = page.height * 0.5f
//                            page.rotationX = rotation
                        setAnim(pos, page)
                    }
                })
//                handler.removeCallbacks(slideRunnable)
//                handler.postDelayed(slideRunnable,5000)
//                if (position== 2){
//                    handler.postDelayed({
//                        binding.viewPager.setCurrentItem(0,false)
//                    },2000)
//                }
            }
        })
//
        binding.indicator.setViewPager2(binding.viewPager)
//        val DELAY_MS: Long = 2000
//        val PERIOD_MS: Long = 3000
//        val handler = Handler(Looper.getMainLooper())
//        val update = Runnable {
//            if (binding.viewPager.currentItem == 2) {
//                binding.viewPager.currentItem = 0
//            } else {
//               slideRunnable
//            }
//        }
//        Timer().schedule(object : TimerTask() {
//            override fun run() {
//                handler.post(update)
//            }
//        }, DELAY_MS, PERIOD_MS)
    }

    private fun setAnim(position: Float, view: View) {
        val MIN_SCALE = 0.85f
        val MIN_ALPHA = 0.5f
        view.apply {
            val pageWidth = width
            val pageHeight = height
            when {
                position < -1 -> { // [-Infinity,-1)
                    // This page is way off-screen to the left.
                    alpha = 0f
                }
                position <= 1 -> { // [-1,1]
                    // Modify the default slide transition to shrink the page as well
                    val scaleFactor = Math.max(MIN_SCALE, 1 - kotlin.math.abs(position).toFloat())
                    val vertMargin = pageHeight * (1 - scaleFactor) / 2
                    val horzMargin = pageWidth * (1 - scaleFactor) / 2
                    translationX = if (position < 0) {
                        horzMargin - vertMargin / 2
                    } else {
                        horzMargin + vertMargin / 2
                    }

                    // Scale the page down (between MIN_SCALE and 1)
                    scaleX = scaleFactor
                    scaleY = scaleFactor

                    // Fade the page relative to its size.
                    alpha = (MIN_ALPHA +
                            (((scaleFactor - MIN_SCALE) / (1 - MIN_SCALE)) * (1 - MIN_ALPHA)))
                }
                else -> { // (1,+Infinity]
                    // This page is way off-screen to the right.
                    alpha = 0f
                }
            }
        }
    }


    private fun hide() {
        view?.let {
            Snackbar.make(it, "No Internet connection", Snackbar.LENGTH_LONG)
                .show()
        }
        binding.spinKit.visibility = View.VISIBLE
        connectivty = false
        binding.line.visibility = View.INVISIBLE
        binding.dataConstraid.visibility = View.INVISIBLE
    }

    private fun show() {
        connectivty = true
        binding.spinKit.visibility = View.INVISIBLE
        binding.line.visibility = View.VISIBLE
        binding.dataConstraid.visibility = View.VISIBLE
    }

    private val slideRunnable: Runnable = Runnable {
        binding.viewPager.currentItem = binding.viewPager.currentItem + 1
    }

    override fun onStart() {
        super.onStart()
        checkLocation()
        seViewPager()
    }

    private fun checkLocation() {
        var l = SharedPreference.getInstance(requireContext())
        if (l.hasLocation) {
            try {
                if (l != null && l.location != null) {
                    lifecycleScope.launch {
                        getNeariestPharmacy()
                        var locationName =
                            SharedPreference.getInstance(requireContext()).location.name.toString()
                        if (locationName != "Incorrect location") {
                            binding.adress.text = locationName
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Incorrect location",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                } else {
                    askPermission()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Wrong location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun askPermission() {
        if (!SharedPreference.getInstance(requireContext()).hasLocation) {
            val dialog = Dialog(requireContext())
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.location_dialog)
            dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val dialogBtn_cancel = dialog.findViewById(R.id.deny) as TextView
            dialogBtn_cancel.setOnClickListener {
                if (helper.isNetworkConnected()) {
                    try {
                        SharedPreference.getInstance(requireContext()).hasLocation = true
                        var name = Functions().getLocationName(
                            requireContext(),
                            41.311081,
                            69.240562,
                        )

                        SharedPreference.getInstance(requireContext())
                            .setLocation("${41.311081}", "${69.240562}", name)
                        binding.adress.text = name
                        lifecycleScope.launch {
                            getNeariestPharmacy()
                        }
                        dialog.cancel()
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Wrong location", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "No Internet connection",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
            val dialogBtn_okay = dialog.findViewById(R.id.grant) as TextView
            dialogBtn_okay.setOnClickListener {
                var i = Intent(requireContext(), MapActivity::class.java)
                startActivity(i)
                dialog.cancel()
            }
            dialog.setCancelable(false)
            dialog.show()
        } else {
            try {
                lifecycleScope.launch {
                    getNeariestPharmacy()
                    var locationName =
                        SharedPreference.getInstance(requireContext()).location.name.toString()
                    if (locationName != "Incorrect location") {
                        binding.adress.text = locationName
                    } else {
                        Toast.makeText(requireContext(), "Incorrect location", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Wrong location", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private suspend fun getBestMed() {
        try {
            val list = ApiClient.apiService.getBestMedicaments() as ArrayList<Medicament>
            bestAdapter = HomeMediacamentAdapter(requireContext(), list,
                object : HomeMediacamentAdapter.itemOnCLick {
                    override fun itemClick(mediacament: Medicament, position: Int) {
                        SharedPreference.getInstance(requireContext()).medId =
                            mediacament.id.toString()
                        findNavController().navigate(R.id.infoMedicamentFragment)
                    }

                    override fun itemLikeClick(
                        mediacament: Medicament,
                        position: Int,
                        state: Boolean
                    ) {
                        if (!state) {
                            AppDataBase.getInstance(requireContext()).dao()
                                .add(
                                    FavoritesEntity(
                                        mediacament.id,
                                        mediacament.name,
                                        mediacament.manufacturer,
                                        mediacament.country,
                                        mediacament.category,
                                        mediacament.price
                                    )
                                )
                        } else {
                            AppDataBase.getInstance(requireContext()).dao()
                                .delete(mediacament.id)
                        }
                    }
                })
            binding.medicamentRv.adapter = bestAdapter
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Server Error", Toast.LENGTH_SHORT).show()
        }

    }

    private suspend fun getNeariestPharmacy() {
        try {
            val long = SharedPreference.getInstance(requireContext()).location.lat
            val lat = SharedPreference.getInstance(requireContext()).location.long
            val list =
                ApiClient.apiService.getNeariestPharmacy(lat, long) as ArrayList<NeariestPharmcy>
            nearByBranchAdapter = HomeBranchAdapter(list, object : HomeBranchAdapter.itemOnCLick {
                override fun itemClick(id: Int) {
                    val bundle = Bundle()
                    bundle.putString("param1", id.toString())
                    findNavController().navigate(R.id.infoBranchFragment, bundle)
                }
            })
            binding.pharmcyRv.adapter = nearByBranchAdapter
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "Incorrect location or internet disconnected",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    override fun onResume() {
        super.onResume()
        checkInternet()
        (activity as MainActivity).showBottomBar()
    }

    private fun checkInternet() {
        if (helper.isNetworkConnected()) {
            show()
            lifecycleScope.launch {
                getBestMed()
            }
        } else {
            hide()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}