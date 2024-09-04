package com.leiteup.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.leiteup.controller.MilkingController
import com.leiteup.databinding.FragmentMilkingBinding

class MilkingFragment : Fragment() {

    private var _binding: FragmentMilkingBinding? = null
    private val binding get() = _binding!!

    private lateinit var milkingController: MilkingController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        milkingController = MilkingController()
    }

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

        binding.btnAddMilking.setOnClickListener() {
            validateMilking()
        }


    }

    private fun validateMilking() {

        val cowName = binding.edtCowName.text.toString().trim().uppercase()
        val quantity = binding.edtQuantity.text.toString().trim().toDouble()

        if (cowName.isNotEmpty() && quantity != null) {
            milkingController.validateAndSaveMilking(cowName, quantity,
                onSuccess = {
                    findNavController().popBackStack()
                    Toast.makeText(requireContext(), "Ordenha salva com sucesso.", Toast.LENGTH_SHORT).show()
                },
                onError = { errorMessage ->
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }
            )
        } else {
            Toast.makeText(requireContext(), "Preencha todos os campos corretamente.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}