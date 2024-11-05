package com.leiteup.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.storage.FirebaseStorage
import com.leiteup.R
import com.leiteup.controller.CowController
import com.leiteup.controller.MilkingController
import com.leiteup.databinding.FragmentCowDetailBinding
import com.leiteup.model.Cow

class CowDetail : Fragment() {

    private var _binding: FragmentCowDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var cow: Cow

    private lateinit var milkingController: MilkingController
    private lateinit var cowController: CowController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            cow = CowDetailArgs.fromBundle(it).cow
        }
        milkingController = MilkingController()
        cowController = CowController()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCowDetailBinding.inflate(inflater, container, false)
        return binding.root
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
    }

    private fun updateUI() {

        val linearLayout = view?.findViewById<LinearLayout>(R.id.textEarring)
        if(!cow.hideEarring) {
            linearLayout?.visibility = View.VISIBLE
            view?.findViewById<TextView>(R.id.setEarring)?.text = cow.earring.toString()
        } else {
            linearLayout?.visibility = View.GONE
        }

        view?.findViewById<TextView>(R.id.setName)?.text = cow.name

        if(cow.imageUrl.isNotEmpty()) {
            FirebaseStorage.getInstance().getReferenceFromUrl(cow.imageUrl)
                .downloadUrl
                .addOnSuccessListener { uri ->
                    Glide.with(this)
                        .load(uri.toString()) // Use a URL de download obtida
                        .into(binding.imageDetail)
                }
                .addOnFailureListener { exception ->
                    Log.e("FirebaseStorage", "Erro ao carregar a imagem", exception)
                }
        } else {
            binding.imageDetail.setBackgroundResource(R.drawable.img)
        }
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
                if(cow.imageUrl.isNotEmpty()) {
                    val imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(cow.imageUrl)
                    imageRef.delete()
                        .addOnSuccessListener {
                            cowController.deleteCow(
                                cow.id,
                                onSuccess = {
                                    findNavController().popBackStack()
                                    Toast.makeText(
                                        requireContext(),
                                        "Animal deletado com sucesso",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                onError = { errorMessage ->
                                    Toast.makeText(
                                        requireContext(),
                                        errorMessage,
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                            )
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(
                                requireContext(),
                                "Erro ao excluir a imagem: ${exception.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                } else {
                    cowController.deleteCow(
                        cow.id,
                        onSuccess = {
                            findNavController().popBackStack()
                            Toast.makeText(
                                requireContext(),
                                "Animal deletado com sucesso",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        onError = { errorMessage ->
                            Toast.makeText(
                                requireContext(),
                                errorMessage,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    )
                }
            }
            .setNegativeButton("Não", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
