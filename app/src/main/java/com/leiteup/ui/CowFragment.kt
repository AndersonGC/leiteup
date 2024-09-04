package com.leiteup.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.leiteup.R
import com.leiteup.adapter.CowAdapter
import com.leiteup.controller.CowController
import com.leiteup.databinding.FragmentCowBinding
import com.leiteup.helper.FirebaseHelper
import com.leiteup.model.Cow


class CowFragment : Fragment() {

    private var _binding: FragmentCowBinding? = null
    private val binding get() = _binding!!

    private lateinit var cowAdapter: CowAdapter
    private val cowList = mutableListOf<Cow>()
    private lateinit var cowController: CowController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initController()
        initClicks()
        fetchCows()
    }

    private fun initController() {
        cowController = CowController(
            onCowsReceived = { cows ->
                cowList.clear()
                cowList.addAll(cows)
                initAdapter()
            },
            onError = { errorMessage ->
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun initClicks() {
        binding.fabAddCow.setOnClickListener {
            findNavController().navigate(R.id.action_cowFragment_to_formCowFragment)
        }
    }

    private fun fetchCows() {

        val userId = FirebaseHelper.getIdUser()
        if (!userId.isNullOrEmpty()) {
            cowController.fetchCows(userId)
        } else {
            Toast.makeText(requireContext(), "Usuário não encontrado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initAdapter() {
        if (_binding == null) {
            return
        }
        binding.cowList.layoutManager = LinearLayoutManager(requireContext())
        binding.cowList.setHasFixedSize(true)

        cowAdapter = CowAdapter(requireContext(), cowList) {cow ->

        }
        binding.cowList.adapter = cowAdapter
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}