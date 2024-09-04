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
import com.leiteup.controller.MilkingController
import com.leiteup.helper.FirebaseHelper
import com.leiteup.model.Cow

class CowDetail : Fragment() {

    private lateinit var cow: Cow

    private lateinit var milkingController: MilkingController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            cow = CowDetailArgs.fromBundle(it).cow
        }
        milkingController = MilkingController()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cow_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Cow>("updatedCow")
            ?.observe(viewLifecycleOwner) { updatedCow ->
                cow = updatedCow
                updateUI()
            }

        updateUI()
        initClicks()
       // fetchMilkData()
    }

    private fun updateUI() {

        view?.findViewById<TextView>(R.id.setEarring)?.text = cow.earring.toString()
        view?.findViewById<TextView>(R.id.setName)?.text = cow.name
        milkingController.getAverageMilkingsLast7Days(cow.name,
            onResult = { average ->
                view?.findViewById<TextView>(R.id.setMilk)?.let { textView ->
                    textView.text = String.format("%.2f Litros", average)
                }
                Log.d("MilkingAverage", "Média dos últimos 7 dias: $average")
            },
            onError = { errorMessage ->
                Log.e("MilkingAverageError", errorMessage)
            }
        )
        view?.findViewById<TextView>(R.id.setGender)?.text = cow.gender
        view?.findViewById<TextView>(R.id.setBreed)?.text = cow.breed
        view?.findViewById<TextView>(R.id.setWeight)?.text = cow.weight.toString() + " Kilos"
        view?.findViewById<TextView>(R.id.setBirthDay)?.text = cow.birthDay
        view?.findViewById<TextView>(R.id.setIatf)?.text = if (cow.isIATF) "Sim" else "Não"
        view?.findViewById<TextView>(R.id.setFather)?.text = cow.father
        view?.findViewById<TextView>(R.id.setMother)?.text = cow.mother
    }

    private fun initClicks() {
        view?.findViewById<FloatingActionButton>(R.id.deleteCow)?.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        view?.findViewById<FloatingActionButton>(R.id.editCow)?.setOnClickListener {
            val action = CowDetailDirections.actionCowDetailToEditFormCowFragment(cow)
            findNavController().navigate(action)
        }

        view?.findViewById<FloatingActionButton>(R.id.milkingCow)?.setOnClickListener {
            val action = CowDetailDirections.actionCowDetailToMilkingList(cow)
            findNavController().navigate(action)
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
