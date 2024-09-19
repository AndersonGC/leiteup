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
import com.leiteup.databinding.FragmentFormCowBinding
import com.leiteup.model.Cow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FormCowFragment : Fragment() {

    private var _binding: FragmentFormCowBinding? = null
    private val binding get() = _binding!!

    private lateinit var cow: Cow
    private var newCow: Boolean = true
    private var isValid = true

    private lateinit var cowController: CowController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cowController = CowController()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFormCowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
    }

    private fun initListeners() {
        binding.edtDate.setOnClickListener() {showDatePickerDialog()}
        binding.btnAddCow.setOnClickListener() { validadeEarring() }
    }

    private fun validadeEarring() {
        val earringText = binding.edtEarring.text.toString().trim()
        val cowName = binding.edtName.text.toString().uppercase()
        if(earringText.isNotEmpty()) {
            cowController.cowExistsByEarring(earringText.toInt(), { exists ->
                if (exists) {
                    binding.edtEarring.setBackgroundResource(R.drawable.bg_input_error)
                    isValid = false
                    Toast.makeText(requireContext(), "Brinco já cadastrado, insira um brinco válido.", Toast.LENGTH_SHORT).show()
                    binding.root.scrollTo(0, 0)
                } else {
                    isValid = true
                    binding.edtEarring.setBackgroundResource(R.drawable.bg_input_text)
                    validateCow(earringText, cowName)
                }
            }, { error ->
                Toast.makeText(requireContext(), "o.", Toast.LENGTH_SHORT).show()
            })
        } else {
            isValid = false
            binding.edtEarring.setBackgroundResource(R.drawable.bg_input_error)
            Toast.makeText(requireContext(), "O campo brinco não pode estar em branco.", Toast.LENGTH_SHORT).show()
            binding.root.scrollTo(0, 0)
        }
    }

    private fun validateCow(earringText: String, cowName: String) {
        if(cowName.isNotEmpty()) {
            cowController.cowExists(cowName, { exists ->
                if (exists) {
                    binding.edtName.setBackgroundResource(R.drawable.bg_input_error)
                    isValid = false
                    Toast.makeText(requireContext(), "Animal já cadastrado, insira um animal válido.", Toast.LENGTH_SHORT).show()
                    binding.root.scrollTo(0, 0)
                } else {
                    isValid = true
                    binding.edtName.setBackgroundResource(R.drawable.bg_input_text)
                    proceedWithValidation(earringText, cowName)
                }
            }, { error ->
                Toast.makeText(requireContext(), "o.", Toast.LENGTH_SHORT).show()
            })
        } else {
            isValid = false
            binding.edtName.setBackgroundResource(R.drawable.bg_input_error)
            Toast.makeText(requireContext(), "O campo nome não pode estar em branco.", Toast.LENGTH_SHORT).show()
            binding.root.scrollTo(0, 0)
        }
    }

    private fun proceedWithValidation(earringText: String, cowName: String) {

        var cowGender = when (binding.rGender.checkedRadioButtonId) {
            R.id.btnMale -> "Macho"
            R.id.btnFemale -> "Fêmea"
            else -> {
                isValid = false
                binding.rGender.setBackgroundResource(R.drawable.bg_input_error)
                Toast.makeText(requireContext(), "Insira o sexo do animal.", Toast.LENGTH_SHORT).show()
                binding.root.scrollTo(0, 0)
                return
            }
        }
        if(cowGender != null) {
            isValid = true
            binding.rGender.setBackgroundResource(R.drawable.bg_input_text)
        }

        val breed = binding.edtBreed.text.toString().trim()
        if(breed.isEmpty()) {
            isValid = false
            binding.edtBreed.setBackgroundResource(R.drawable.bg_input_error)
            Toast.makeText(requireContext(), "Insira a raça do animal.", Toast.LENGTH_SHORT).show()
            binding.root.scrollTo(0, 0)
            return
        } else {
            isValid = true
            binding.edtBreed.setBackgroundResource(R.drawable.bg_input_text)
        }

        var weightText = binding.edtWeight.text.toString().trim()
        if(weightText.isEmpty()) {
            isValid = false
            binding.edtWeight.setBackgroundResource(R.drawable.bg_input_error)
            Toast.makeText(requireContext(), "Insira o peso do animal.", Toast.LENGTH_SHORT).show()
            binding.root.scrollTo(0, 0)
            return
        } else {
            isValid = true
            binding.edtWeight.setBackgroundResource(R.drawable.bg_input_text)
        }

        var birthDay = binding.edtDate.text.toString().trim()
        if(birthDay.isEmpty()) {
            isValid = false
            binding.edtDate.setBackgroundResource(R.drawable.bg_input_error)
            Toast.makeText(requireContext(), "Insira a data de nascimento do animal.", Toast.LENGTH_SHORT).show()
            binding.root.scrollTo(0, 0)
            return
        } else {
            isValid = true
            binding.edtDate.setBackgroundResource(R.drawable.bg_input_text)
        }

        var isIATF = when (binding.isIATF.checkedRadioButtonId) {
            R.id.btnYes -> true
            R.id.btnNo -> false
            else -> {
                isValid = false
                binding.isIATF.setBackgroundResource(R.drawable.bg_input_error)
                Toast.makeText(requireContext(), "Insira a forma de nascimento do animal.", Toast.LENGTH_SHORT).show()
                binding.root.scrollTo(0, 0)
                return
            }
        }
        if(isIATF != null) {
            isValid = true
            binding.isIATF.setBackgroundResource(R.drawable.bg_input_text)
        }

        var father = binding.edtFather.text.toString().trim()
        var mother = binding.edtMother.text.toString().trim()

        if (!isValid) {
            return
        } else {
            if(newCow) cow = Cow()
            cow.breed = breed
            cow.earring = earringText.toInt()
            cow.gender = cowGender
            cow.name = cowName
            cow.weight = weightText.toInt()
            cow.birthDay = birthDay
            cow.isIATF = isIATF
            cow.father = father
            cow.mother = mother

            cowController.saveCow(cow, newCow, {
                findNavController().popBackStack()
                Toast.makeText(requireContext(), "Animal salvo com sucesso.", Toast.LENGTH_SHORT).show()
            }, { errorMessage ->
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            })
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
                binding.edtDate.setText(sdf.format(date.time))
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