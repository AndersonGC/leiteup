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
        oldCowEarring: Int,
        updatedCow: Cow,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val databaseReference: DatabaseReference = FirebaseHelper.getDatabase()
            .child("cow")
            .child(FirebaseHelper.getIdUser() ?: "")

        // Atualizar o animal
        databaseReference.child(updatedCow.id).setValue(updatedCow)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("COW_UPDATE", "Animal atualizado com sucesso.")

                    // Variáveis para rastrear se as ordenhas foram atualizadas
                    var updatesCompleted = 0
                    val updatesNeeded = 2 // Nome e brinco

                    // Função para verificar se todas as atualizações foram feitas
                    fun checkIfUpdateIsComplete() {
                        updatesCompleted++
                        if (updatesCompleted == updatesNeeded) {
                            onSuccess()
                        }
                    }

                    // Atualizar nome das ordenhas se necessário
                    if (oldCowName != updatedCow.name) {
                        updateMilkingsWithNewCowName(oldCowName, updatedCow.name, {
                            checkIfUpdateIsComplete() // Sucesso
                        }, { error ->
                            onError("Erro ao atualizar o nome das ordenhas: $error") // Erro
                        })
                    } else {
                        updatesCompleted++ // Nome não precisa ser atualizado
                    }

                    // Atualizar brinco das ordenhas se necessário
                    if (oldCowEarring != updatedCow.earring) {
                        updateMilkingsWithNewCowEarring(oldCowEarring, updatedCow.earring, {
                            checkIfUpdateIsComplete() // Sucesso
                        }, { error ->
                            onError("Erro ao atualizar o brinco das ordenhas: $error") // Erro
                        })
                    } else {
                        updatesCompleted++ // Brinco não precisa ser atualizado
                    }

                    // Se nome e brinco não precisarem ser atualizados, chamar onSuccess diretamente
                    if (updatesCompleted == updatesNeeded) {
                        onSuccess()
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
        val milkingReference = FirebaseHelper.getDatabase().child("milkings").child(FirebaseHelper.getIdUser() ?: "")

        // Busca todas as ordenhas vinculadas ao nome antigo da vaca
        milkingReference.orderByChild("cowName").equalTo(oldCowName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Itera sobre todas as ordenhas encontradas e atualiza o nome da vaca
                        for (childSnapshot in dataSnapshot.children) {
                            val milking = childSnapshot.getValue(Milking::class.java)
                            if (milking != null) {

                                milking.cowName = newCowName

                                milkingReference.child(milking.id).setValue(milking)
                                    .addOnFailureListener {
                                        Log.e("MILKING_UPDATE", "Erro ao atualizar ordenha: ${it.message}")
                                    }
                            }
                        }
                        onSuccess()
                    } else {
                        Log.d("MILKING_UPDATE", "Nenhuma ordenha encontrada para o nome da vaca: $oldCowName")
                        onSuccess()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w("MILKING_UPDATE", "Falha ao ler ordenhas: ${databaseError.toException()}")
                    onError("Falha ao ler as ordenhas.")
                }
            })
    }

    private fun updateMilkingsWithNewCowEarring(
        oldCowEarring: Int,
        newCowEarring: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val milkingReference = FirebaseHelper.getDatabase().child("milkings").child(FirebaseHelper.getIdUser() ?: "")

        milkingReference.orderByChild("cowEarring").equalTo(oldCowEarring.toDouble())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Itera sobre todas as ordenhas encontradas e atualiza o nome da vaca
                        for (childSnapshot in dataSnapshot.children) {
                            val milking = childSnapshot.getValue(Milking::class.java)
                            if (milking != null) {

                                milking.cowEarring = newCowEarring

                                milkingReference.child(milking.id).setValue(milking)
                                    .addOnFailureListener {
                                        Log.e("MILKING_UPDATE", "Erro ao atualizar ordenha: ${it.message}")
                                    }
                            }
                        }
                        onSuccess()
                    } else {
                        Log.d("MILKING_UPDATE", "Nenhuma ordenha encontrada para o nome da vaca: $oldCowEarring")
                        onSuccess()
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

    fun fetchPregnantCows(userId: String, onCowsPregnantReceived: (List<Cow>) -> Unit) {
        val databaseReference = FirebaseHelper.getDatabase().child("cow").child(userId)

        databaseReference.orderByChild("pregnant").equalTo(true)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val cowPregnant = mutableListOf<Cow>()
                    for (snapshot in dataSnapshot.children) {
                        val cow = snapshot.getValue(Cow::class.java)
                        cow?.let {
                            cowPregnant.add(it)
                        }
                    }
                    onCowsPregnantReceived(cowPregnant)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle possible errors
                    Log.e("CowController", "Error fetching pregnant cows", databaseError.toException())
                }
            })
    }

    fun updateCowWithPragnant(cowName: String, datePregnant: String, isPregnant: Boolean, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val databaseReference = FirebaseHelper.getDatabase().child("cow").child(FirebaseHelper.getIdUser() ?: "")

        databaseReference.orderByChild("name").equalTo(cowName).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                if (snapshot.exists()) {
                    // Itera sobre os resultados encontrados
                    for (cowSnapshot in snapshot.children) {
                        // Atualiza os dados da vaca
                        val updates = mapOf(
                            "pregnant" to isPregnant,
                            "pregnantDate" to datePregnant
                        )
                        cowSnapshot.ref.updateChildren(updates).addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                onSuccess() // Chama o callback de sucesso
                            } else {
                                onError(updateTask.exception?.message ?: "Erro ao atualizar dados.") // Chama o callback de erro
                            }
                        }
                        break // Se uma vaca foi encontrada e atualizada, não precisa continuar iterando
                    }
                } else {
                    onError("Nenhuma vaca encontrada com esse nome.") // Chama o callback de erro se não encontrou a vaca
                }
            } else {
                onError(task.exception?.message ?: "Erro ao buscar dados da vaca.") // Chama o callback de erro
            }
        }
    }


    fun cowExists(cowName: String, onResult: (Boolean, Cow?) -> Unit, onError: (String) -> Unit) {
        val cowReference = FirebaseHelper.getDatabase().child("cow").child(FirebaseHelper.getIdUser() ?: "")

        cowReference.orderByChild("name").equalTo(cowName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Iterar sobre os filhos para pegar o primeiro objeto Cow
                        for (snapshot in dataSnapshot.children) {
                            val cow = snapshot.getValue(Cow::class.java) // Converte o snapshot para o objeto Cow
                            if (cow != null) {
                                onResult(true, cow) // Retorna true e o objeto Cow
                                return
                            }
                        }
                    }
                    onResult(false, null) // Caso não exista ou não consiga pegar o objeto
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w("COW_CHECK", "Falha ao verificar a vaca: ${databaseError.toException()}")
                    onError("Falha ao verificar a vaca: ${databaseError.message}")
                }
            })
    }

    fun cowExistsByEarring(earring: Int, onResult: (Boolean, Cow?) -> Unit, onError: (String) -> Unit) {
        val cowReference = FirebaseHelper.getDatabase().child("cow").child(FirebaseHelper.getIdUser() ?: "")

        cowReference.orderByChild("earring").equalTo(earring.toDouble())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(childDataSnapshot: DataSnapshot) {
                    if (childDataSnapshot.exists()) {
                        for (snapshot in childDataSnapshot.children) {
                            val cow = snapshot.getValue(Cow::class.java) // Converte o snapshot para o objeto Cow
                            if (cow != null) {
                                onResult(true, cow) // Retorna true e o objeto Cow
                                return
                            }
                        }
                    } else {
                        onResult(false, null)
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
                    Log.d("Firebase", "cowsCount: " + cowsCount)

                    if (cowsCount > 0) {
                        for (cow in cowList) {
                            milkingController.getAverageMilkingsLast7Days(cow.name, { average ->
                                val adjustedAverage = average / 2.5
                                results.add(Pair(cow, adjustedAverage))
                                processedCowsCount++
                                Log.d("Firebase", "processedCowsCount: " + processedCowsCount)
                                if (processedCowsCount == cowsCount) {
                                    onResult(results)
                                    Log.d("Firebase", "results2:" + results.toString())
                                }
                            }, { error ->
                                processedCowsCount++
                                Log.d("Firebase", "Erro para a vaca ${cow.name}: $error")

                                // Verifica se processamos todas as vacas
                                if (processedCowsCount == cowsCount) {
                                    onResult(results)
                                    Log.d("Firebase", "results2: $results")
                                }
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

    fun getCowWithHighestMilkAverage(
        userId: String,
        onResult: (Cow?) -> Unit, // Retorna a vaca com a maior média ou null se não houver
        onError: (String) -> Unit
    ) {
        val cowReference = FirebaseHelper.getDatabase().child("cow").child(userId)

        cowReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val cowList = mutableListOf<Cow>()
                if (dataSnapshot.exists()) {
                    for (childSnapshot in dataSnapshot.children) {
                        val cow = childSnapshot.getValue(Cow::class.java)
                        if (cow != null) {
                            cowList.add(cow)
                        }
                    }

                    if (cowList.isEmpty()) {
                        onResult(null) // Nenhuma vaca encontrada
                        return
                    }

                    // Processar médias para cada vaca
                    val milkingController = MilkingController()
                    var highestAverage: Double? = null
                    var cowWithHighestAverage: Cow? = null
                    var processedCowsCount = 0

                    for (cow in cowList) {
                        milkingController.getAverageMilkingsLast7Days(cow.name, { average ->
                            processedCowsCount++

                            if (highestAverage == null || average > highestAverage!!) {
                                highestAverage = average
                                cowWithHighestAverage = cow
                            }

                            // Verifica se todas as vacas foram processadas
                            if (processedCowsCount == cowList.size) {
                                onResult(cowWithHighestAverage)
                            }
                        }, { error ->
                            processedCowsCount++
                            Log.e("COW_AVG_ERROR", "Erro ao calcular a média para a vaca ${cow.name}: $error")

                            // Verifica se todas as vacas foram processadas
                            if (processedCowsCount == cowList.size) {
                                onResult(cowWithHighestAverage)
                            }
                        })
                    }
                } else {
                    onError("Nenhuma vaca encontrada para o usuário $userId")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("COW_FETCH_ERROR", "Falha ao ler vacas: ${databaseError.toException()}")
                onError("Falha ao ler vacas.")
            }
        })
    }

    fun getCowWithLowestMilkAverage(
        userId: String,
        onResult: (Cow?) -> Unit, // Retorna a vaca com a menor média ou null se não houver
        onError: (String) -> Unit
    ) {
        val cowReference = FirebaseHelper.getDatabase().child("cow").child(userId)

        cowReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val cowList = mutableListOf<Cow>()
                if (dataSnapshot.exists()) {
                    for (childSnapshot in dataSnapshot.children) {
                        val cow = childSnapshot.getValue(Cow::class.java)
                        if (cow != null) {
                            cowList.add(cow)
                        }
                    }

                    if (cowList.isEmpty()) {
                        onResult(null) // Nenhuma vaca encontrada
                        return
                    }

                    // Processar médias para cada vaca
                    val milkingController = MilkingController()
                    var lowestAverage: Double? = null
                    var cowWithLowestAverage: Cow? = null
                    var processedCowsCount = 0

                    for (cow in cowList) {
                        milkingController.getAverageMilkingsLast7Days(cow.name, { average ->
                            processedCowsCount++

                            // Compara para encontrar a menor média
                            if (lowestAverage == null || average < lowestAverage!!) {
                                lowestAverage = average
                                cowWithLowestAverage = cow
                            }

                            // Verifica se todas as vacas foram processadas
                            if (processedCowsCount == cowList.size) {
                                onResult(cowWithLowestAverage)
                            }
                        }, { error ->
                            processedCowsCount++
                            Log.e("COW_AVG_ERROR", "Erro ao calcular a média para a vaca ${cow.name}: $error")

                            // Verifica se todas as vacas foram processadas
                            if (processedCowsCount == cowList.size) {
                                onResult(cowWithLowestAverage)
                            }
                        })
                    }
                } else {
                    onError("Nenhuma vaca encontrada para o usuário $userId")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("COW_FETCH_ERROR", "Falha ao ler vacas: ${databaseError.toException()}")
                onError("Falha ao ler vacas.")
            }
        })
    }
}