package uz.juo.hobee.ui.home

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.FadingCircle
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import uz.juo.hobee.MainActivity
import uz.juo.hobee.R
import uz.juo.hobee.adapters.HomeBranchAdapter
import uz.juo.hobee.adapters.HomeMediacamentAdapter
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
import java.util.*
import kotlin.collections.ArrayList

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    var connectivty = false
    private lateinit var helper: NetworkHelper
    var handler = Handler(Looper.getMainLooper())
    var locationRequest = false
    private lateinit var nearByBranchAdapter: HomeBranchAdapter
    lateinit var bestAdapter: HomeMediacamentAdapter
    private var param1: String? = null
    private var param2: String? = null
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
        setViewPagerData()
        val progressBar = binding.spinKit as ProgressBar
        val doubleBounce: Sprite = FadingCircle()
        progressBar.indeterminateDrawable = doubleBounce
        binding.card.setOnClickListener {
            findNavController().navigate(R.id.searchMedicamentFragment)
        }
        checkInternet()
        binding.sendLocationIcon.setOnClickListener {
            var i = Intent(requireContext(), MapActivity::class.java)
            startActivity(i)
        }
        return binding.root
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

    private fun checkLocation() {
        if (!SharedPreference.getInstance(requireContext()).hasLocation) {
            val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            builder.setTitle("Location Permission")
                .setMessage("Please for using this app grant the permission and mark location")
                .setPositiveButton("Ok") { dialog, which ->
                    dialog.cancel()
//            if (Functions().checkPermission(requireContext())) {
                var i = Intent(requireContext(), MapActivity::class.java)
                startActivity(i)
//            } else {

//            }
                    dialog.cancel()
                }
                .setNegativeButton("No") { a, i ->
                    a.cancel()
                    SharedPreference.getInstance(requireContext()).hasLocation=true
                    SharedPreference.getInstance(requireContext())
                        .setLocation("${41.311081}", "${69.240562}")
                    try {
                        var name = Functions().getLocationName(
                            requireContext(),
                            41.311081,
                            69.240562,
                        )
                        binding.adress.text = name
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Wrong location", Toast.LENGTH_SHORT)
                            .show()
                    }

                }
                .setCancelable(false)
            builder.show()

        } else {
            try {
                lifecycleScope.launch {
                    getNeariestPharmacy()
                    var l = SharedPreference.getInstance(requireContext()).location
                    var name = Functions().getLocationName(
                        requireContext(),
                        l.long.toDouble(),
                        l.lat.toDouble()
                    )
                    binding.adress.text = name
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
                        SharedPreference.getInstance(requireContext()).lang =
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
        val long = SharedPreference.getInstance(requireContext()).location.lat
        val lat = SharedPreference.getInstance(requireContext()).location.long
        try {
            val list = ApiClient.apiService.getNeariestPharmacy(lat, long) as ArrayList<NeariestPharmcy>

            nearByBranchAdapter = HomeBranchAdapter(list, object : HomeBranchAdapter.itemOnCLick {
                override fun itemClick(id: Int) {
                    val bundle = Bundle()
                    bundle.putString("param1", id.toString())
                    findNavController().navigate(R.id.infoBranchFragment, bundle)
                }
            })
            binding.pharmcyRv.adapter = nearByBranchAdapter
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Get nearest pharmacy error", Toast.LENGTH_SHORT)
                .show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setViewPagerData() {
        val wormDotsIndicator = binding.springDotsIndicator
        val viewPager = binding.viewPager
        var viewPagerAdapter = ViewPagerAdapter(requireActivity())
        binding.viewPager.adapter = viewPagerAdapter
        handler = Handler(Looper.getMainLooper())
        val update = Runnable {
            if (viewPager.currentItem == 2) {
                viewPager.currentItem = 0
            } else {
                viewPager.currentItem++
            }
        }
        Timer().schedule(object : TimerTask() {
            override fun run() {
                handler.post(update)
            }
        }, 2500, 2200)
        binding.viewPager.setPageTransformer(ZoomOutPageTransformer())
        wormDotsIndicator.setViewPager2(binding.viewPager)
    }

    override fun onResume() {
        super.onResume()
        checkInternet()
    }

    private fun checkInternet() {
        if (helper.isNetworkConnected()) {
            lifecycleScope.launch {
                getBestMed()
                checkLocation()
            }
            show()
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