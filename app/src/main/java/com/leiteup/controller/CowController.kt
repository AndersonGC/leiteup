package com.leiteup.controller

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.leiteup.helper.FirebaseHelper
import com.leiteup.model.Cow
import com.leiteup.model.Milking

class CowController (private val onCowsReceived: (List<Cow>) -> Unit = {}, private val onError: (String) -> Unit = {}) {

//    fun updateCow(updatedCow: Cow, onSuccess: () -> Unit, onError: (String) -> Unit) {
//        val databaseReference: DatabaseReference = FirebaseHelper.getDatabase()
//            .child("cow")
//            .child(FirebaseHelper.getIdUser() ?: "")
//
//        databaseReference
//            .child(updatedCow.id)
//            .setValue(updatedCow)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    Log.d("COW_UPDATE", "Animal atualizado com sucesso.")
//                    onSuccess()
//                } else {
//                    Log.e("COW_UPDATE", "Erro ao atualizar animal: ${task.exception?.message}")
//                    onError("Erro ao atualizar o animal.")
//                }
//            }
//            .addOnFailureListener { e ->
//                Log.e("COW_UPDATE", "Erro ao atualizar animal: ${e.message}")
//                onError(e.message ?: "Erro desconhecido ao atualizar o animal.")
//            }
//    }
fun saveCow(cow: Cow, isNewCow: Boolean, onSuccess: () -> Unit, onError: (String) -> Unit) {
    val cowReference: DatabaseReference = FirebaseHelper
        .getDatabase()
        .child("cow")
        .child(FirebaseHelper.getIdUser() ?: "")
        .child(cow.id)

    cowReference.setValue(cow)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onSuccess()
            } else {
                onError("Erro ao salvar animal: ${task.exception?.message}")
            }
        }
        .addOnFailureListener { exception ->
            onError("Erro ao salvar animal: ${exception.message}")
        }
    }

    fun deleteCow(cowId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val cowReference = FirebaseHelper.getDatabase()
            .child("cow")
            .child(FirebaseHelper.getIdUser() ?: " ")
            .child(cowId)

        cowReference.removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onSuccess()
            } else {
                onError("Erro ao deletar animal.")
            }
        }
        .addOnFailureListener {
            Log.e("COW_ERROR", "Erro ao deletar animal: ${it.message}")
        }
    }

    fun updateCowAndMilkings(
        oldCowName: String,
        updatedCow: Cow,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val databaseReference: DatabaseReference = FirebaseHelper.getDatabase()
            .child("cow")
            .child(FirebaseHelper.getIdUser() ?: "")

        // Atualiza a vaca
        databaseReference.child(updatedCow.id).setValue(updatedCow)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("COW_UPDATE", "Animal atualizado com sucesso.")

                    // Verifica se o nome foi alterado
                    if (oldCowName != updatedCow.name) {
                        // Nome da vaca foi alterado, agora atualiza os milkings
                        updateMilkingsWithNewCowName(oldCowName, updatedCow.name, onSuccess, onError)
                    } else {
                        onSuccess() // Se o nome não foi alterado, apenas completa a operação
                    }
                } else {
                    Log.e("COW_UPDATE", "Erro ao atualizar animal: ${task.exception?.message}")
                    onError("Erro ao atualizar o animal.")
                }
            }
            .addOnFailureListener { e ->
                Log.e("COW_UPDATE", "Erro ao atualizar animal: ${e.message}")
                onError(e.message ?: "Erro desconhecido ao atualizar o animal.")
            }
    }

    private fun updateMilkingsWithNewCowName(
        oldCowName: String,
        newCowName: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val milkingReference = FirebaseHelper.getDatabase().child("milkings")

        // Busca todas as ordenhas vinculadas ao nome antigo da vaca
        milkingReference.orderByChild("cowName").equalTo(oldCowName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Itera sobre todas as ordenhas encontradas e atualiza o nome da vaca
                        for (childSnapshot in dataSnapshot.children) {
                            val milking = childSnapshot.getValue(Milking::class.java)
                            if (milking != null) {
                                // Atualiza o nome da vaca na ordenha
                                milking.cowName = newCowName

                                // Salva a ordenha atualizada no banco de dados
                                milkingReference.child(milking.id).setValue(milking)
                                    .addOnFailureListener {
                                        Log.e("MILKING_UPDATE", "Erro ao atualizar ordenha: ${it.message}")
                                    }
                            }
                        }
                        onSuccess() // Conclui a operação após todas as ordenhas terem sido atualizadas
                    } else {
                        Log.d("MILKING_UPDATE", "Nenhuma ordenha encontrada para o nome da vaca: $oldCowName")
                        onSuccess() // Conclui a operação mesmo que não haja ordenhas para atualizar
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w("MILKING_UPDATE", "Falha ao ler ordenhas: ${databaseError.toException()}")
                    onError("Falha ao ler as ordenhas.")
                }
            })
    }

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

    fun cowExists(cowName: String, onResult: (Boolean) -> Unit, onError: (String) -> Unit) {
        val cowReference = FirebaseHelper.getDatabase().child("cow").child(FirebaseHelper.getIdUser() ?: "")
        Log.i("COW_VALIDATE", "ACHOUUU " + cowName)
        cowReference.orderByChild("name").equalTo(cowName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Log.i("COW_VALIDATE", "ACHOUUU " + cowName)
                        onResult(true)
                    } else {
                        Log.i("COW_VALIDATE", "NAO ACHOUUU " + cowName)
                        onResult(false)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w("COW_CHECK", "Falha ao verificar a vaca: ${databaseError.toException()}")
                    onError("Falha ao verificar a vaca: ${databaseError.message}")
                }
            })
    }

    fun cowExistsByEarring(earring: Int, onResult: (Boolean) -> Unit, onError: (String) -> Unit) {
        val cowReference = FirebaseHelper.getDatabase().child("cow").child(FirebaseHelper.getIdUser() ?: "")

        cowReference.orderByChild("earring").equalTo(earring.toDouble())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(childDataSnapshot: DataSnapshot) {
                    if (childDataSnapshot.exists()) {
                        onResult(true) // Se o animal com o brinco existe
                        Log.i("COW_VALIDATE", "CHAMOU: " + childDataSnapshot.children.toString())
                    } else {
                        onResult(false) // Se o animal com o brinco não foi encontrado
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w("COW_CHECK", "Falha ao verificar o animal pelo brinco: ${databaseError.toException()}")
                    onError("Falha ao verificar o animal: ${databaseError.message}")
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