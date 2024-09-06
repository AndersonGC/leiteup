package com.leiteup.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.leiteup.databinding.MilkingAdapterBinding
import com.leiteup.model.Milking
import com.leiteup.ui.MilkingListDirections


class MilkingAdapter(
    private val context: Context,
    private val milkingList: List<Milking>,
    val milkingselec: (Milking) -> Unit
): RecyclerView.Adapter<MilkingAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MilkingAdapter.MyViewHolder {
        return MyViewHolder(
            MilkingAdapterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MilkingAdapter.MyViewHolder, position: Int) {
        val milking = milkingList[position]

        holder.binding.dateMilking.text = milking.date
        holder.binding.orderMilking.text = milking.milkingNumber.toString()

        holder.itemView.setOnClickListener {
            val action = MilkingListDirections.actionMilkingListToEditFormMilkFragment(milking)
            it.findNavController().navigate(action)
        }
    }

    override fun getItemCount() = milkingList.size

    inner class MyViewHolder(val binding: MilkingAdapterBinding) :
        RecyclerView.ViewHolder(binding.root)

}