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
import kotlinx.coroutines.launch
import uz.juo.hobee.MainActivity
import uz.juo.hobee.R
import uz.juo.hobee.adapters.BranchInfoAdapter
import uz.juo.hobee.databinding.FragmentInfoBranchBinding
import uz.juo.hobee.models.Item
import uz.juo.hobee.models.ItemX
import uz.juo.hobee.room.AppDataBase
import uz.juo.hobee.room.entity.FavoritesEntity
import uz.juo.hobee.utils.SharedPreference
import uz.juo.hobee.viewmodel.get_medicaments_pharmacy.MedicamentsPharmacyModelFactory
import uz.juo.hobee.viewmodel.get_medicaments_pharmacy.MedicamentsPharmacyViewModel

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class InfoBranchFragment : Fragment() {
    lateinit var binding: FragmentInfoBranchBinding
    var name = ""
    private var param1: Item? = null
    private var param2: String? = null
    lateinit var viewModel: MedicamentsPharmacyViewModel
    lateinit var adapter: BranchInfoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getSerializable(ARG_PARAM1) as Item
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
        loadData()
        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.map.setOnClickListener {
            Toast.makeText(requireContext(), param1?.longitude.toString(), Toast.LENGTH_SHORT)
                .show()
        }
        binding.phone.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:" + param1?.phone)
            startActivity(intent)
        }
        binding.name.text = param1?.name
        adapter = BranchInfoAdapter(requireContext(), object : BranchInfoAdapter.setOnClick {
            override fun itemClick(mediacament: ItemX, position: Int) {
                var bundle = Bundle()
                bundle.putInt("param1", mediacament.id)
                SharedPreference.getInstance(requireContext()).lang = mediacament.id.toString()
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
        binding.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                name = p0.toString()
                param1?.id?.let {
                    viewModel.medicaments(requireContext(), name, it)
                        .observe(viewLifecycleOwner, Observer {
                            lifecycleScope.launch {
                                adapter.submitData(it)
                            }
                        })
                }
            }
        })
        return binding.root
    }

    private fun loadData() {
        viewModel.medicaments(requireContext(), "", param1?.id!!).observe(viewLifecycleOwner, {
            lifecycleScope.launch {
                adapter.submitData(it)
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