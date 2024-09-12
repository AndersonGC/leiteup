package com.leiteup.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.leiteup.controller.CowController
import com.leiteup.controller.MilkingController
import com.leiteup.databinding.FragmentEditFormMilkBinding
import com.leiteup.model.Milking

class EditFormMilkFragment : Fragment() {

    private var _binding: FragmentEditFormMilkBinding? = null
    private val binding get() = _binding!!

    private lateinit var milking: Milking
    private lateinit var cowController: CowController
    private lateinit var milkingController: MilkingController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            milking = EditFormMilkFragmentArgs.fromBundle(it).milking
        }
        cowController = CowController()
        milkingController = MilkingController()
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

        binding.btnDeleteMilking.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    private fun validateMilking() {
        val newCowName = binding.edtCowName.text.toString().trim().uppercase()
        var newQuantity = -1.0
        if(binding.edtQuantity.text.isNotEmpty()) {
            newQuantity = binding.edtQuantity.text.toString().trim().toDouble()
        }
        if (newCowName.isNotEmpty()) {
            if(newQuantity != -1.0) {
                cowController.cowExists(newCowName, { exists ->
                    if (exists) {
                        updateMilking(newCowName, newQuantity)
                        Toast.makeText(requireContext(), "Animal atualizado com sucesso.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Não existe nenhuma animal com esse nome, digite um animal válido", Toast.LENGTH_SHORT).show()
                    }
                }, { error ->
                    Toast.makeText(requireContext(), "o.", Toast.LENGTH_SHORT).show()
                })
            } else {
                Toast.makeText(requireContext(), "O campo quantidade não pode estar em branco.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "O campo nome não pode estar em branco.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateMilking(newCowName: String, newQuantity: Double) {
        milkingController.updateMilking(
            milking.id, // O ID da ordenha a ser atualizado
            newCowName,
            newQuantity,
            onSuccess = {
                findNavController().popBackStack()
                Toast.makeText(requireContext(), "Ordenha atualizada com sucesso", Toast.LENGTH_SHORT).show()
            },
            onError = { errorMessage ->
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Excluir Ordenha")
            .setMessage("Tem certeza que deseja excluir esta ordenha?")
            .setPositiveButton("Sim") { _, _ ->
                milkingController.deleteMilking(
                    milking.id,
                    onSuccess = {
                        findNavController().popBackStack()
                        Toast.makeText(requireContext(), "Ordenha deletada com sucesso", Toast.LENGTH_SHORT).show()
                    },
                    onError = { errorMessage ->
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                    }
                )
            }
            .setNegativeButton("Não", null)
            .show()
    }
}