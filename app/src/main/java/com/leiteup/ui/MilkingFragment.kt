package com.leiteup.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.leiteup.R
import com.leiteup.databinding.FragmentMilkingBinding
import com.leiteup.helper.FirebaseHelper
import com.leiteup.model.Milking
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MilkingFragment : Fragment() {

    private var _binding: FragmentMilkingBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMilkingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
    }

    private fun initListeners() {

        binding.btnAddMilking.setOnClickListener() { validateMilking() }


    }

    private fun validateMilking() {

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val instantDate = LocalDate.now().format(formatter)

        val earring = binding.edtEarring.text.toString().trim().toInt()
        val quantity = binding.edtQuantity.text.toString().trim().toDouble()

        FirebaseHelper.getDatabase()
            .child("milkings")
            .orderByChild("cowEarring")
            .equalTo(earring.toDouble())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val existingMilkings = snapshot.children.mapNotNull { it.getValue(Milking::class.java) }
                        .filter { it.date == instantDate }

                    when (existingMilkings.size) {
                        0 -> {
                            // Nenhuma ordenha na data, registrando a primeira
                            saveMilking(earring, quantity, instantDate, 1)
                        }
                        1 -> {
                            // Uma ordenha na data, registrando a segunda
                            saveMilking(earring, quantity, instantDate, 2)
                        }
                        2 -> {
                            // Duas ordenhas na data, registrando a terceira
                            saveMilking(earring, quantity, instantDate, 3)
                        }
                        else -> {
                            // Já existem três ordenhas, não permitir mais registros
                            Toast.makeText(requireContext(), "Não é permitido registrar mais de três ordenhas para a mesma data.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Erro ao consultar o banco de dados", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun saveMilking(earring: Int, quantity: Double, instantDate: String, milkingNumber: Int) {
        val milking = Milking(
            cowEarring = earring,
            quantity = quantity,
            date = instantDate,
            milkingNumber = milkingNumber
        )

        FirebaseHelper.getDatabase()
            .child("milkings")
            .child(milking.id)
            .setValue(milking)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    findNavController().popBackStack()
                    Toast.makeText(requireContext(), "Ordenha salva com sucesso.", Toast.LENGTH_SHORT).show()
                    // Ordenha salva com sucesso
                } else {
                    Toast.makeText(requireContext(), "Erro ao salvar ordenha.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}