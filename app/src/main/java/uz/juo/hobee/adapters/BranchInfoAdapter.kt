package uz.juo.hobee.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import uz.juo.hobee.R
import uz.juo.hobee.databinding.BestMdeicamentsItemBinding
import uz.juo.hobee.models.ItemX
import uz.juo.hobee.utils.Functions
import uz.juo.hobee.utils.MyInterpolator

class BranchInfoAdapter(var context: Context, var itemClick: setOnClick) :
    PagingDataAdapter<ItemX, BranchInfoAdapter.Vh>(MyDiffUtil()) {

    inner class Vh(var item: BestMdeicamentsItemBinding) :
        RecyclerView.ViewHolder(item.root) {
        fun onBind(mediacament: ItemX, position: Int) {
            var likable = false
            item.name.text = mediacament.name ?: ""
            if (mediacament.price == null || mediacament.price == "") {
                item.price.text = "нет в наличии"
            } else {
                item.price.text = ("${mediacament.price}")
            }
            item.manufacturer.text = mediacament.manufacturer ?: ""
            for (i in Functions().getFavorite(context)) {
                if (i.id == mediacament.id) {
                    likable = true
                    break
                } else {
                    likable = false
                }
            }
            if (likable) {
                item.like.setImageResource(R.drawable.ic_liked)
            } else {
                item.like.setImageResource(R.drawable.ic_like_icon)
            }
            item.root.setOnClickListener {
                itemClick.itemClick(mediacament, position)
            }
            item.like.setOnClickListener {
                var stateLike = false
                for (i in Functions().getFavorite(context)) {
                    if (i.id == mediacament.id) {
                        stateLike = true
                        break
                    } else {
                        stateLike = false
                    }
                }
                if (!stateLike) {
                    val myAnim: Animation = AnimationUtils.loadAnimation(context, R.anim.bounce)
                    val interpolator = MyInterpolator(0.2, 20.0)
                    myAnim.interpolator = interpolator
                    item.like.startAnimation(myAnim)
                    item.like.setImageResource(R.drawable.ic_liked)
                } else {
                    item.like.setImageResource(R.drawable.ic_like_icon)
                }
                itemClick.itemLikeClick(mediacament, position, stateLike)
            }
        }
    }

    class MyDiffUtil() : DiffUtil.ItemCallback<ItemX>() {
        override fun areItemsTheSame(oldItem: ItemX, newItem: ItemX): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ItemX, newItem: ItemX): Boolean {
            return oldItem == newItem
        }

    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BranchInfoAdapter.Vh {
        return Vh(
            BestMdeicamentsItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    interface setOnClick {
        fun itemClick(mediacament: ItemX, position: Int)
        fun itemLikeClick(mediacament: ItemX, position: Int, state: Boolean)
    }

    override fun onBindViewHolder(holder: BranchInfoAdapter.Vh, position: Int) {
        getItem(position).let {
            if (it != null) {
                holder.onBind(it, position)
            }
        }
    }
}