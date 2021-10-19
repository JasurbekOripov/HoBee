package uz.juo.hobee.ui.searchMedicaments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isEmpty
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.FadingCircle
import kotlinx.coroutines.launch
import uz.juo.hobee.MainActivity
import uz.juo.hobee.adapters.SearchMedicamentAdapter
import uz.juo.hobee.databinding.FragmentSearchBinding
import uz.juo.hobee.models.ItemX
import uz.juo.hobee.room.AppDataBase
import uz.juo.hobee.room.entity.FavoritesEntity
import uz.juo.hobee.utils.SharedPreference
import uz.juo.hobee.viewmodel.search_medicament.SearchViewModel
import java.lang.Exception
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import uz.juo.hobee.R
import uz.juo.hobee.adapters.ManufacturerAdapter
import uz.juo.hobee.viewmodel.manufacturer.ManufacturerViewModel
import java.sql.Time
import java.util.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class SearchMedicamentFragment : Fragment() {
    lateinit var binding: FragmentSearchBinding
    var time = 0L
    lateinit var manufacturerViewModel: ManufacturerViewModel
    lateinit var searchViewMoedl: SearchViewModel
    lateinit var adapter: SearchMedicamentAdapter
    lateinit var manufavturerAdapter: ManufacturerAdapter
    var name = ""
    var m = ""
    var position = 0
    private var param1: String? = null
    private var param2: String? = null
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
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        searchViewMoedl = ViewModelProvider(this)[SearchViewModel::class.java]
        manufacturerViewModel = ViewModelProvider(this)[ManufacturerViewModel::class.java]
        loadData()
        val progressBar = binding.spinKit as ProgressBar
        val doubleBounce: Sprite = FadingCircle()
        progressBar.indeterminateDrawable = doubleBounce
        binding.refresh.setOnRefreshListener {
            loadData()
            binding.refresh.isRefreshing = false
        }
        binding.filter.setOnClickListener {
            showBottomSheet()
        }
        return binding.root
    }

    private fun showBottomSheet() {
        val bottomSheetDialog =
            BottomSheetDialog(requireContext(), R.style.MyTransparentBottomSheetDialogTheme)
        bottomSheetDialog.setContentView(R.layout.manufacturer_bottom_sheet)
        val rv = bottomSheetDialog.findViewById<RecyclerView>(R.id.rv)
        val search = bottomSheetDialog.findViewById<EditText>(R.id.searchManufacturer)
        search?.setText(m)
        manufavturerAdapter =
            ManufacturerAdapter(requireContext(), m, object : ManufacturerAdapter.setOnClick {
                override fun itemClicked(m1: String, position1: Int) {
                    position = position1
                    m = m1
                    binding.filterStatus.visibility = View.VISIBLE
                    searchViewMoedl.medicaments(requireContext(), name, m)
                        .observe(viewLifecycleOwner, Observer {
                            lifecycleScope.launch {
                                adapter.submitData(it)
                                rv?.scrollToPosition(position)
                            }
                        })
                    bottomSheetDialog.cancel()
                }
            })
        search?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                m = p0.toString()
                try {
                    if (m == "") {
                        manufavturerAdapter = ManufacturerAdapter(
                            requireContext(),
                            m,
                            object : ManufacturerAdapter.setOnClick {
                                override fun itemClicked(m1: String, position1: Int) {
                                    position = position1
                                    m = m1
                                    binding.filterStatus.visibility = View.VISIBLE
                                    searchViewMoedl.medicaments(requireContext(), name, m)
                                        .observe(viewLifecycleOwner, Observer {
                                            lifecycleScope.launch {
                                                adapter.submitData(it)
                                                rv?.scrollToPosition(position)
                                            }
                                        })
                                    bottomSheetDialog.cancel()
                                }
                            })
                        binding.filterStatus.visibility = View.INVISIBLE
                        rv?.adapter = manufavturerAdapter
                        loadData()
                    }
                    manufacturerViewModel.manufacturers(m).observe(viewLifecycleOwner, {
                        lifecycleScope.launch {
                            manufavturerAdapter.submitData(it)
                        }
                    })
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                }
                rv?.scrollToPosition(position)
            }
        })
        manufacturerViewModel.manufacturers(m).observe(viewLifecycleOwner, {
            lifecycleScope.launch {
                manufavturerAdapter.submitData(it)
                rv?.scrollToPosition(position)
            }
        })
        rv?.adapter = manufavturerAdapter
        bottomSheetDialog.show()
    }

    private fun loadData() {
        adapter =
            SearchMedicamentAdapter(requireContext(), object : SearchMedicamentAdapter.setOnClick {
                override fun itemClick(mediacament: ItemX, position: Int) {
                    SharedPreference.getInstance(requireContext()).medId = mediacament.id.toString()
                    findNavController().navigate(
                        R.id.infoMedicamentFragment
                    )
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
        binding.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {


            }

            override fun afterTextChanged(p0: Editable?) {
                name = p0.toString()
                try {
                    var interval = System.currentTimeMillis() - time
                    if (interval > 400 || name == "") {
                        time = System.currentTimeMillis()
                        searchViewMoedl.medicaments(requireContext(), name, m)
                            .observe(viewLifecycleOwner, {
                                lifecycleScope.launch {
                                    adapter.submitData(it)

                                }
                            })
                    }

                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                }
                binding.rv.scrollToPosition(0)
            }
        })
        searchViewMoedl.medicaments(requireContext(), name, m).observe(viewLifecycleOwner, {
            lifecycleScope.launch {
                adapter.submitData(it)
                binding.rv.scrollToPosition(0)
            }
        })
        binding.rv.adapter = adapter
    }


    override fun onResume() {
        super.onResume()
        m = ""
        loadData()
//        if (m != "") {
//            binding.filterStatus.visibility = View.VISIBLE
//        } else {
        binding.filterStatus.visibility = View.INVISIBLE
//        }
        (activity as MainActivity).hideBottomBar()
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as MainActivity).showBottomBar()
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchMedicamentFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}