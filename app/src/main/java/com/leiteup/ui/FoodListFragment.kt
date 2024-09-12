package com.leiteup.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.leiteup.adapter.FoodAdapter
import com.leiteup.controller.CowController
import com.leiteup.controller.MilkingController
import com.leiteup.databinding.FragmentFoodListBinding
import com.leiteup.helper.FirebaseHelper
import com.leiteup.model.Cow

class FoodListFragment : Fragment() {

    private var _binding: FragmentFoodListBinding? = null
    private val binding get() = _binding!!

    private lateinit var foodAdapter: FoodAdapter
    private lateinit var cowController: CowController
    private lateinit var milkingController: MilkingController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        cowController = CowController()
        milkingController = MilkingController()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFoodListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initClicks()
        fetchCowsAndUpdateList()
    }

    private fun initClicks() {

    }

    private fun fetchCowsAndUpdateList() {
        val userId = FirebaseHelper.getIdUser()
        if (userId != null) {
            cowController.calculateFood(userId, onResult = { resultList ->
                initAdapter(resultList)
            }, onError = { error ->
                println("Erro: $error")
            })
        }
    }

    private fun initAdapter(foodList: List<Pair<Cow, Double>>) {
        if(_binding == null) {
            return
        }

        binding.foodList.layoutManager = LinearLayoutManager(requireContext())
        binding.foodList.setHasFixedSize(true)

        foodAdapter  = FoodAdapter(foodList)
        binding.foodList.adapter = foodAdapter

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}