package com.leiteup.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.leiteup.R
import com.leiteup.controller.CowController
import com.leiteup.controller.MilkingController
import com.leiteup.databinding.FragmentHomeBinding
import com.leiteup.helper.FirebaseHelper

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var cowController: CowController
    private lateinit var milkingController: MilkingController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        milkingController = MilkingController()
        cowController = CowController()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initClicks()
        updateUI()
    }

    private fun updateUI() {
        cowController.fetchCows(FirebaseHelper.getIdUser() ?: " ")
    }

    private fun initClicks() {
        binding.btnCow.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_cowFragment)
        }
        binding.btnToMilk.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_milkingFragment)
        }
        binding.btnFood.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_foodListFragment)
        }
        binding.btnReports.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_farmDataFragment)
        }
        binding.btnToReproduction.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_reproductionFragment)
        }
        binding.btnHelp.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_helpFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}