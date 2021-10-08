package uz.juo.hobee.ui.favorite

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import uz.juo.hobee.MainActivity
import uz.juo.hobee.R
import uz.juo.hobee.adapters.FavoriteAdapter
import uz.juo.hobee.databinding.FragmentFavoriteBinding
import uz.juo.hobee.models.*
import uz.juo.hobee.retrofit.ApiClient
import uz.juo.hobee.room.AppDataBase
import uz.juo.hobee.room.entity.FavoritesEntity
import uz.juo.hobee.utils.SharedPreference
import java.lang.Exception

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FavoriteFragment : Fragment() {
    lateinit var binding: FragmentFavoriteBinding
    lateinit var adapter: FavoriteAdapter
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        var db = AppDataBase.getInstance(requireContext())
        var list = db.dao().getAll() as ArrayList<FavoritesEntity>
        loadFavorites(list)
        binding.refresh.setOnClickListener {
            updateFavorites()
        }
        return binding.root
    }

    private fun updateFavorites() {
        try {
            val db = AppDataBase.getInstance(requireContext())
            val savedList = db.dao().getAll() as ArrayList<FavoritesEntity>
            val idList = ArrayList<Int>()
            for (i in savedList) {
                idList.add(i.id)
            }
            if (idList.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    var id = PostObject(idList)
                    val data = ApiClient.apiService.updateFavorites(id)
                    data.enqueue(object : Callback<ObjectFavorites> {
                        @SuppressLint("NotifyDataSetChanged")
                        override fun onResponse(
                            call: Call<ObjectFavorites>,
                            response: Response<ObjectFavorites>
                        ) {
                            if (response.body() != null) {
                                db.dao().deleteAll()
                                var list = response.body() as ArrayList<ObjectFavoritesItem>
                                var favorits = ArrayList<FavoritesEntity>()
                                for (i in list) {
                                    var medicament = FavoritesEntity(i.id, i.name, i.manufacturer, i.country, i.category, i.price)
                                    favorits.add(medicament)
                                    db.dao().add(medicament)
                                }
                                loadFavorites(favorits)
                            } else {
                                view?.let {
                                    Snackbar.make(it, "Update failed", Snackbar.LENGTH_LONG).show()
                                }
                            }
                        }

                        override fun onFailure(call: Call<ObjectFavorites>, t: Throwable) {
                            view?.let {
                                Snackbar.make(it, "Check internet connection", Snackbar.LENGTH_LONG)
                                    .show()
                            }
                        }
                    })

                }
            } else {
                view?.let {
                    Snackbar.make(it, "Not founded medicament's for update", Snackbar.LENGTH_LONG)
                        .show()
                }
            }

        } catch (e: Exception) {
            view?.let {
                Snackbar.make(it, "You can not update favorites", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun loadFavorites(list: ArrayList<FavoritesEntity>) {
        var db = AppDataBase.getInstance(requireContext())
        adapter = FavoriteAdapter(
            list,
            object : FavoriteAdapter.itemOnCLick {
                override fun itemClick(mediacament: FavoritesEntity, position: Int) {
                    var bundle = Bundle()
                    SharedPreference.getInstance(requireContext()).medId = mediacament.id.toString()
                    bundle.putInt("param1", mediacament.id)
                    findNavController(this@FavoriteFragment).navigate(
                        R.id.infoMedicamentFragment,
                        bundle
                    )
                }

                override fun itemLikeClick(
                    mediacament: FavoritesEntity,
                    position: Int,
                    state: Boolean
                ) {
                    list.remove(mediacament)
                    db.dao().delete(mediacament.id)
                    adapter.notifyItemRemoved(position)
                    adapter.notifyItemRangeRemoved(position, list.size)
                }
            }, requireContext()
        )
        binding.rv.adapter = adapter
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FavoriteFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}