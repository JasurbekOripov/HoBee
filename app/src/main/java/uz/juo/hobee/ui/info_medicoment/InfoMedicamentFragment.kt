package uz.juo.hobee.ui.info_medicoment

import android.content.ContentValues
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
import kotlinx.coroutines.launch
import uz.juo.hobee.MainActivity
import uz.juo.hobee.R
import uz.juo.hobee.adapters.MedInfoViewPagerAdapter
import uz.juo.hobee.databinding.FragmentInfoMedicamentBinding
import uz.juo.hobee.retrofit.ApiClient
import uz.juo.hobee.room.AppDataBase
import uz.juo.hobee.room.entity.FavoritesEntity
import uz.juo.hobee.utils.Functions
import uz.juo.hobee.utils.MyInterpolator
import uz.juo.hobee.utils.NetworkHelper
import uz.juo.hobee.utils.SharedPreference
import java.lang.Exception

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class InfoMedicamentFragment : Fragment() {
    lateinit var binding: FragmentInfoMedicamentBinding
    var pos = 1

    //    private var param1: Int? = null
    lateinit var viewPageraAdapter: MedInfoViewPagerAdapter
//    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

//            param1 = it.getInt(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
        }
        (activity as MainActivity).hideBottomBar()
        pos = SharedPreference.getInstance(requireContext()).lang.toInt()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInfoMedicamentBinding.inflate(inflater, container, false)
        binding.viewPager.isUserInputEnabled = false
        setData()
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
                        val myAnim: Animation = AnimationUtils.loadAnimation(context, R.anim.bounce)
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
        return binding.root
    }

    private fun checkLikable() {
        lifecycleScope.launch {
            var data =
                ApiClient.apiService.getMedicamentById(SharedPreference.getInstance(requireContext()).lang.toInt())
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

    private fun setData() {
        if (NetworkHelper(requireContext()).isNetworkConnected()) {
            try {
                binding.linear.visibility = View.VISIBLE
                lifecycleScope.launch {
                    binding.name.text = ApiClient.apiService.getMedicamentById(
                        SharedPreference.getInstance(requireContext()).lang.toInt()
                    ).name
                    var data = ApiClient.apiService.getMedicamentById(pos)
                    Log.d(ContentValues.TAG, "loadData1212121212: $data")
                    checkLikable()
                    binding.name.text = data.name
                    binding.country.text = data.country
                    binding.description.text = data.dosage_info
                    binding.manufacturer.text = data.manufacturer
//                    binding.map.setOnClickListener {  }
                    if (data.price == null || data.price == "") {
                        binding.priceFrom.text = "нет в наличии"
                    } else {
                        binding.priceFrom.text = ("от ${data.price}")
                    }
                }

                TabLayoutMediator(binding.tab, binding.viewPager) { tab, pos ->
                    tab.text = loadDat()[pos]
                }.attach()
            } catch (e: Exception) {
//                Toast.makeText(requireContext(), "id null", Toast.LENGTH_SHORT).show()
            }
        } else {
            binding.linear.visibility = View.INVISIBLE
            view?.let {
                Snackbar.make(it, "No Internet connection", Snackbar.LENGTH_LONG)
                    .show()
            }
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
        pos = SharedPreference.getInstance(requireContext()).lang.toInt()
        setData()
//        (activity as MainActivity).hideBottomBar()
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as MainActivity).showBottomBar()
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