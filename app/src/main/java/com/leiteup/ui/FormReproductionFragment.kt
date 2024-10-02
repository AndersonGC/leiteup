package com.leiteup.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.leiteup.R
import com.leiteup.controller.CowController
import com.leiteup.databinding.FragmentFormReproductionBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FormReproductionFragment : Fragment() {

    private var _binding: FragmentFormReproductionBinding? = null
    private val binding get() = _binding!!

    private lateinit var cowController: CowController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cowController = CowController()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFormReproductionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
    }

    private fun initListeners() {

        binding.edtDatePregnant.setOnClickListener() {
            showDatePickerDialog()
        }
        binding.btnAddPregnant.setOnClickListener() {
            validatePregnant()
        }
    }

    private fun validatePregnant() {
        val cowName = binding.edtCowName.text.toString().trim().uppercase()
        var datePregnant = binding.edtDatePregnant.text.toString().trim()

        if (cowName.isNotEmpty()) {
            if(datePregnant.isNotEmpty()) {
                cowController.cowExists(cowName, { exists ->
                    if (exists) {
                        cowController.updateCowWithPragnant(cowName, datePregnant,
                            onSuccess = {
                                findNavController().popBackStack()
                                Toast.makeText(requireContext(), "Animal salvo com sucesso.", Toast.LENGTH_SHORT).show()
                            },
                            onError = { errorMessage ->
                                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                            }
                        )
                    } else {
                        binding.edtCowName.setBackgroundResource(R.drawable.bg_input_error)
                        binding.edtDatePregnant.setBackgroundResource(R.drawable.bg_input_text)
                        Toast.makeText(requireContext(), "Não existe nenhuma animal com esse nome, digite um animal válido", Toast.LENGTH_SHORT).show()
                    }
                }, { error ->
                    Toast.makeText(requireContext(), "o.", Toast.LENGTH_SHORT).show()
                })
            } else {
                binding.edtCowName.setBackgroundResource(R.drawable.bg_input_text)
                binding.edtDatePregnant.setBackgroundResource(R.drawable.bg_input_error)
                Toast.makeText(requireContext(), "O campo data não pode estar em branco.", Toast.LENGTH_SHORT).show()
            }
        } else {
            binding.edtCowName.setBackgroundResource(R.drawable.bg_input_error)
            Toast.makeText(requireContext(), "O campo nome não pode estar em branco.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            R.style.CustomDatePickerTheme,
            { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                // Define o formato da data
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val date = Calendar.getInstance()
                date.set(selectedYear, selectedMonth, selectedDay)
                binding.edtDatePregnant.setText(sdf.format(date.time))
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}