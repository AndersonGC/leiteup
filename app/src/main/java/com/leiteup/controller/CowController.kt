package com.leiteup.controller

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.leiteup.helper.FirebaseHelper
import com.leiteup.model.Cow

class CowController (private val onCowsReceived: (List<Cow>) -> Unit = {}, private val onError: (String) -> Unit = {}) {

    fun fetchCows(userId: String) {
        val cowReference = FirebaseHelper.getDatabase().child("cow").child(userId)

        cowReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val cowList = mutableListOf<Cow>()
                if (dataSnapshot.exists()) {
                    for (childSnapshot in dataSnapshot.children) {
                        val cow = childSnapshot.getValue(Cow::class.java)
                        if (cow != null) {
                            cowList.add(cow)
                            Log.d("Firebase", "Cow: ${cow.name}, Breed: ${cow.breed}, Earring: ${cow.earring}, Gender: ${cow.gender}")
                        } else {
                            Log.w("Firebase", "Objeto Cow é nulo")
                        }
                    }
                    onCowsReceived(cowList)
                } else {
                    Log.d("Firebase", "Nenhum dado encontrado para o usuário $userId")
                    onError("Nenhum dado encontrado para o usuário $userId")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("Firebase", "Falha ao ler o valor.", databaseError.toException())
                onError("Falha ao ler o valor.")
            }
        })
    }

    fun calculateFood(userId: String, onResult: (List<Pair<Cow, Double>>) -> Unit, onError: (String) -> Unit) {
        val cowReference = FirebaseHelper.getDatabase().child("cow").child(userId)

        cowReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val cowList = mutableListOf<Cow>()
                if (dataSnapshot.exists()) {
                    for (childSnapshot in dataSnapshot.children) {
                        val cow = childSnapshot.getValue(Cow::class.java)
                        if (cow != null) {
                            cowList.add(cow)
                            Log.d("Firebase", "Cow: ${cow.name}, Breed: ${cow.breed}, Earring: ${cow.earring}, Gender: ${cow.gender}")
                        } else {
                            Log.w("Firebase", "Objeto Cow é nulo")
                        }
                    }

                    // Processa a média de leite para cada vaca
                    val milkingController = MilkingController()
                    val results = mutableListOf<Pair<Cow, Double>>()
                    val cowsCount = cowList.size
                    var processedCowsCount = 0

                    if (cowsCount > 0) {
                        for (cow in cowList) {
                            milkingController.getAverageMilkingsLast7Days(cow.name, { average ->
                                val adjustedAverage = average / 2.5
                                results.add(Pair(cow, adjustedAverage))
                                processedCowsCount++

                                // Verifica se todas as vacas foram processadas
                                if (processedCowsCount == cowsCount) {
                                    onResult(results)
                                }
                            }, { error ->
                                onError("Erro ao calcular a média para a vaca ${cow.name}: $error")
                            })
                        }
                    } else {
                        onResult(results) // Retorna lista vazia se não houver vacas
                    }
                } else {
                    Log.d("Firebase", "Nenhum dado encontrado para o usuário $userId")
                    onError("Nenhum dado encontrado para o usuário $userId")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("Firebase", "Falha ao ler o valor.", databaseError.toException())
                onError("Falha ao ler o valor.")
            }
        })
    }
}