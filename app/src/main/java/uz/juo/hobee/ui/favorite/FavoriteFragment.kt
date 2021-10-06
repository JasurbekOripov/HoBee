package uz.juo.hobee.ui.favorite

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment.findNavController
import uz.juo.hobee.MainActivity
import uz.juo.hobee.R
import uz.juo.hobee.adapters.FavoriteAdapter
import uz.juo.hobee.databinding.FragmentFavoriteBinding
import uz.juo.hobee.room.AppDataBase
import uz.juo.hobee.room.entity.FavoritesEntity
import uz.juo.hobee.utils.SharedPreference

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
        loadFavorites()
        return binding.root
    }

    private fun loadFavorites() {
        var db = AppDataBase.getInstance(requireContext())
        var list = db.dao().getAll() as ArrayList<FavoritesEntity>
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