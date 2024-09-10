package com.leiteup.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.leiteup.controller.CowController
import com.leiteup.databinding.FragmentEditFormMilkBinding
import com.leiteup.model.Milking

class EditFormMilkFragment : Fragment() {

    private var _binding: FragmentEditFormMilkBinding? = null
    private val binding get() = _binding!!

    private lateinit var milking: Milking
    private lateinit var cowController: CowController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            milking = EditFormMilkFragmentArgs.fromBundle(it).milking
        }
        cowController = CowController()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditFormMilkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fillFormWithMilkingData()
        initClicks()
    }

    private fun fillFormWithMilkingData() {

        binding.edtCowName.setText(milking.cowName)
        binding.edtQuantity.setText(milking.quantity.toString())
    }

    private fun initClicks() {

        binding.btnEditMilking.setOnClickListener {
            validateMilking()
        }
    }

    private fun validateMilking() {
        val newCowName = binding.edtCowName.text.toString().trim().uppercase()
        val newQuantity = binding.edtQuantity.text.toString().trim().toDouble()

        cowController.cowExists(newCowName, { exists ->
            if (exists) {
                Toast.makeText(requireContext(), "Animal atualizado com sucesso.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Animal não encontrado, digite um animal válido", Toast.LENGTH_SHORT).show()
            }
        }, { error ->
            Toast.makeText(requireContext(), "o.", Toast.LENGTH_SHORT).show()
        })

    }

    private fun updateMilking(newCowName: String, newQuantity: Int) {

    }
}