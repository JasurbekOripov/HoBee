package uz.juo.hobee.ui.branchs

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.FadingCircle
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import uz.juo.hobee.R
import uz.juo.hobee.adapters.AllBranchesAdapter
import uz.juo.hobee.databinding.FragmentBranchBinding
import uz.juo.hobee.models.Item
import uz.juo.hobee.utils.NetworkHelper
import uz.juo.hobee.utils.SharedPreference
import uz.juo.hobee.viewmodel.all_branch_view_model.AllBranchesViewModel

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class BranchFragment : Fragment() {
    private lateinit var helper: NetworkHelper
    lateinit var binding: FragmentBranchBinding

    lateinit var branchesViewModel: AllBranchesViewModel
    lateinit var adapter: AllBranchesAdapter
    private var param1: String? = null
    private var param2: String? = null
    var name = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        helper = NetworkHelper((requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBranchBinding.inflate(inflater, container, false)
        val progressBar = binding.spinKit as ProgressBar
        val doubleBounce: Sprite = FadingCircle()
        progressBar.indeterminateDrawable = doubleBounce
        checkInternet()
        binding.refresh.setOnRefreshListener {
            checkInternet()
            binding.refresh.isRefreshing = false
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        checkInternet()
    }

    private fun getBranches() {
        hideLoading()
        val long = SharedPreference.getInstance(activity?.applicationContext).location.lat
        val lat = SharedPreference.getInstance(activity?.applicationContext).location.long
        branchesViewModel = ViewModelProvider(this)[AllBranchesViewModel::class.java]
        adapter = AllBranchesAdapter(requireContext(), object : AllBranchesAdapter.setOnClick {
            override fun itemClicked(branch: Item, position: Int) {
                var bundle = Bundle()
                bundle.putString("param1", branch.id.toString())
                findNavController().navigate(R.id.infoBranchFragment, bundle)
            }
        })
        binding.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                name = p0.toString()
                branchesViewModel.branches(name, lat, long)
                    .observe(viewLifecycleOwner, Observer {
                        lifecycleScope.launch {
                            adapter.submitData(it)
                        }
                    })
            }
        })
        branchesViewModel.branches(name, lat, long).observe(viewLifecycleOwner, Observer {
            lifecycleScope.launch {
                adapter.submitData(it)
            }
        })
        binding.rv.adapter = adapter
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun checkInternet() {
        Log.d(ContentValues.TAG, "checkInternet: worked ")
        if (helper.isNetworkConnected()) {
            getBranches()
            hideLoading()
        } else {
            view?.let {
                Snackbar.make(it, "No Internet connection", Snackbar.LENGTH_LONG)
                    .show()
            }
            showLoading()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BranchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun hideLoading() {
        binding.spinKit.visibility = View.INVISIBLE
        binding.rv.visibility = View.VISIBLE
        binding.searchLinear.visibility = View.VISIBLE
    }

    fun showLoading() {
        binding.spinKit.visibility = View.VISIBLE
        binding.rv.visibility = View.INVISIBLE
        binding.searchLinear.visibility = View.INVISIBLE
    }
}