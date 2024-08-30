package com.leiteup.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.leiteup.R
import com.leiteup.databinding.FragmentEditFormCowBinding
import com.leiteup.helper.FirebaseHelper
import com.leiteup.model.Cow
import java.time.format.DateTimeFormatter

class EditFormCowFragment : Fragment() {

    private var _binding: FragmentEditFormCowBinding? = null
    private val binding get() = _binding!!

    private lateinit var cow: Cow

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            cow = EditFormCowFragmentArgs.fromBundle(it).cow
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentEditFormCowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.edtEarring).text = cow.earring.toString()
        view.findViewById<TextView>(R.id.edtName).text = cow.name
        if(cow.gender == "Macho") {
            view.findViewById<RadioButton>(R.id.btnMale).isChecked = true
        } else {
            view.findViewById<RadioButton>(R.id.btnFemale).isChecked = true
        }
        view.findViewById<TextView>(R.id.edtBreed).text = cow.breed

        view.findViewById<TextView>(R.id.edtWeight).text = cow.weight.toString()
        view.findViewById<TextView>(R.id.edtDate).text = cow.birthDay
        if(cow.isIATF) {
            view.findViewById<RadioButton>(R.id.btnYes).isChecked = true
        } else {
            view.findViewById<RadioButton>(R.id.btnNo).isChecked = true
        }
        view.findViewById<TextView>(R.id.edtFather).text = cow.father
        view.findViewById<TextView>(R.id.edtMother).text = cow.mother

        initClicks()
    }

    private fun initClicks() {
        view?.findViewById<Button>(R.id.btnAddCow)?.setOnClickListener {
            validadeCow()
        }
    }

    private fun validadeCow() {
        val newEarring = binding.edtEarring.text.toString().trim().toInt()
        val newName = binding.edtName.text.toString().trim()
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
        val updatedCow = Cow(
            id = cow.id,
            earring = newEarring,
            name = newName,
            gender = newGender,
            breed = newBreed,
            weight = newWeight,
            birthDay = newBirthDayFormatter,
            isIATF = newIatf,
            father = newFather,
            mother = newMother
        )
        updateCow(updatedCow)
    }

    private fun updateCow(updatedCow: Cow) {
        FirebaseHelper
            .getDatabase()
            .child("cow")
            .child(FirebaseHelper.getIdUser() ?: " ")
            .child(updatedCow.id)
            .setValue(updatedCow)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    findNavController().previousBackStackEntry?.savedStateHandle?.set("updatedCow", updatedCow)
                    findNavController().popBackStack()
                    Toast.makeText(requireContext(), "Animal atualizado com sucesso.", Toast.LENGTH_SHORT).show()
                    Log.e("COW_ID", "ID UPDATE" + updatedCow.id)
                } else {
                    Log.e("COW_ERROR", "Erro ao atualizar animal: ${task.exception?.message}")
                    Toast.makeText(requireContext(), "Erro ao atualizar animal.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Log.e("COW_ERROR", "Erro ao atualizar animal: ${it.message}")
                Toast.makeText(requireContext(), "Erro ao atualizar animal.", Toast.LENGTH_SHORT).show()
            }
    }

}