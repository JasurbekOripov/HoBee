package uz.juo.hobee.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import uz.juo.hobee.R
import uz.juo.hobee.databinding.BestMdeicamentsItemBinding
import uz.juo.hobee.models.Medicament
import uz.juo.hobee.utils.Functions
import uz.juo.hobee.utils.MyInterpolator

class HomeMediacamentAdapter(
    var context: Context,
    var list: ArrayList<Medicament>,
    var itemClick: itemOnCLick
) :
    RecyclerView.Adapter<HomeMediacamentAdapter.Vh>() {
    inner class Vh(var item: BestMdeicamentsItemBinding) : RecyclerView.ViewHolder(item.root) {
        fun onBind(mediacament: Medicament, position: Int) {
            var likable = false
            item.name.text = mediacament.name ?: ""
            if (mediacament.price == null || mediacament.price == "") {
                item.price.text = "нет в наличии"
            } else {
                item.price.text = ("от ${mediacament.price}")
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
                    val myAnim: Animation = AnimationUtils.loadAnimation(context, R.anim.bounce_for_like)
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
        fun itemClick(mediacament: Medicament, position: Int)
        fun itemLikeClick(mediacament: Medicament, position: Int, state: Boolean)
    }
}