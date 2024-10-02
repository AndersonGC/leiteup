package com.leiteup.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.leiteup.R
import com.leiteup.adapter.ReproductionAdapter
import com.leiteup.controller.CowController
import com.leiteup.databinding.FragmentReproductionBinding
import com.leiteup.model.Cow


class ReproductionFragment : Fragment() {

    private var _binding: FragmentReproductionBinding? = null
    private val binding get() = _binding!!

    private lateinit var reproductionAdapter: ReproductionAdapter
    private val cowPregnant = mutableListOf<Cow>()
    private val filteredReproductionList = mutableListOf<Cow>()
    private lateinit var cowController: CowController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReproductionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initController()
        initClicks()
        fetchPregnantCows()
    }

    private fun initController() {
        cowController = CowController()
    }

    private fun initClicks() {
        binding.fabAddReproduction.setOnClickListener {
            findNavController().navigate(R.id.action_reproductionFragment_to_formReproductionFragment)
        }

        binding.btnSearch.setOnClickListener {
            filterCows()
        }
    }

    private fun fetchPregnantCows() {
        cowController.fetchPregnantCows { cows ->
            cowPregnant.clear()
            cowPregnant.addAll(cows)
            initAdapter()
            if (cows.isEmpty()) {
                Toast.makeText(requireContext(), "Nenhuma vaca grávida encontrada", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun filterCows() {
        val searchText = binding.edtSearch.text.toString().trim().uppercase()
        filteredReproductionList.clear()
        if (searchText.isEmpty()) {
            filteredReproductionList.addAll(cowPregnant)
        } else {
            for (cow in cowPregnant) {
                if (cow.name.uppercase().contains(searchText)) {
                    filteredReproductionList.add(cow)
                }
            }
        }
        binding.edtSearch.setText("")
        reproductionAdapter.updateList(filteredReproductionList)
        reproductionAdapter.notifyDataSetChanged()
    }

    private fun initAdapter() {
        if (_binding == null) {
            return
        }
        binding.cowReproduction.layoutManager = LinearLayoutManager(requireContext())
        binding.cowReproduction.setHasFixedSize(true)

        reproductionAdapter = ReproductionAdapter(requireContext(), cowPregnant) {cow ->

        }
        binding.cowReproduction.adapter = reproductionAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}