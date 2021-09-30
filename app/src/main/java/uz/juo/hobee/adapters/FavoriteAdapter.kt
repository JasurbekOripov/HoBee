package uz.juo.hobee.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.juo.hobee.R
import uz.juo.hobee.databinding.BestMdeicamentsItemBinding
import uz.juo.hobee.models.Medicament
import uz.juo.hobee.room.entity.FavoritesEntity
import uz.juo.hobee.utils.Functions

class FavoriteAdapter(
    var list: ArrayList<FavoritesEntity>,
    var itemClick: itemOnCLick,
    var context: Context
) :
    RecyclerView.Adapter<FavoriteAdapter.Vh>() {
    inner class Vh(var item: BestMdeicamentsItemBinding) : RecyclerView.ViewHolder(item.root) {
        fun onBind(mediacament: FavoritesEntity, position: Int) {
            item.name.text = mediacament.name ?: ""
            if (mediacament.price == null || mediacament.price == "") {
                item.price.text = "нет в наличии"
            } else {
                item.price.text = ("от ${mediacament.price}")
            }
            item.manufacturer.text = mediacament.manufacturer ?: ""
            item.like.setImageResource(R.drawable.ic_liked)
            item.root.setOnClickListener {
                itemClick.itemClick(mediacament, position)
            }
            item.like.setOnClickListener {
                var stateLike = false
                item.like.setImageResource(R.drawable.ic_liked)
                itemClick.itemLikeClick(mediacament, position, stateLike)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh = Vh(
        BestMdeicamentsItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(list[position], position)
    }

    override fun getItemCount(): Int = list.size

    interface itemOnCLick {
        fun itemClick(mediacament: FavoritesEntity, position: Int)
        fun itemLikeClick(mediacament: FavoritesEntity, position: Int, state: Boolean)
    }
}