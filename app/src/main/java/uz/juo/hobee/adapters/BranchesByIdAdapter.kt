package uz.juo.hobee.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import uz.juo.hobee.databinding.InfoMedicamentItemBinding
import uz.juo.hobee.models.ItemMedIdPrice
import uz.juo.hobee.utils.Functions

class BranchesByIdAdapter(var itemClick: setOnClick) :
    PagingDataAdapter<ItemMedIdPrice, BranchesByIdAdapter.Vh>(MyDiffUtil()) {

    inner class Vh(var item: InfoMedicamentItemBinding) :
        RecyclerView.ViewHolder(item.root) {
        fun onBind(branch: ItemMedIdPrice, position: Int) {
            item.branchName.text = branch.name
            if (branch.distance != null) {
                item.distance.text = "Нету"
            } else {
                item.distance.text = Functions().kmConvertor(branch.distance)
            }
            item.workingTime.text = Functions().getWorkingTime(branch.start_time, branch.end_time)
            item.root.setOnClickListener {
                itemClick.itemClicked(branch, position)
            }
            item.callBtn.setOnClickListener {
                itemClick.itemPhoneClicked(branch, position)
            }
            item.locationBtn.setOnClickListener {
                itemClick.itemMapClicked(branch, position)
            }
        }
    }

    class MyDiffUtil() : DiffUtil.ItemCallback<ItemMedIdPrice>() {
        override fun areItemsTheSame(oldItem: ItemMedIdPrice, newItem: ItemMedIdPrice): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ItemMedIdPrice, newItem: ItemMedIdPrice): Boolean {
            return oldItem == newItem
        }

    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        getItem(position)?.let { holder.onBind(it, position) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(
            InfoMedicamentItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    interface setOnClick {
        fun itemClicked(branch: ItemMedIdPrice, position: Int)
        fun itemPhoneClicked(branch: ItemMedIdPrice, position: Int)
        fun itemMapClicked(branch: ItemMedIdPrice, position: Int)
    }
}