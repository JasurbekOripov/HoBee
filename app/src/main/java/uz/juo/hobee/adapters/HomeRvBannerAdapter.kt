package uz.juo.hobee.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils

import androidx.recyclerview.widget.RecyclerView
import uz.juo.hobee.R
import uz.juo.hobee.databinding.ViewPagerItemBinding

class HomeRvBannerAdapter(var context:Context) : RecyclerView.Adapter<HomeRvBannerAdapter.Vh>() {
    inner class Vh(var item: ViewPagerItemBinding) : RecyclerView.ViewHolder(item.root) {
        fun onBind(position: Int) {
            var anim = AnimationUtils.loadAnimation(context, R.anim.rv)
            item.root.startAnimation(anim)
            Log.d("TAG", "onBind:$position ")

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ViewPagerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int = 3
}