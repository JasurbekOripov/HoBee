package uz.juo.hobee.adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import uz.juo.hobee.R
import uz.juo.hobee.databinding.BranchRvItemBinding
import uz.juo.hobee.models.Item
import uz.juo.hobee.utils.Functions

class AllBranchesAdapter(var context: Context,var itemClick: setOnClick) :
    PagingDataAdapter<Item, AllBranchesAdapter.Vh>(MyDiffUtil()) {

    inner class Vh(var item: BranchRvItemBinding) :
        RecyclerView.ViewHolder(item.root) {
        fun onBind(branch: Item, position: Int) {
            item.name.text = branch.name
            item.distance1.text = Functions().kmConvertor(branch.distance)
            item.writeTime.text = Functions().getWorkingTime(branch.start_time, branch.end_time)
            var anim = AnimationUtils.loadAnimation(context, R.anim.rv)
            item.root.startAnimation(anim)
            item.root.setOnClickListener {
                itemClick.itemClicked(branch, position)
            }
        }
    }

    class MyDiffUtil() : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }

    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        getItem(position)?.let { holder.onBind(it, position) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(BranchRvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    interface setOnClick {
        fun itemClicked(branch: Item, position: Int)
    }
}