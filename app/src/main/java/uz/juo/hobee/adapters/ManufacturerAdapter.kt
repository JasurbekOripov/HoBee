package uz.juo.hobee.adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import uz.juo.hobee.databinding.ManufacturerItemBinding
import uz.juo.hobee.models.ManufacturerItem

class ManufacturerAdapter(var context: Context, var filter: String, var itemClick: setOnClick) :
    PagingDataAdapter<ManufacturerItem, ManufacturerAdapter.Vh>(MyDiffUtil()) {

    inner class Vh(var item: ManufacturerItemBinding) :
        RecyclerView.ViewHolder(item.root) {
        fun onBind(branch: ManufacturerItem, position: Int) {
            item.radio.isChecked = filter == branch.manufacturer
            item.root.setOnClickListener {
                itemClick.itemClicked(branch.manufacturer, position)
            }
            item.radio.setOnClickListener {
                itemClick.itemClicked(branch.manufacturer, position)
            }
            item.itemTv.text = branch.manufacturer
        }
    }

    class MyDiffUtil : DiffUtil.ItemCallback<ManufacturerItem>() {
        override fun areItemsTheSame(
            oldItem: ManufacturerItem,
            newItem: ManufacturerItem
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: ManufacturerItem,
            newItem: ManufacturerItem
        ): Boolean {
            return oldItem == newItem
        }

    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        getItem(position)?.let { holder.onBind(it, position) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(
            ManufacturerItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    interface setOnClick {
        fun itemClicked(branch: String, position: Int)
    }
}