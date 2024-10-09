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

        var cowEarringText = ""
        var cowName = ""

        if(binding.btnEarring.isChecked) {
            cowEarringText = binding.edtCowNameOrEarring.text.toString().trim()
        } else {
            cowName = binding.edtCowNameOrEarring.text.toString().trim().uppercase()
        }

        var quantity = -1.0
        if(binding.edtQuantity.text.isNotEmpty()) {
            quantity = binding.edtQuantity.text.toString().trim().toDouble()
        }
        if(cowEarringText.isEmpty() && cowName.isEmpty()){
            binding.edtCowNameOrEarring.setBackgroundResource(R.drawable.bg_input_error)
            Toast.makeText(requireContext(), "O campo de indentificação não pode estar em branco.", Toast.LENGTH_SHORT).show()
        } else {
            if(quantity != -1.0) {
                if (cowName.isNotEmpty()) {
                    cowController.cowExists(cowName, { exists, cow ->
                        if (exists && cow != null) {
                            milkingController.validateAndSaveMilking(cowName, quantity, cow.earring,
                                onSuccess = {
                                    findNavController().popBackStack()
                                    Toast.makeText(requireContext(), "Ordenha salva com sucesso.", Toast.LENGTH_SHORT).show()
                                },
                                onError = { errorMessage ->
                                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                                }
                            )
                        } else {
                            binding.edtCowNameOrEarring.setBackgroundResource(R.drawable.bg_input_error)
                            binding.edtQuantity.setBackgroundResource(R.drawable.bg_input_text)
                            Toast.makeText(requireContext(), "Não existe nenhuma animal com esse nome ou brinco, digite um animal válido", Toast.LENGTH_SHORT).show()
                        }
                    }, { error ->
                        Toast.makeText(requireContext(), "o.", Toast.LENGTH_SHORT).show()
                    })
                }
                if (cowEarringText.isNotEmpty()) {
                    val cowEarring = cowEarringText.toIntOrNull()
                    if(cowEarring != null) {
                        cowController.cowExistsByEarring(cowEarringText.toInt(), { exists, cow ->
                            if (exists && cow != null) {
                                milkingController.validateAndSaveMilking(cow.name,
                                    quantity,
                                    cowEarringText.toInt(),
                                    onSuccess = {
                                        findNavController().popBackStack()
                                        Toast.makeText(
                                            requireContext(),
                                            "Ordenha salva com sucesso.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    onError = { errorMessage ->
                                        Toast.makeText(
                                            requireContext(),
                                            errorMessage,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                )
                            } else {
                                binding.edtCowNameOrEarring.setBackgroundResource(R.drawable.bg_input_error)
                                binding.edtQuantity.setBackgroundResource(R.drawable.bg_input_text)
                                Toast.makeText(
                                    requireContext(),
                                    "Não existe nenhuma animal com esse nome ou brinco, digite um animal válido",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }, { error ->
                            Toast.makeText(requireContext(), "o.", Toast.LENGTH_SHORT).show()
                        })
                    } else {
                        binding.edtCowNameOrEarring.setBackgroundResource(R.drawable.bg_input_error)
                        Toast.makeText(requireContext(), "O campo brinco deve conter apenas números.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                binding.edtCowNameOrEarring.setBackgroundResource(R.drawable.bg_input_text)
                binding.edtQuantity.setBackgroundResource(R.drawable.bg_input_error)
                Toast.makeText(requireContext(), "O campo quantidade não pode estar em branco.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}