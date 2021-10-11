package uz.juo.hobee.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import uz.juo.hobee.databinding.ViewPagerItemBinding
import uz.juo.hobee.ui.viewpager.ViewPagerFragment
import java.util.*


class ViewPagerAdapter(var context: Context): RecyclerView.Adapter<ViewPagerAdapter.Vh>(){
    inner class Vh(var itemViewpagerBinding: ViewPagerItemBinding):RecyclerView.ViewHolder(itemViewpagerBinding.root){
}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ViewPagerItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {

    }

    override fun getItemCount(): Int =3
}