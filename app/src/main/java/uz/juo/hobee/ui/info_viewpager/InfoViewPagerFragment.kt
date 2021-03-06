package uz.juo.hobee.ui.info_viewpager

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.FadingCircle
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import uz.juo.hobee.MainActivity
import uz.juo.hobee.adapters.BranchesByIdAdapter
import uz.juo.hobee.databinding.FragmentInfoViewPagerBinding
import uz.juo.hobee.models.ItemMedIdPrice
import uz.juo.hobee.ui.location.PharmacyMapActivity
import uz.juo.hobee.utils.*
import uz.juo.hobee.viewmodel.branch_by_id_map.BranchesByIdViewModel
import java.lang.Exception

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "position"

class InfoViewPagerFragment : Fragment() {
    lateinit var binding: FragmentInfoViewPagerBinding

    //    private var param1: Int? = null
    private var position: String? = "0"
    var pos = 1
    lateinit var branchbyMap: BranchesByIdViewModel
    lateinit var adapter: BranchesByIdAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pos = SharedPreference.getInstance(requireContext()).medId.toInt()
        arguments?.let {
//            param1 = it.getInt(ARG_PARAM1)
            position = it.getString(ARG_PARAM2)
        }
//        (activity as MainActivity).hideBottomBar()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentInfoViewPagerBinding.inflate(inflater, container, false)
        val progressBar = binding.loading as ProgressBar
        val doubleBounce: Sprite = FadingCircle()
        loadData()
        progressBar.indeterminateDrawable = doubleBounce
        binding.refresh.setOnRefreshListener {
            loadData()
            binding.refresh.isRefreshing = false
        }
        return binding.root
    }

    private fun loadData() {
        if (NetworkHelper(requireContext()).isNetworkConnected()) {
            hideLoading()
            val location = SharedPreference.getInstance(activity?.applicationContext).location
            branchbyMap = ViewModelProvider(this)[BranchesByIdViewModel::class.java]
            adapter = BranchesByIdAdapter(object : BranchesByIdAdapter.setOnClick {
                override fun itemClicked(branch: ItemMedIdPrice, position: Int) {

                }

                override fun itemPhoneClicked(branch: ItemMedIdPrice, position: Int) {
                    if (branch.phone != "") {
                        val intent = Intent(Intent.ACTION_DIAL)
                        intent.data = Uri.parse("tel:" + branch.phone)
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Phone Number not found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun itemMapClicked(branch: ItemMedIdPrice, position: Int) {
                    var i = Intent(requireContext(), PharmacyMapActivity::class.java)
                    if (branch.latitude != null && branch.longitude != null) {
                        i.putExtra("lat", branch)
                        startActivity(i)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "No Location at this Pharmacy",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
            try {
                branchbyMap.branches(pos, location.lat, location.long, position!!)
                    .observe(viewLifecycleOwner, {
                        lifecycleScope.launch {
                            adapter.submitData(it)
                        }
                    })
                binding.rv.adapter = adapter
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
            }
        } else {
            view?.let {
                Snackbar.make(it, "No Internet connection", Snackbar.LENGTH_LONG)
                    .show()
            }
            showLoading()
        }

    }

    fun showLoading() {
        binding.loading.visibility = View.VISIBLE
        binding.rv.visibility = View.INVISIBLE

    }

    fun hideLoading() {
        binding.loading.visibility = View.INVISIBLE
        binding.rv.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        pos = SharedPreference.getInstance(requireContext()).medId.toInt()
//        loadData()
//        (activity as MainActivity).hideBottomBar()
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as MainActivity).showBottomBar()
    }

    companion object {
        @JvmStatic
        fun newInstance(position: String) =
            InfoViewPagerFragment().apply {
                arguments = Bundle().apply {
//                    putInt(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, position)
                }
            }
    }
}