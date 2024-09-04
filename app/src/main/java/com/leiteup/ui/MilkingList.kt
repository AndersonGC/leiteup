package com.leiteup.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.leiteup.adapter.MilkingAdapter
import com.leiteup.controller.MilkingController
import com.leiteup.databinding.FragmentMilkingListBinding
import com.leiteup.model.Cow
import com.leiteup.model.Milking


class MilkingList : Fragment() {

    private var _binding: FragmentMilkingListBinding? = null
    private val binding get() = _binding!!

    private lateinit var milkingAdapter: MilkingAdapter
    private lateinit var cow: Cow

    private lateinit var milkingController: MilkingController

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        arguments?.let {
            cow = MilkingListArgs.fromBundle(it).cow
        }

        milkingController = MilkingController(
            onMilkingDataReceived = { milkingList ->
                initAdapter(milkingList)
            },
            onError = { errorMessage ->
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMilkingListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initClicks()
        getMilkings()
    }

    private fun initClicks() {

    }

    private fun getMilkings() {
        milkingController.fetchMilkingsWithCowName(cow.name)
    }

    private fun initAdapter(milkingList: List<Milking>) {
        if (_binding == null) {
            return
        }
        binding.milkingList.layoutManager = LinearLayoutManager(requireContext())
        binding.milkingList.setHasFixedSize(true)

        milkingAdapter  = MilkingAdapter(requireContext(), milkingList) {milking ->

        }
        binding.milkingList.adapter = milkingAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}