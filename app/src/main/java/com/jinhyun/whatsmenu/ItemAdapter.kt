package com.jinhyun.whatsmenu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_list.view.*

class ItemAdapter(private val itemlist: List<MainData>) : RecyclerView.Adapter<ItemAdapter.ItempViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItempViewHolder {
        val itempview = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)

        return ItempViewHolder(itempview)
    }

    override fun getItemCount() = itemlist.size

    override fun onBindViewHolder(holder: ItempViewHolder, position: Int) {
        val currentItem = itemlist[position]

        holder.mealtype.text = currentItem.mealType
        holder.meal1.text = currentItem.meal1
        holder.meal2.text = currentItem.meal2
        holder.meal3.text = currentItem.meal3
        holder.meal4.text = currentItem.meal4
        holder.meal5.text = currentItem.meal5
        holder.meal6.text = currentItem.meal6
    }

    class ItempViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mealtype : TextView = itemView.mealType
        val meal1 : TextView = itemView.meal1
        val meal2 : TextView = itemView.meal2
        val meal3 : TextView = itemView.meal3
        val meal4 : TextView = itemView.meal4
        val meal5 : TextView = itemView.meal5
        val meal6 : TextView = itemView.meal6
    }

}