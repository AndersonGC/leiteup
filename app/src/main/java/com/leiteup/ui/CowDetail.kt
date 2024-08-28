package com.leiteup.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.leiteup.R
import com.leiteup.helper.FirebaseHelper
import com.leiteup.model.Cow

class CowDetail : Fragment() {

    private lateinit var cow: Cow

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // Usa CowDetailArgs para obter o objeto Cow
            cow = CowDetailArgs.fromBundle(it).cow
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla o layout para este fragmento
        return inflater.inflate(R.layout.fragment_cow_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        view.findViewById<TextView>(R.id.setEarring).text = cow.earring.toString()
        view.findViewById<TextView>(R.id.setName).text = cow.name
        view.findViewById<TextView>(R.id.setGender).text = cow.gender
        view.findViewById<TextView>(R.id.setBreed).text = cow.breed
        view.findViewById<TextView>(R.id.setWeight).text = cow.weight.toString() + " Kilos"
        view.findViewById<TextView>(R.id.setBirthDay).text = cow.birthDay
        view.findViewById<TextView>(R.id.setIatf).text = if (cow.isIATF) "Sim" else "Não"
        view.findViewById<TextView>(R.id.setFather).text = cow.father
        view.findViewById<TextView>(R.id.setMother).text = cow.mother

        initClicks()
       // fetchMilkData()
    }

    private fun initClicks() {
        view?.findViewById<FloatingActionButton>(R.id.deleteCow)?.setOnClickListener {
            // Confirmação de exclusão
            showDeleteConfirmationDialog()
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Excluir Animal")
            .setMessage("Tem certeza que deseja excluir este animal?")
            .setPositiveButton("Sim") { _, _ ->
                deleteCow()
            }
            .setNegativeButton("Não", null)
            .show()
    }

    private fun deleteCow() {
        FirebaseHelper
            .getDatabase()
            .child("cow")
            .child(FirebaseHelper.getIdUser() ?: " ")
            .child(cow.id)
            .removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    findNavController().popBackStack()
                    Toast.makeText(requireContext(), "Animal deletado com sucesso.", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("COW_ERROR", "Erro ao deletar animal: ${task.exception?.message}")
                    Toast.makeText(requireContext(), "Erro ao deletar animal.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Log.e("COW_ERROR", "Erro ao deletar animal: ${it.message}")
                Toast.makeText(requireContext(), "Erro ao deletar animal.", Toast.LENGTH_SHORT).show()
            }
    }



}
