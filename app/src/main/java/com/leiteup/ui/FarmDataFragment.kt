package com.leiteup.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.leiteup.controller.CowController
import com.leiteup.controller.FarmController
import com.leiteup.controller.MilkingController
import com.leiteup.databinding.FragmentFarmDataBinding
import com.leiteup.helper.FirebaseHelper
import com.leiteup.model.Cow
import com.leiteup.model.Farm

class FarmDataFragment : Fragment() {

    private var _binding: FragmentFarmDataBinding? = null
    private val binding get() = _binding!!

    private val cowList = mutableListOf<Cow>()
    private val cowPregnant = mutableListOf<Cow>()
    private lateinit var farm: Farm
    private lateinit var cowController: CowController
    private lateinit var milkingController: MilkingController
    private lateinit var farmController: FarmController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFarmDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        farm = Farm()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(_binding != null) {
            initController()
            fetchMilkProductionToday()
            fetchMilkProductionWeekly()
            fetchMilkProductionMonthly()
            fetchCows()
            bestAndWorstCow()
            fetchPregnantCows()
        }

    }


    private fun saveFarm(farm: Farm) {
        farm.bestCow = "teste"
        farmController.saveData(farm, {

        }, { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        })

    }

    private fun fetchPregnantCows() {
        val userId = FirebaseHelper.getIdUser()
        if (!userId.isNullOrEmpty()) {
            cowController.fetchCows(userId)
            cowController.fetchPregnantCows(userId) { cows ->
                cowPregnant.clear()

                if (cows.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "Nenhuma vaca grávida encontrada",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                binding.setPregnant.setText(cows.size.toString())
            }
        } else {
            Toast.makeText(requireContext(), "Usuário não encontrado", Toast.LENGTH_SHORT).show()
        }
    }

            @SuppressLint("SetTextI18n")
    private fun initController() {
        Log.e("COW_FARM", "controller")
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
        farmController = FarmController()
    }

    private fun fetchCows() {
        Log.e("COW_FARM", "fetchCows")
        val userId = FirebaseHelper.getIdUser()
        if (!userId.isNullOrEmpty()) {
            cowController.fetchCows(userId)
        } else {
            Toast.makeText(requireContext(), "Usuário não encontrado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun bestAndWorstCow() {
        if(_binding != null) {
            Log.e("COW_FARM", "bestAndWorstCow")
            cowController.getCowWithHighestMilkAverage(
                FirebaseHelper.getIdUser() ?: "",
                { cowWithHighestAverage ->
                    if (cowWithHighestAverage != null) {
                        binding.setBestCow.setText(cowWithHighestAverage.name)
                        milkingController.getAverageMilkingsLast7Days(cowWithHighestAverage.name,
                            onResult = { average ->
                                binding.setAverageBestCow.setText(
                                    average.toInt().toString() + " Litros"
                                )
                            },
                            onError = { errorMessage ->
                                Log.e("MilkingAverageError", errorMessage)
                            }
                        )
                    } else {

                    }
                },
                { error ->
                    Log.e("COW_HIGHEST_AVG", "Erro ao calcular a vaca com a maior média: $error")
                })

            cowController.getCowWithLowestMilkAverage(
                FirebaseHelper.getIdUser() ?: "",
                { cowWithLowestAverage ->
                    if (cowWithLowestAverage != null) {
                        binding.setWorstCow.setText(cowWithLowestAverage.name)
                        milkingController.getAverageMilkingsLast7Days(cowWithLowestAverage.name,
                            onResult = { average ->
                                binding.setAverageWorstCow.setText(
                                    average.toInt().toString() + " Litros"
                                )
                            },
                            onError = { errorMessage ->
                                Log.e("MilkingAverageError", errorMessage)
                            }
                        )
                    } else {

                    }
                },
                { error ->
                    Log.e("COW_HIGHEST_AVG", "Erro ao calcular a vaca com a maior média: $error")
                })
        }
    }

    private fun fetchMilkProductionToday() {
        Log.e("COW_FARM", "1")
        milkingController.getTotalMilkingsForPeriod(
            days = 1,  // Passa 1 dia para pegar a produção de ontem
            onResult = { totalMilk ->
                binding.setTotalProductionToday.setText(totalMilk.toInt().toString() + " Litros")
            },
            onError = { errorMessage ->
                binding.setTotalProductionToday.setText("0")
            }
        )
    }

    private fun fetchMilkProductionWeekly() {
        Log.e("COW_FARM", "2")
        milkingController.getTotalMilkingsForPeriod(
            days = 7,  // Passa 1 dia para pegar a produção de ontem
            onResult = { totalMilk ->
                binding.setTotalWeeklyProduction.setText(totalMilk.toInt().toString() + " Litros")
            },
            onError = { errorMessage ->
                binding.setTotalWeeklyProduction.setText("0 Litros")
            }
        )
    }

    private fun fetchMilkProductionMonthly() {
        Log.e("COW_FARM", "3")
        milkingController.getTotalMilkingsForPeriod(
            days = 30,  // Passa 1 dia para pegar a produção de ontem
            onResult = { totalMilk ->
                binding.setTotalMonthlyProduction.setText(totalMilk.toInt().toString() + " Litros")
            },
            onError = { errorMessage ->
                binding.setTotalMonthlyProduction.setText("0 Litros")
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}