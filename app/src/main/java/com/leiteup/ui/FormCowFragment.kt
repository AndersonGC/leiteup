package com.leiteup.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.leiteup.R
import com.leiteup.databinding.FragmentFormCowBinding
import com.leiteup.helper.FirebaseHelper
import com.leiteup.model.Cow
import java.time.format.DateTimeFormatter

class FormCowFragment : Fragment() {

    private var _binding: FragmentFormCowBinding? = null
    private val binding get() = _binding!!

    private lateinit var cow: Cow
    private var newCow: Boolean = true

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

        binding.btnAddCow.setOnClickListener() { validateCow() }


    }

    private fun validateCow() {

        val earring = binding.edtEarring.text.toString().trim().toInt()
        val cowName = binding.edtName.text.toString().trim().uppercase()
        var cowGender = when (binding.rGender.checkedRadioButtonId) {
            R.id.btnMale -> "Macho"
            R.id.btnFemale -> "Fêmea"
            else -> ""
        }
        val breed = binding.edtBreed.text.toString().trim()
        var weight = binding.edtWeight.text.toString().trim().toInt()


        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        var birthDay = binding.edtDate.text.toString().trim()
        val birthDayFormatter = birthDay.format(formatter)
        var isIATF = when (binding.isIATF.checkedRadioButtonId) {
            R.id.btnYes -> true
            R.id.btnNo -> false
            else -> true
        }
        var father = binding.edtFather.text.toString().trim()
        var mother = binding.edtMother.text.toString().trim()

        if(newCow) cow = Cow()
        cow.breed = breed
        cow.earring = earring
        cow.gender = cowGender
        cow.name = cowName
        cow.weight = weight
        cow.birthDay = birthDayFormatter
        cow.isIATF = isIATF
        cow.father = father
        cow.mother = mother

        Log.i("COW_ERROR", cowName + cow)

        saveCow()
    }

    private fun saveCow() {
        FirebaseHelper
            .getDatabase()
            .child("cow")
            .child(FirebaseHelper.getIdUser() ?: " ")
            .child(cow.id)
            .setValue(cow)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    if(newCow) {
                        findNavController().popBackStack()
                        Log.i("COW_ERROR", "CowSucesso: ${task.exception?.message}")
                        Toast.makeText(requireContext(), "Animal salvo com sucesso.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.i("COW_ERROR", "CowErro1: ${task.exception?.message}")
                    Toast.makeText(requireContext(), "Erro ao salvar animal1.", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Erro ao salvar animal2.", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}