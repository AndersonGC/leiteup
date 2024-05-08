package com.leiteup.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.leiteup.R
import com.leiteup.databinding.FragmentFormCowBinding
import com.leiteup.model.Cow

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
        binding.btnAddCow.setOnClickListener() {

            validateCow()
        }
    }

    private fun validateCow() {
        val cowName = binding.edtName.text.toString().trim()
        val earring = binding.edtEarring.text.toString().trim().toInt()

        if(newCow) cow = Cow()
        cow.name = cowName
        cow.earring = earring

        saveCow()
    }

    private fun saveCow() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}