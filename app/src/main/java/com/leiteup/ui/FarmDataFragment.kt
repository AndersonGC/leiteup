package com.leiteup.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.leiteup.controller.CowController
import com.leiteup.controller.MilkingController
import com.leiteup.databinding.FragmentFarmDataBinding
import com.leiteup.helper.FirebaseHelper
import com.leiteup.model.Cow

class FarmDataFragment : Fragment() {

    private var _binding: FragmentFarmDataBinding? = null
    private val binding get() = _binding!!

    private val cowList = mutableListOf<Cow>()
    private lateinit var cowController: CowController
    private lateinit var milkingController: MilkingController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFarmDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initController()
        fetchYesterdayMilkProduction()
        fetchCows()
        bestAndWorstCow()
    }

    private fun initController() {
        cowController = CowController(
            onCowsReceived = { cows ->
                cowList.clear()
                cowList.addAll(cows)
                binding.setAnimalQuantity.setText(cowList.size.toString())
            },
            onError = { errorMessage ->
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        )

        milkingController = MilkingController()
    }

    private fun fetchCows() {

        val userId = FirebaseHelper.getIdUser()
        if (!userId.isNullOrEmpty()) {
            cowController.fetchCows(userId)
        } else {
            Toast.makeText(requireContext(), "Usuário não encontrado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun bestAndWorstCow() {
        cowController.getCowWithHighestMilkAverage(FirebaseHelper.getIdUser() ?: "", { cowWithHighestAverage ->
            if (cowWithHighestAverage != null) {
                binding.setBestCow.setText(cowWithHighestAverage.name.toString())
                Log.d("COW_HIGHEST_AVG", "A vaca com a maior média de leite é: ${cowWithHighestAverage.name}")
            } else {
                Log.d("COW_HIGHEST_AVG", "Nenhuma vaca tem produção de leite nos últimos 7 dias.")
            }
        }, { error ->
            Log.e("COW_HIGHEST_AVG", "Erro ao calcular a vaca com a maior média: $error")
        })

        cowController.getCowWithLowestMilkAverage(FirebaseHelper.getIdUser() ?: "", { cowWithLowestAverage ->
            if (cowWithLowestAverage != null) {
                binding.setWorstCow.setText(cowWithLowestAverage.name.toString())
                Log.d("COW_HIGHEST_AVG", "A vaca com a maior média de leite é: ${cowWithLowestAverage.name}")
            } else {
                Log.d("COW_HIGHEST_AVG", "Nenhuma vaca tem produção de leite nos últimos 7 dias.")
            }
        }, { error ->
            Log.e("COW_HIGHEST_AVG", "Erro ao calcular a vaca com a maior média: $error")
        })
    }

    private fun fetchYesterdayMilkProduction() {
        milkingController.getTotalMilkingsFromYesterday(
            onResult = { totalMilk ->
                // Atualiza o campo setDailyProduction com o total de leite produzido ontem
                binding.setDailyProduction.text = totalMilk.toString() + " Litros"
            },
            onError = { errorMessage ->
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}