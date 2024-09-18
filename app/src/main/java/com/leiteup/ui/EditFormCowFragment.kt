package com.leiteup.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.leiteup.R
import com.leiteup.controller.CowController
import com.leiteup.databinding.FragmentEditFormCowBinding
import com.leiteup.model.Cow
import java.time.format.DateTimeFormatter

class EditFormCowFragment : Fragment() {

    private var _binding: FragmentEditFormCowBinding? = null
    private val binding get() = _binding!!

    private lateinit var cowController: CowController

    private lateinit var cow: Cow

    private var isValid = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            cow = EditFormCowFragmentArgs.fromBundle(it).cow
        }
        cowController = CowController()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditFormCowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fillFormWithCowData()
        initClicks()
    }

    private fun fillFormWithCowData() {

        binding.edtEarring.setText(cow.earring.toString())
        binding.edtName.setText(cow.name)
        binding.edtBreed.setText(cow.breed)
        binding.edtWeight.setText(cow.weight.toString())
        binding.edtDate.setText(cow.birthDay)
        binding.edtFather.setText(cow.father)
        binding.edtMother.setText(cow.mother)

        when (cow.gender) {
            "Macho" -> binding.btnMale.isChecked = true
            else -> binding.btnFemale.isChecked = true
        }

        if (cow.isIATF) {
            binding.btnYes.isChecked = true
        } else {
            binding.btnNo.isChecked = true
        }
    }


    private fun initClicks() {
        view?.findViewById<Button>(R.id.btnAddCow)?.setOnClickListener {
            validadeEarring()
        }
    }

    private fun validadeEarring() {
        val newEarringText = binding.edtEarring.text.toString().trim()
        val newCowName = binding.edtName.text.toString().uppercase()
        if(newEarringText.isNotEmpty()) {
            if(newEarringText.toInt() != cow.earring) {
                cowController.cowExistsByEarring(newEarringText.toInt(), { exists ->
                    if (exists) {
                        binding.edtEarring.setBackgroundResource(R.drawable.bg_input_error)
                        isValid = false
                        Toast.makeText(requireContext(), "Brinco já cadastrado, insira um brinco válido.", Toast.LENGTH_SHORT).show()
                        binding.root.scrollTo(0, 0)
                    } else {
                        isValid = true
                        binding.edtEarring.setBackgroundResource(R.drawable.bg_input_text)
                        validateCow(newEarringText, newCowName)
                    }
                }, { error ->
                    Toast.makeText(requireContext(), "o.", Toast.LENGTH_SHORT).show()
                })
            } else {
                isValid = true
                binding.edtEarring.setBackgroundResource(R.drawable.bg_input_text)
                validateCow(newEarringText, newCowName)
            }
        } else {
            isValid = false
            binding.edtEarring.setBackgroundResource(R.drawable.bg_input_error)
            Toast.makeText(requireContext(), "O campo brinco não pode estar em branco.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateCow(newEarringText: String, newCowName: String) {
        if(newCowName.isNotEmpty()) {
            if(newCowName.uppercase() != cow.name) {
                cowController.cowExists(newCowName, { exists ->
                    if (exists) {
                        binding.edtName.setBackgroundResource(R.drawable.bg_input_error)
                        isValid = false
                        Toast.makeText(requireContext(), "Animal já cadastrado, insira um animal válido.", Toast.LENGTH_SHORT).show()
                        binding.root.scrollTo(0, 0)
                    } else {
                        isValid = true
                        binding.edtName.setBackgroundResource(R.drawable.bg_input_text)
                        proceedWithValidation(newEarringText, newCowName)
                    }
                }, { error ->
                    Toast.makeText(requireContext(), "o.", Toast.LENGTH_SHORT).show()
                })
            } else {
                isValid = true
                binding.edtName.setBackgroundResource(R.drawable.bg_input_text)
                proceedWithValidation(newEarringText, newCowName)
            }
        } else {
            isValid = false
            binding.edtName.setBackgroundResource(R.drawable.bg_input_error)
            Toast.makeText(requireContext(), "O campo nome não pode estar em branco.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun proceedWithValidation(newEarringText: String, newCowName: String) {

        var newGender = when (binding.rGender.checkedRadioButtonId) {
            R.id.btnMale -> "Macho"
            R.id.btnFemale -> "Fêmea"
            else -> ""
        }
        val newBreed = binding.edtBreed.text.toString().trim()
        var newWeight = binding.edtWeight.text.toString().trim().toInt()


        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        var birthDay = binding.edtDate.text.toString().trim()
        val newBirthDayFormatter = birthDay.format(formatter)
        var newIatf = when (binding.isIATF.checkedRadioButtonId) {
            R.id.btnYes -> true
            R.id.btnNo -> false
            else -> true
        }
        var newFather = binding.edtFather.text.toString().trim()
        var newMother = binding.edtMother.text.toString().trim()
        Log.e("COW_ID", "ID COW" + cow.id)


        if (!isValid) {
            // Toast.makeText(requireContext(), "Por favor, preencha todos os campos obrigatórios.", Toast.LENGTH_SHORT).show()
            binding.root.scrollTo(0,0)
            Log.i("COW_VALIDATE", "CHAMOU: ")
            return
        } else {
            val updatedCow = Cow(
                id = cow.id,
                earring = newEarringText.toInt(),
                name = newCowName,
                gender = newGender,
                breed = newBreed,
                weight = newWeight,
                birthDay = newBirthDayFormatter,
                isIATF = newIatf,
                father = newFather,
                mother = newMother
            )

            cowController.updateCowAndMilkings(
                oldCowName = cow.name,
                updatedCow = updatedCow, // O objeto Cow atualizado
                onSuccess = {
                    findNavController().previousBackStackEntry?.savedStateHandle?.set("updatedCow", updatedCow)
                    findNavController().popBackStack()
                    Toast.makeText(requireContext(), "Animal atualizado com sucesso.", Toast.LENGTH_SHORT).show()
                },
                onError = { errorMessage ->
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}