package com.leiteup.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.leiteup.R
import com.leiteup.controller.CowController
import com.leiteup.databinding.FragmentDetailReproductionBinding
import com.leiteup.model.Cow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DetailReproductionFragment : Fragment() {

    private var _binding: FragmentDetailReproductionBinding? = null
    private val binding get() = _binding!!

    private lateinit var cowPregnant: Cow
    private lateinit var cowController: CowController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
           cowPregnant = DetailReproductionFragmentArgs.fromBundle(it).cow
        }

        cowController = CowController()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailReproductionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateUI()
        initClicks()
    }

    private fun initClicks() {
        binding.btnNascimento.setOnClickListener {
            val cowName = cowPregnant.name
            val pregnantDate = ""
            cowController.updateCowWithPragnant(cowName, pregnantDate, false,
                onSuccess = {
                    findNavController().popBackStack()
                    Toast.makeText(requireContext(), "Parabéns, o bezerro nasceu!.", Toast.LENGTH_SHORT).show()
                },
                onError = { errorMessage ->
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun updateUI(){

        if(cowPregnant.imageUrl.isNotEmpty()) {
            FirebaseStorage.getInstance().getReferenceFromUrl(cowPregnant.imageUrl)
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
            binding.imageDetail.setImageResource(R.drawable.img)
        }
        binding.setName.setText(cowPregnant.name)
        binding.setEarring.setText(cowPregnant.earring.toString())
        binding.setPregnantDate.setText(cowPregnant.pregnantDate)

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        try {
            val pregnantDate = dateFormat.parse(cowPregnant.pregnantDate)

            val calendar = Calendar.getInstance()
            calendar.time = pregnantDate

            calendar.add(Calendar.DAY_OF_YEAR, 285)
            val childbirth = dateFormat.format(calendar.time)
            binding.setChildbirth.setText(childbirth)

            calendar.time = pregnantDate
            calendar.add(Calendar.DAY_OF_YEAR, 225)
            val secagemDate = dateFormat.format(calendar.time)
            binding.setSecagem.setText(secagemDate)

            calendar.time = pregnantDate
            calendar.add(Calendar.DAY_OF_YEAR, 255)
            val confinamentoDate = dateFormat.format(calendar.time)
            binding.setConfinamento.setText(confinamentoDate)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}