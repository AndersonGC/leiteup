package com.leiteup.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.leiteup.R
import com.leiteup.databinding.FragmentEditFormMilkBinding
import com.leiteup.model.Milking

class EditFormMilkFragment : Fragment() {

    private var _binding: FragmentEditFormMilkBinding? = null
    private val binding get() = _binding!!

    private lateinit var milking: Milking

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            milking = EditFormMilkFragmentArgs.fromBundle(it).milking
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditFormMilkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.edtCowName).text = milking.cowName.toString()
        view.findViewById<TextView>(R.id.edtQuantity).text = milking.quantity.toString()
    }
}