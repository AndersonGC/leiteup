package com.leiteup.ui

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.leiteup.R
import com.leiteup.controller.CowController
import com.leiteup.databinding.FragmentEditFormCowBinding
import com.leiteup.model.Cow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditFormCowFragment : Fragment() {

    private var _binding: FragmentEditFormCowBinding? = null
    private val binding get() = _binding!!

    private lateinit var cowController: CowController

    private lateinit var cow: Cow

    private var isValid = true
    private var imageUri: Uri? = null // Armazena a URI da imagem
    private val getImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageUri = it
            binding.edtBtnChoosePhoto.setText(imageUri.toString())
        }
    }

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
        binding.edtDate.setOnClickListener() {showDatePickerDialog()}
        binding.btnTakePhoto.setOnClickListener() {openGallery()}
    }

    private fun openGallery() {
        getImage.launch("image/*")
    }


    private fun validadeEarring() {
        val newEarringText = binding.edtEarring.text.toString().trim()
        val newCowName = binding.edtName.text.toString().uppercase()
        if(newEarringText.isNotEmpty()) {
            if(newEarringText.toInt() != cow.earring) {
                cowController.cowExistsByEarring(newEarringText.toInt(), { exists, cow ->
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
            binding.root.scrollTo(0, 0)
        }
    }

    private fun validateCow(newEarringText: String, newCowName: String) {
        if(newCowName.isNotEmpty()) {
            if(newCowName.uppercase() != cow.name) {
                cowController.cowExists(newCowName, { exists, cow ->
                    if (exists && cow != null) {
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
            binding.root.scrollTo(0, 0)
        }
    }

    private fun proceedWithValidation(newEarringText: String, newCowName: String) {

        var newGender = when (binding.rGender.checkedRadioButtonId) {
            R.id.btnMale -> "Macho"
            R.id.btnFemale -> "Fêmea"
            else -> ""
        }

        val newBreed = binding.edtBreed.text.toString().trim()
        if(newBreed.isEmpty()) {
            isValid = false
            binding.edtBreed.setBackgroundResource(R.drawable.bg_input_error)
            Toast.makeText(requireContext(), "O campo raça não pode estar em branco.", Toast.LENGTH_SHORT).show()
            binding.root.scrollTo(0, 0)
            return
        } else {
            isValid = true
            binding.edtBreed.setBackgroundResource(R.drawable.bg_input_text)
        }

        var newWeightText = binding.edtWeight.text.toString().trim()
        if(newWeightText.isEmpty()) {
            isValid = false
            binding.edtWeight.setBackgroundResource(R.drawable.bg_input_error)
            Toast.makeText(requireContext(), "O campo peso não pode estar em branco.", Toast.LENGTH_SHORT).show()
            binding.root.scrollTo(0, 0)
            return
        } else {
            isValid = true
            binding.edtWeight.setBackgroundResource(R.drawable.bg_input_text)
        }

        var newBirthDay = binding.edtDate.text.toString().trim()
        if(newBirthDay.isEmpty()) {
            isValid = false
            binding.edtDate.setBackgroundResource(R.drawable.bg_input_error)
            Toast.makeText(requireContext(), "O campo data de nascimento não pode estar em branco.", Toast.LENGTH_SHORT).show()
            binding.root.scrollTo(0, 0)
            return
        } else {
            isValid = true
            binding.edtDate.setBackgroundResource(R.drawable.bg_input_text)
        }
        var newIatf = when (binding.isIATF.checkedRadioButtonId) {
            R.id.btnYes -> true
            R.id.btnNo -> false
            else -> true
        }
        var newFather = binding.edtFather.text.toString().trim()
        var newMother = binding.edtMother.text.toString().trim()


        if (!isValid) {
            return
        } else {
            val updatedCow = Cow(
                id = cow.id,
                earring = newEarringText.toInt(),
                name = newCowName,
                gender = newGender,
                breed = newBreed,
                weight = newWeightText.toInt(),
                birthDay = newBirthDay,
                isIATF = newIatf,
                father = newFather,
                mother = newMother,
                pregnant = cow.pregnant,
                pregnantDate = cow.pregnantDate
            )
            if(imageUri == null) {
                updatedCow.imageUrl = cow.imageUrl
                updateCow(updatedCow)
            } else {
                imageUri?.let { editCowImage(updatedCow, it) }
            }

        }
    }

    private fun editCowImage(updatedCow: Cow, newImageUri: Uri) {
        if(cow.imageUrl.isEmpty()) {
            updateCowAndUploadImage(cow, newImageUri)
        } else {
            val oldImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(cow.imageUrl)
            oldImageRef.delete()
                .addOnSuccessListener {
                    updateCowAndUploadImage(updatedCow, newImageUri)
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(requireContext(), "Erro ao excluir a imagem antiga: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun updateCowAndUploadImage(updatedCow: Cow, imageUri: Uri) {
        val storageRef = FirebaseStorage.getInstance().reference
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val fileRef = storageRef.child("$userId/images/${System.currentTimeMillis()}.jpg")

        updatedCow.imageUrl = fileRef.toString()

        fileRef.putFile(imageUri)
            .addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener { uri ->
                }
            }
            .addOnFailureListener { exception ->
            }

        updateCow(updatedCow)
    }

    private fun updateCow(updatedCow: Cow) {
        cowController.updateCowAndMilkings(
            oldCowName = cow.name,
            oldCowEarring = cow.earring,
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