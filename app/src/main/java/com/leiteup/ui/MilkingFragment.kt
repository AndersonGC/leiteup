package com.leiteup.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.leiteup.R
import com.leiteup.controller.CowController
import com.leiteup.controller.MilkingController
import com.leiteup.databinding.FragmentMilkingBinding

class MilkingFragment : Fragment() {

    private var _binding: FragmentMilkingBinding? = null
    private val binding get() = _binding!!

    private lateinit var milkingController: MilkingController
    private lateinit var cowController: CowController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        milkingController = MilkingController()
        cowController = CowController()
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
        var quantity = -1.0
        if(binding.edtQuantity.text.isNotEmpty()) {
            quantity = binding.edtQuantity.text.toString().trim().toDouble()
        }

        if (cowName.isNotEmpty()) {
            if(quantity != -1.0) {
                cowController.cowExists(cowName, { exists ->
                    if (exists) {
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
                        binding.edtCowName.setBackgroundResource(R.drawable.bg_input_error)
                        binding.edtQuantity.setBackgroundResource(R.drawable.bg_input_text)
                        Toast.makeText(requireContext(), "Não existe nenhuma animal com esse nome, digite um animal válido", Toast.LENGTH_SHORT).show()
                    }
                }, { error ->
                    Toast.makeText(requireContext(), "o.", Toast.LENGTH_SHORT).show()
                })
            } else {
                binding.edtCowName.setBackgroundResource(R.drawable.bg_input_text)
                binding.edtQuantity.setBackgroundResource(R.drawable.bg_input_error)
                Toast.makeText(requireContext(), "O campo quantidade não pode estar em branco.", Toast.LENGTH_SHORT).show()
            }
        } else {
            binding.edtCowName.setBackgroundResource(R.drawable.bg_input_error)
            Toast.makeText(requireContext(), "O campo nome não pode estar em branco.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}