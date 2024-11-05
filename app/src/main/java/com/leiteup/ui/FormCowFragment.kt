package com.leiteup.ui

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
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

    private var imageUri: Uri? = null // Armazena a URI da imagem
    var hideEarring = false

    private val getImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageUri = it
            binding.photoBar.progress = 0
            simulateProgress()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cowController = CowController()
        cow = Cow()
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
        binding.edtDate.setOnClickListener { showDatePickerDialog() }
        binding.btnAddCow.setOnClickListener { validadeEarring() }
        binding.btnTakePhoto.setOnClickListener { openGallery() }
    }

    private fun openGallery() {
        binding.photoBar.progress = 0
        binding.photoBar.progressDrawable.colorFilter = null
        getImage.launch("image/*")
    }

    private fun validadeEarring() {
        var earringText = binding.edtEarring.text.toString().trim()
        val cowName = binding.edtName.text.toString().uppercase()

        if (earringText.isEmpty()) {
            earringText = generateUniqueEarring()
        }

        cowController.cowExistsByEarring(earringText.toInt(), { exists, cow ->
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
            Toast.makeText(requireContext(), "Erro ao verificar brinco: $error", Toast.LENGTH_SHORT).show()
        })
    }

    private fun validateCow(earringText: String, cowName: String) {
        if (cowName.isNotEmpty()) {
            cowController.cowExists(cowName, { exists, cow ->
                if (exists && cow != null) {
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
                Toast.makeText(requireContext(), "Erro ao verificar animal: $error", Toast.LENGTH_SHORT).show()
            })
        } else {
            isValid = false
            binding.edtName.setBackgroundResource(R.drawable.bg_input_error)
            Toast.makeText(requireContext(), "O campo nome não pode estar em branco.", Toast.LENGTH_SHORT).show()
            binding.root.scrollTo(0, 0)
        }
    }

    private fun proceedWithValidation(earringText: String, cowName: String) {
        val cowGender = when (binding.rGender.checkedRadioButtonId) {
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

        val breed = binding.edtBreed.text.toString().trim()
        if (breed.isEmpty()) {
            isValid = false
            binding.edtBreed.setBackgroundResource(R.drawable.bg_input_error)
            Toast.makeText(requireContext(), "Insira a raça do animal.", Toast.LENGTH_SHORT).show()
            binding.root.scrollTo(0, 0)
            return
        } else {
            binding.edtBreed.setBackgroundResource(R.drawable.bg_input_text)
        }

        val weightText = binding.edtWeight.text.toString().trim()
        if (weightText.isEmpty()) {
            isValid = false
            binding.edtWeight.setBackgroundResource(R.drawable.bg_input_error)
            Toast.makeText(requireContext(), "Insira o peso do animal.", Toast.LENGTH_SHORT).show()
            binding.root.scrollTo(0, 0)
            return
        } else {
            binding.edtWeight.setBackgroundResource(R.drawable.bg_input_text)
        }

        val birthDay = binding.edtDate.text.toString().trim()
        if (birthDay.isEmpty()) {
            isValid = false
            binding.edtDate.setBackgroundResource(R.drawable.bg_input_error)
            Toast.makeText(requireContext(), "Insira a data de nascimento do animal.", Toast.LENGTH_SHORT).show()
            binding.root.scrollTo(0, 0)
            return
        } else {
            binding.edtDate.setBackgroundResource(R.drawable.bg_input_text)
        }

        val isIATF = when (binding.isIATF.checkedRadioButtonId) {
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

        var father = binding.edtFather.text.toString().trim()
        var mother = binding.edtMother.text.toString().trim()

        if (!isValid) {
            return
        } else {
            if (newCow) cow.breed = breed
            cow.earring = earringText.toInt()
            cow.gender = cowGender
            cow.name = cowName
            cow.weight = weightText.toInt()
            cow.birthDay = birthDay
            cow.isIATF = isIATF
            cow.father = father
            cow.mother = mother
            cow.hideEarring = hideEarring
            if(binding.photoBar.progress == 100) {
                imageUri?.let { saveCowAndUploadImage(cow, it) }
            } else {
                saveCow(cow)
            }

        }
    }

    private fun saveCowAndUploadImage(cow: Cow, imageUri: Uri) {
        val storageRef = FirebaseStorage.getInstance().reference
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val fileRef = storageRef.child("$userId/images/${System.currentTimeMillis()}.jpg")

        cow.imageUrl = fileRef.toString()

        fileRef.putFile(imageUri)
            .addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener { uri ->
                }
            }
            .addOnFailureListener { exception ->
            }

        saveCow(cow)
    }

    private fun saveCow(cow: Cow) {

        cowController.saveCow(cow, newCow, {
            findNavController().popBackStack()
            Toast.makeText(requireContext(), "Animal salvo com sucesso.", Toast.LENGTH_SHORT).show()
        }, { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        })
    }

    private fun generateUniqueEarring(): String {
        var newEarring: Int
        do {
            newEarring = (0..99999).random()
        } while (isEarringDuplicated(newEarring))
        hideEarring = true;
        return newEarring.toString()
    }

    private fun isEarringDuplicated(earring: Int): Boolean {
        var isDuplicated = false
        cowController.cowExistsByEarring(earring, { exists, cow ->
            isDuplicated = exists
        }, { error -> })
        return isDuplicated
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

    private fun simulateProgress() {
        val handler = android.os.Handler()
        var progress = 0

        handler.postDelayed(object : Runnable {
            override fun run() {
                if (progress < 100) {
                    progress += 5
                    binding.photoBar.progress = progress
                    handler.postDelayed(this, 50)
                } else {
                    binding.photoBar.progressDrawable.setColorFilter(
                        ContextCompat.getColor(requireContext(), R.color.blue_app),
                        android.graphics.PorterDuff.Mode.SRC_IN

                    )
                    Toast.makeText(requireContext(), "Foto carregada.", Toast.LENGTH_SHORT).show()
                }
            }
        }, 50)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
