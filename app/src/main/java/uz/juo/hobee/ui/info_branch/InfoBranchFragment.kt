package uz.juo.hobee.ui.info_branch

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import uz.juo.hobee.MainActivity
import uz.juo.hobee.R
import uz.juo.hobee.adapters.BranchInfoAdapter
import uz.juo.hobee.databinding.FragmentInfoBranchBinding
import uz.juo.hobee.models.*
import uz.juo.hobee.retrofit.ApiClient
import uz.juo.hobee.retrofit.ApiService
import uz.juo.hobee.room.AppDataBase
import uz.juo.hobee.room.entity.FavoritesEntity
import uz.juo.hobee.ui.location.MapActivity
import uz.juo.hobee.ui.location.PharmacyMapActivity
import uz.juo.hobee.utils.Functions
import uz.juo.hobee.utils.SharedPreference
import uz.juo.hobee.viewmodel.get_medicaments_pharmacy.MedicamentsPharmacyModelFactory
import uz.juo.hobee.viewmodel.get_medicaments_pharmacy.MedicamentsPharmacyViewModel
import java.lang.Exception

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class InfoBranchFragment : Fragment() {
    lateinit var binding: FragmentInfoBranchBinding
    var name = ""
    private var param1: String? = null
    var interval = 0L
    private var param2: String? = null
    lateinit var viewModel: MedicamentsPharmacyViewModel
    lateinit var adapter: BranchInfoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        (activity as MainActivity).hideBottomBar()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInfoBranchBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[MedicamentsPharmacyViewModel::class.java]
        lifecycleScope.launch {
            try {
                var branch = ApiClient.apiService.getPharmacyById(param1?.toInt()!!)
                binding.workingTime.text =
                    Functions().getWorkingTime(branch.start_time, branch.end_time)
                binding.map.setOnClickListener {
                    if (branch.latitude != 0.0 && branch.longitude != 0.0) {
                        var i = Intent(
                            requireContext(),
                            PharmacyMapActivity::class.java
                        )
                        i.putExtra(
                            "lat",
                            ItemMedIdPrice(
                                branch.address,
                                branch.distance,
                                branch.end_time,
                                branch.id,
                                branch.latitude,
                                branch.longitude,
                                branch.name, branch.phone,
                                "",
                                branch.start_time
                            )
                        )
                        startActivity(i)
                    }
                }

                binding.phone.setOnClickListener {
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse("tel:" + branch.phone)
                    startActivity(intent)
                }
                binding.name.text = branch.name
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "No Internet connection", Toast.LENGTH_SHORT)
                    .show()
            }

        }
        loadData()
        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                name = p0.toString()
                var time = System.currentTimeMillis() - interval
                if (time > 400 || name == "") {
                    interval = System.currentTimeMillis()
                    viewModel.medicaments(requireContext(), name, param1!!.toInt())
                        .observe(viewLifecycleOwner, Observer {
                            lifecycleScope.launch {

                                adapter.submitData(it)
                            }
                        })
                }
                binding.rv.scrollToPosition(0)
            }
        })
        adapter = BranchInfoAdapter(requireContext(), object : BranchInfoAdapter.setOnClick {
            override fun itemClick(mediacament: ItemX, position: Int) {
                var bundle = Bundle()
                bundle.putInt("param1", mediacament.id)
                SharedPreference.getInstance(requireContext()).medId = mediacament.id.toString()
                findNavController().navigate(R.id.infoMedicamentFragment, bundle)
            }

            override fun itemLikeClick(mediacament: ItemX, position: Int, state: Boolean) {
                if (!state) {
                    AppDataBase.getInstance(requireContext()).dao()
                        .add(
                            FavoritesEntity(
                                mediacament.id,
                                mediacament.name,
                                mediacament.manufacturer,
                                mediacament.country,
                                "",
                                mediacament.price
                            )
                        )
                } else {
                    AppDataBase.getInstance(requireContext()).dao()
                        .delete(mediacament.id)
                }
            }
        })
        binding.rv.adapter = adapter
        return binding.root
    }

    private fun loadData() {
        viewModel.medicaments(requireContext(), "", param1?.toInt()!!).observe(viewLifecycleOwner, {
            lifecycleScope.launch {
                try {
                    adapter.submitData(it)
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                }

            }
        })
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).hideBottomBar()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            InfoBranchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    override fun onDestroy() {
        super.onDestroy()
        (activity as MainActivity).showBottomBar()
    }
}