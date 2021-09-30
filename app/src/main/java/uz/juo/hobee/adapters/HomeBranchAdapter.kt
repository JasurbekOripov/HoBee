package uz.juo.hobee.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.juo.hobee.databinding.HomeNearbyRvItemBinding
import uz.juo.hobee.models.Item
import uz.juo.hobee.models.NeariestPharmcy
import uz.juo.hobee.utils.Functions

class HomeBranchAdapter(var list: ArrayList<NeariestPharmcy>, var itemClick: itemOnCLick) :
    RecyclerView.Adapter<HomeBranchAdapter.Vh>() {
    inner class Vh(var item: HomeNearbyRvItemBinding) : RecyclerView.ViewHolder(item.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(pharmacy: NeariestPharmcy, position: Int) {
            item.barnchName.text = pharmacy.name
            if (pharmacy.distance != null) {
                item.distance.text = Functions().kmConvertor(pharmacy.distance)
            } else {
                item.distance.text = "Нет"
            }
            item.workingTime.text =
                Functions().getWorkingTime(pharmacy.start_time, pharmacy.end_time)
            item.root.setOnClickListener {
                itemClick.itemClick(pharmacy.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh = Vh(
        HomeNearbyRvItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(list[position], position)
    }

    override fun getItemCount(): Int = list.size

    interface itemOnCLick {
        fun itemClick(id: Int)
    }
}