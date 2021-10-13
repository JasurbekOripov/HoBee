package uz.juo.hobee.ui.info_medicament

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import uz.juo.hobee.MainActivity
import uz.juo.hobee.R
import uz.juo.hobee.adapters.MedInfoViewPagerAdapter
import uz.juo.hobee.databinding.FragmentInfoMedicamentBinding
import uz.juo.hobee.models.BranchForMap
import uz.juo.hobee.retrofit.ApiClient
import uz.juo.hobee.room.AppDataBase
import uz.juo.hobee.room.entity.FavoritesEntity
import uz.juo.hobee.ui.branchs_on_map.BranchsOnMapActivity
import uz.juo.hobee.utils.Functions
import uz.juo.hobee.utils.MyInterpolator
import uz.juo.hobee.utils.NetworkHelper
import uz.juo.hobee.utils.SharedPreference
import java.lang.Exception
import android.widget.TextView
import android.graphics.drawable.ColorDrawable
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.view.Window
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import com.google.android.material.bottomsheet.BottomSheetDialog
import uz.juo.hobee.models.GetById
import uz.juo.hobee.models.ItemXXX


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class InfoMedicamentFragment : Fragment() {
    lateinit var binding: FragmentInfoMedicamentBinding
    var pos = 1

    //    private var param1: Int? = null
    lateinit var viewPageraAdapter: MedInfoViewPagerAdapter

    //    private var param2: String? = null
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
//            param1 = it.getInt(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
        }
        try {
            (activity as MainActivity).hideBottomBar()
            pos = SharedPreference.getInstance(requireContext()).medId.toInt()
        }catch (e:Exception){
            e.printStackTrace()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInfoMedicamentBinding.inflate(inflater, container, false)
        binding.viewPager.isUserInputEnabled = false
        var data = GetById()
        if (NetworkHelper(requireContext()).isNetworkConnected()) {
            try {
                lifecycleScope.launch {
                    data = ApiClient.apiService.getMedicamentById(pos)
                    setData(data)
                }
            } catch (e: Exception) {
//                Toast.makeText(requireContext(), "id null", Toast.LENGTH_SHORT).show()
            }
        } else {
            view?.let {
                Snackbar.make(it, "No Internet connection", Snackbar.LENGTH_LONG)
                    .show()
            }
        }
        binding.info.setOnClickListener {
            try {
                openInfo(data)
            } catch (e: Exception) {
                view?.let {
                    Snackbar.make(it, "No Internet connection", Snackbar.LENGTH_LONG)
                        .show()
                }
            }
        }
        binding.map.setOnClickListener {
            var location = SharedPreference.getInstance(requireContext()).location
            CoroutineScope(Dispatchers.IO).launch {
                val info = ApiClient.apiService.branchPriceForMap(
                    location.lat,
                    location.long,
                    SharedPreference.getInstance(requireContext()).medId.toInt()
                )
                info.enqueue(object : Callback<BranchForMap> {
                    override fun onResponse(
                        call: Call<BranchForMap>,
                        response: Response<BranchForMap>
                    ) {

                        if (response.body()?.items != null) {
                            var count = 0
                            for (i in response.body()?.items!!) {
                                if (i.latitude == null || i.longitude == null) {
                                    count++
                                }
                            }
                            if (count != response.body()?.items!!.size) {
                                var i = Intent(requireContext(), BranchsOnMapActivity::class.java)
                                i.putExtra(
                                    "id",
                                    SharedPreference.getInstance(requireContext()).medId.toInt()
                                )
                                startActivity(i)
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "There are no pharmacies where the address is available",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "There are no pharmacies where the address is available",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }

                    override fun onFailure(call: Call<BranchForMap>, t: Throwable) {
                        Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT)
                            .show()
                    }
                })
            }
        }
        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.viewPager.currentItem = tab?.position!!
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
        binding.like.setOnClickListener {
            lifecycleScope.launch {
                try {
                    var mediacament = ApiClient.apiService.getMedicamentById(pos)
                    var likable = false
                    for (i in Functions().getFavorite(requireContext())) {
                        if (i.id == pos) {
                            likable = true
                            break
                        } else {
                            likable = false
                        }
                    }
                    if (!likable) {
                        val myAnim: Animation =
                            AnimationUtils.loadAnimation(context, R.anim.bounce_for_like)
                        val interpolator = MyInterpolator(0.2, 20.0)
                        myAnim.interpolator = interpolator
                        binding.like.startAnimation(myAnim)
                        binding.like.setImageResource(R.drawable.ic_liked)
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
                        binding.like.setImageResource(R.drawable.ic_like_icon)
                        AppDataBase.getInstance(requireContext()).dao()
                            .delete(mediacament.id)
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                }
            }
        }
        viewPageraAdapter = MedInfoViewPagerAdapter(requireActivity())
        binding.viewPager.adapter = viewPageraAdapter
        TabLayoutMediator(binding.tab, binding.viewPager) { tab, pos ->
            tab.text = loadDat()[pos]
        }.attach()
        return binding.root
    }

    private fun checkLikable() {
        lifecycleScope.launch {
            var data =
                ApiClient.apiService.getMedicamentById(SharedPreference.getInstance(requireContext()).medId.toInt())
            var likable = false
            for (i in Functions().getFavorite(requireContext())) {
                if (i.id == data.id) {
                    likable = true
                    break
                } else {
                    likable = false
                }
            }
            if (likable) {
                binding.like.setImageResource(R.drawable.ic_liked)
            } else {
                binding.like.setImageResource(R.drawable.ic_like_icon)
            }
        }
    }

    fun openInfo(data: GetById) {
        val bottomSheetDialog =
            BottomSheetDialog(requireContext(), R.style.MyTransparentBottomSheetDialogTheme)
        bottomSheetDialog.setContentView(R.layout.info_med_bottomsheet)
        val branchName = bottomSheetDialog.findViewById<TextView>(R.id.info_med_med_name)
        val branchPrice = bottomSheetDialog.findViewById<TextView>(R.id.info_med_price_from)
        val manufacturer = bottomSheetDialog.findViewById<TextView>(R.id.info_med_manufacturer)
        val description = bottomSheetDialog.findViewById<TextView>(R.id.info_med_description)
        val country = bottomSheetDialog.findViewById<TextView>(R.id.info_med_country)
        bottomSheetDialog.setCanceledOnTouchOutside(true)
        lifecycleScope.launch {
            branchName?.text = data.name
            if (data.price == null || data.price == "") {
                branchPrice?.text = "нет в наличии"
            } else {
                branchPrice?.text = (data.price)
            }
            manufacturer?.text = data.manufacturer
            description?.text = data.dosage_info
            country?.text = data.country
        }
        bottomSheetDialog.show()
    }

    private fun setData(data: GetById) {
        checkLikable()
        binding.name.text = data.name
        if (data.price == null || data.price == "") {
            binding.priceFrom.text = "нет в наличии"
        } else {
            binding.priceFrom.text = ("от ${data.price}")
        }
    }

    private fun loadDat(): ArrayList<String> {
        var list = ArrayList<String>()
        list.add("Ближе")
        list.add("Дешевле")
        return list
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).hideBottomBar()
        pos = SharedPreference.getInstance(requireContext()).medId.toInt()
    }

    override fun onDestroy() {
        super.onDestroy()
            (activity as MainActivity).showBottomBar()
//        activity?.window?.statusBarColor = Color.parseColor("#1B6DDC")
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            InfoMedicamentFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}