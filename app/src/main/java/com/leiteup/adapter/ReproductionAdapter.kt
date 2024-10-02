package com.leiteup.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.leiteup.databinding.ReproductionAdapterBinding
import com.leiteup.model.Cow

class ReproductionAdapter(
    private val context: Context,
    private var cowPregnant: List<Cow>,
    val cowSelected: (Cow) -> Unit
    ): RecyclerView.Adapter<ReproductionAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReproductionAdapter.MyViewHolder {
        return MyViewHolder(
            ReproductionAdapterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val cow = cowPregnant[position]

        holder.binding.name.text = cow.name.lowercase().replaceFirstChar { it.uppercase()}
        holder.binding.breed.text = cow.breed
        holder.binding.timeForBirth.text = cow.birthDay

        holder.itemView.setOnClickListener {
            //val action = CowFragmentDirections.actionCowFragmentToCowDetail(cow)
            //it.findNavController().navigate(action)
        }
    }

    override fun getItemCount() = cowPregnant.size

    inner class MyViewHolder(val binding: ReproductionAdapterBinding) :
            RecyclerView.ViewHolder(binding.root)

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newReproductionList: List<Cow>) {
        cowPregnant = newReproductionList
        notifyDataSetChanged()
    }
}