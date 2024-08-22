package com.leiteup.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.leiteup.R
import com.leiteup.databinding.CowAdapterBinding
import com.leiteup.model.Cow
import androidx.navigation.fragment.findNavController
import com.leiteup.ui.CowFragmentDirections


class CowAdapter(
    private val context: Context,
    private val cowList: List<Cow>,
    val cowSelected: (Cow) -> Unit
): RecyclerView.Adapter<CowAdapter.MyViewHolder>() {

    companion object {
        val SELECT_DETAILS: Int = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CowAdapter.MyViewHolder {
        return MyViewHolder(
            CowAdapterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val cow = cowList[position]

        // holder.binding.cowImage.setImageDrawable() = cow.cowImage
        holder.binding.earring.text = cow.earring.toString()
        holder.binding.breed.text = cow.breed
        holder.binding.gender.text = cow.gender
//        var yearsOld: Int = Period.between(cow.birthDay, LocalDate.now()).years
//        holder.binding.yearsOld.text = yearsOld.toString()

        holder.itemView.setOnClickListener {
            val action = CowFragmentDirections.actionCowFragmentToCowDetail(cow)
            it.findNavController().navigate(action)
            Log.d("cliquei", "chamou a funtion " + cow)
        }
    }

    override fun getItemCount() = cowList.size

    inner class MyViewHolder(val binding: CowAdapterBinding) :
            RecyclerView.ViewHolder(binding.root)
}