package com.leiteup.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.leiteup.databinding.FoodAdapterBinding
import com.leiteup.model.Cow


class FoodAdapter(
    private val foodList: List<Pair<Cow, Double>>

): RecyclerView.Adapter<FoodAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodAdapter.MyViewHolder {
        return MyViewHolder(
            FoodAdapterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: FoodAdapter.MyViewHolder, position: Int) {
        val (cow, adjustedAverage) = foodList[position]

        holder.binding.cowName.text = cow.name
        holder.binding.food.text = adjustedAverage.toString().take(4) + " Kgs"

    }

    override fun getItemCount() = foodList.size

    inner class MyViewHolder(val binding: FoodAdapterBinding) :
        RecyclerView.ViewHolder(binding.root)

}