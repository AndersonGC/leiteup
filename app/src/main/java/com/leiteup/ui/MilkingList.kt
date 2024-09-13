package com.leiteup.ui


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.leiteup.R
import com.leiteup.adapter.MilkingAdapter
import com.leiteup.controller.MilkingController
import com.leiteup.databinding.FragmentMilkingListBinding
import com.leiteup.model.Cow
import com.leiteup.model.Milking
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class MilkingList : Fragment() {

    private var _binding: FragmentMilkingListBinding? = null
    private val binding get() = _binding!!

    private lateinit var milkingAdapter: MilkingAdapter
    private lateinit var cow: Cow

    private lateinit var milkingController: MilkingController

    private var milkingList = listOf<Milking>()
    private val filteredMilkList = mutableListOf<Milking>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        arguments?.let {
            cow = MilkingListArgs.fromBundle(it).cow
        }

        milkingController = MilkingController(
            onMilkingDataReceived = { milkingList ->
                this.milkingList = milkingList
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
        binding.edtSearch.setOnClickListener() {
            showDatePickerDialog()
        }

        binding.btnSearch.setOnClickListener() {
            filterMilks()
        }
    }

    private fun getMilkings() {
        milkingController.fetchMilkingsWithCowName(cow.name)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun filterMilks() {
        val searchText = binding.edtSearch.text.toString().trim()
        filteredMilkList.clear()
        if(searchText.isEmpty()) {
            filteredMilkList.addAll(milkingList)
        } else {
            for (milk in milkingList) {
                if(milk.date.contains(searchText)) {
                    filteredMilkList.add(milk)
                }
            }
        }
        binding.edtSearch.setText("")
        milkingAdapter.updateList(filteredMilkList)
        milkingAdapter.notifyDataSetChanged()
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
                binding.edtSearch.setText(sdf.format(date.time))
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
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