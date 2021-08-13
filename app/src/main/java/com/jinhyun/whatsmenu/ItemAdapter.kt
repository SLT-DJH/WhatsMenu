package com.jinhyun.whatsmenu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jinhyun.whatsmenu.databinding.ItemListBinding

class ItemAdapter(
    private val itemList: List<MainData>
    ) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ItemViewHolder(itemView)
    }

    override fun getItemCount() = itemList.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    inner class ItemViewHolder(
        private val binding: ItemListBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(mainData: MainData){
            binding.mealType.text = mainData.mealType
            binding.meal1.text = mainData.meal1
            binding.meal2.text = mainData.meal2
            binding.meal3.text = mainData.meal3
            binding.meal4.text = mainData.meal4
            binding.meal5.text = mainData.meal5
            binding.meal6.text = mainData.meal6
        }
    }

}