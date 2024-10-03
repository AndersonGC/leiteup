package com.leiteup.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.leiteup.R
import com.leiteup.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

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