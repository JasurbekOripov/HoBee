package uz.juo.hobee.ui.searchMedicaments

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.SearchView
import androidx.core.view.marginBottom
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.FadingCircle
import kotlinx.coroutines.launch
import uz.juo.hobee.MainActivity
import uz.juo.hobee.R
import uz.juo.hobee.adapters.SearchMedicamentAdapter
import uz.juo.hobee.databinding.FragmentSearchBinding
import uz.juo.hobee.models.ItemX
import uz.juo.hobee.room.AppDataBase
import uz.juo.hobee.room.entity.FavoritesEntity
import uz.juo.hobee.utils.SharedPreference
import uz.juo.hobee.viewmodel.search_medicament.SearchViewModel

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class SearchMedicamentFragment : Fragment() {
    lateinit var binding: FragmentSearchBinding
    lateinit var searchViewMoedl: SearchViewModel
    lateinit var adapter: SearchMedicamentAdapter
    var name = ""
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
        loadData()
        val progressBar = binding.spinKit as ProgressBar
        val doubleBounce: Sprite = FadingCircle()
        progressBar.indeterminateDrawable = doubleBounce
        binding.refresh.setOnRefreshListener {
            loadData()
            binding.refresh.isRefreshing = false
        }

        return binding.root
    }

    private fun loadData() {
        adapter = SearchMedicamentAdapter(requireContext(), object : SearchMedicamentAdapter.setOnClick {
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

        binding.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                name = p0.toString()
                searchViewMoedl.medicaments(requireContext(), name)
                    .observe(viewLifecycleOwner, Observer {
                        lifecycleScope.launch {
                            adapter.submitData(it)
                        }
                    })
            }
        })

        searchViewMoedl.medicaments(requireContext(), name).observe(viewLifecycleOwner, Observer {
            lifecycleScope.launch {
                adapter.submitData(it)
            }
        })
        binding.rv.adapter = adapter
    }


    override fun onResume() {
        super.onResume()
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