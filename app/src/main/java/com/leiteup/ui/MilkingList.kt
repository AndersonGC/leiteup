package com.leiteup.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.leiteup.adapter.MilkingAdapter
import com.leiteup.databinding.FragmentMilkingListBinding
import com.leiteup.helper.FirebaseHelper
import com.leiteup.model.Cow
import com.leiteup.model.Milking


class MilkingList : Fragment() {
    // TODO: Rename and change types of parameters
    private var _binding: FragmentMilkingListBinding? = null
    private val binding get() = _binding!!

    private lateinit var milkingAdapter: MilkingAdapter
    private lateinit var cow: Cow

    private val  milkingList = mutableListOf<Milking>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            cow = MilkingListArgs.fromBundle(it).cow
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMilkingListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initClicks()
        getMilkings()
    }

    private fun initClicks() {

    }

    private fun getMilkings() {
        val milkingsReference = FirebaseHelper.getDatabase().child("milkings")
        val cowEarring = cow.earring.toString()
        Log.d("Firebase", "Referência: ${milkingsReference.path}")

        milkingsReference
            .orderByChild("cowEarring")
            .equalTo(cowEarring.toDouble()) // Convertendo para Double, pois Firebase armazena números como Double
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    Log.d("Firebase", "onDataChange chamado")
                    // Verifique se existe algum dado no nó
                    if (dataSnapshot.exists()) {
                        milkingList.clear()
                        for (childSnapshot in dataSnapshot.children) {
                            // Obtenha o valor dentro do nó filho
                            val milking = childSnapshot.getValue(Milking::class.java)
                            if (milking != null) {
                                milkingList.add(milking)
                                Log.d("Firebase", "Milking ID: ${milking.id}, Date: ${milking.date}, Quantity: ${milking.quantity}")
                            } else {
                                Log.w("Firebase", "Objeto Milking é nulo")
                            }
                        }
                        initAdapter()
                    } else {
                        Log.d("Firebase", "Nenhum dado encontrado para o cowEarring $cowEarring")
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w("Firebase", "Falha ao ler o valor.", databaseError.toException())
                }
            })
    }

    private fun initAdapter() {
        if (_binding == null) {
            return
        }
        binding.milkingList.layoutManager = LinearLayoutManager(requireContext())
        binding.milkingList.setHasFixedSize(true)

        milkingAdapter  = MilkingAdapter(requireContext(), milkingList) {milking ->

        }
        binding.milkingList.adapter = milkingAdapter
    }

}