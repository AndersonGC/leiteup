package com.leiteup.controller

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.leiteup.helper.FirebaseHelper
import com.leiteup.model.Milking
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class MilkingController(private val onMilkingDataReceived: (List<Milking>) -> Unit = {}, private val onError: (String) -> Unit = {}) {

    fun validateAndSaveMilking(cowName: String, quantity: Double, cowEarring: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val instantDate = LocalDate.now().format(formatter)

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val dateTimestamp = dateFormat.parse(instantDate)?.time ?: 0L

        FirebaseHelper.getDatabase()
            .child("milkings")
            .child(FirebaseHelper.getIdUser() ?: "")
            .orderByChild("cowName")
            .equalTo(cowName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val existingMilkings = snapshot.children.mapNotNull { it.getValue(Milking::class.java) }
                        .filter { it.date == instantDate }

                    when (existingMilkings.size) {
                        0 -> saveMilking(cowName, quantity, cowEarring, instantDate, dateTimestamp, 1, onSuccess, onError)
                        1 -> saveMilking(cowName, quantity, cowEarring, instantDate, dateTimestamp, 2, onSuccess, onError)
                        2 -> saveMilking(cowName, quantity, cowEarring, instantDate, dateTimestamp, 3, onSuccess, onError)
                        else -> onError("Não é permitido registrar mais de três ordenhas para a mesma data.")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    onError("Erro ao consultar o banco de dados.")
                }
            })
    }

    private fun saveMilking(cowName: String, quantity: Double, cowEarring: Int, instantDate: String, dateTimestamp: Long, milkingNumber: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val milking = Milking(
            cowName = cowName,
            quantity = quantity,
            cowEarring = cowEarring,
            date = instantDate,
            dateTimestamp = dateTimestamp,
            milkingNumber = milkingNumber
        )

        FirebaseHelper.getDatabase()
            .child("milkings")
            .child(FirebaseHelper.getIdUser() ?: "")
            .child(milking.id)
            .setValue(milking)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onError("Erro ao salvar ordenha.")
                }
            }
    }

    fun updateMilking(milkingId: String, newCowName: String, cowEarring: Int, newQuantity: Double, onSuccess: () -> Unit, onError: (String) -> Unit) {
        // Obtém a referência do Firebase para a ordenha com o ID fornecido
        val milkingReference = FirebaseHelper.getDatabase()
            .child("milkings")
            .child(FirebaseHelper.getIdUser() ?: "")
            .child(milkingId)

        // Verifica se a ordenha existe
        milkingReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Ordenha encontrada, agora atualizamos os valores
                    val updates = mapOf<String, Any>(
                        "cowName" to newCowName,
                        "cowEarring" to cowEarring,
                        "quantity" to newQuantity
                    )

                    // Atualiza os dados da ordenha no Firebase
                    milkingReference.updateChildren(updates).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            onSuccess() // Chama o callback de sucesso
                        } else {
                            onError("Erro ao atualizar a ordenha.")
                        }
                    }
                } else {
                    onError("Ordenha não encontrada para o ID: $milkingId")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                onError("Erro ao consultar o banco de dados.")
            }
        })
    }

    fun deleteMilking(milkingId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val milkingReference = FirebaseHelper.getDatabase()
            .child("milkings")
            .child(FirebaseHelper.getIdUser() ?: "")
            .child(milkingId)

        milkingReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Ordenha encontrada, agora deletamos
                    milkingReference.removeValue().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            onSuccess() // Chama o callback de sucesso
                        } else {
                            onError("Erro ao deletar a ordenha.")
                        }
                    }
                } else {
                    onError("Ordenha não encontrada para o ID: $milkingId")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                onError("Erro ao consultar o banco de dados.")
            }
        })
    }

    fun fetchMilkingsWithCowName(cowName: String) {
        val milkingsReference = FirebaseHelper.getDatabase().child("milkings").child(FirebaseHelper.getIdUser() ?: "")

        milkingsReference
            .orderByChild("cowName")
            .equalTo(cowName.toString()) // Convertendo para Double, pois Firebase armazena números como Double
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    Log.d("Firebase", "onDataChange chamado")
                    val milkingList = mutableListOf<Milking>()
                    // Verifique se existe algum dado no nó
                    if (dataSnapshot.exists()) {
                        for (childSnapshot in dataSnapshot.children) {
                            // Obtenha o valor dentro do nó filho
                            val milking = childSnapshot.getValue(Milking::class.java)
                            if (milking != null) {
                                milkingList.add(milking)
                                Log.d("Firebase", "Milking ID: ${milking.id}, Date: ${milking.date}, Quantity: ${milking.quantity}")
                            } else {
                                Log.w("Firebase", "Objeto Milking é nulo")
                            }
                        }
                        onMilkingDataReceived(milkingList)
                    } else {
                        Log.d("Firebase", "Nenhum dado encontrado para o cowEarring $cowName")
                        onError("Nenhum dado encontrado para o brinco $cowName")
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w("Firebase", "Falha ao ler o valor.", databaseError.toException())
                    onError("Falha ao ler o valor.")
                }
            })
    }

    fun getAverageMilkingsLast7Days(cowName: String, onResult: (Double) -> Unit, onError: (String) -> Unit) {
        val endDate = System.currentTimeMillis() // Data e hora atual em milissegundos
        val startDate = endDate - (7 * 24 * 60 * 60 * 1000) // Data e hora 7 dias atrás em milissegundos

        FirebaseHelper.getDatabase()
            .child("milkings")
            .child(FirebaseHelper.getIdUser() ?: "")
            .orderByChild("cowName")
            .equalTo(cowName)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val milkingList = mutableListOf<Milking>()

                    for (childSnapshot in dataSnapshot.children) {
                        val milking = childSnapshot.getValue(Milking::class.java)
                        Log.d("Firebase", "childSnapshot:" + milking)
                        if (milking != null) {
                            if (milking.dateTimestamp in startDate..endDate) {
                                milkingList.add(milking)
                                Log.d("Firebase", "milkingList:" + milkingList.toString())
                                Log.d("Firebase", "milkingList:" + milking)
                            }
                        }
                    }

                    if (milkingList.isNotEmpty()) {
                        val average = milkingList.map { it.quantity }.average()
                        onResult(average)
                    } else {
                        onError("Nenhuma ordenha encontrada nos últimos 7 dias.")
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    onError("Falha ao consultar o banco de dados.")
                }
            })
    }

    fun getTotalMilkingsFromYesterday(onResult: (Double) -> Unit, onError: (String) -> Unit) {
        // Obtém o timestamp do início e do fim do dia anterior
        val currentDate = LocalDate.now()
        val yesterday = currentDate.minusDays(1)

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())
        val formattedYesterday = yesterday.format(formatter)

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val startOfDayTimestamp = dateFormat.parse(formattedYesterday)?.time ?: 0L
        val endOfDayTimestamp = startOfDayTimestamp + (24 * 60 * 60 * 1000) - 1 // fim do dia anterior

        FirebaseHelper.getDatabase()
            .child("milkings")
            .child(FirebaseHelper.getIdUser() ?: "")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val milkingList = mutableListOf<Milking>()

                    // Itera por todas as ordenhas no banco de dados
                    for (childSnapshot in dataSnapshot.children) {
                        val milking = childSnapshot.getValue(Milking::class.java)
                        if (milking != null) {
                            // Verifica se a ordenha ocorreu no dia anterior
                            if (milking.dateTimestamp in startOfDayTimestamp..endOfDayTimestamp) {
                                milkingList.add(milking)
                            }
                        }
                    }

                    // Calcula a soma das ordenhas do dia anterior
                    if (milkingList.isNotEmpty()) {
                        val totalMilkQuantity = milkingList.sumOf { it.quantity }
                        onResult(totalMilkQuantity)
                    } else {
                        onError("Nenhuma ordenha encontrada para o dia anterior.")
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    onError("Falha ao consultar o banco de dados.")
                }
            })
    }

    fun getTotalMilkingsForPeriod(days: Long, onResult: (Double) -> Unit, onError: (String) -> Unit) {
        val currentDate = LocalDate.now()
        val startDate = currentDate.minusDays(days)

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())
        val formattedStartDate = startDate.format(formatter)

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val startOfPeriodTimestamp = dateFormat.parse(formattedStartDate)?.time ?: 0L
        val endOfDayTimestamp = Instant.now().toEpochMilli()
        //val endOfDayTimestamp = currentDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli() - 1 // até o fim de ontem

        FirebaseHelper.getDatabase()
            .child("milkings")
            .child(FirebaseHelper.getIdUser() ?: "")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val milkingList = mutableListOf<Milking>()

                    // Itera por todas as ordenhas no banco de dados
                    for (childSnapshot in dataSnapshot.children) {
                        val milking = childSnapshot.getValue(Milking::class.java)
                        if (milking != null) {
                            // Verifica se a ordenha ocorreu no período especificado
                            if (milking.dateTimestamp in startOfPeriodTimestamp..endOfDayTimestamp) {
                                milkingList.add(milking)
                            }
                        }
                    }

                    // Calcula a soma das ordenhas do período
                    if (milkingList.isNotEmpty()) {
                        val totalMilkQuantity = milkingList.sumOf { it.quantity }
                        onResult(totalMilkQuantity)
                    } else {
                        onError("Nenhuma ordenha encontrada para o período especificado.")
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    onError("Falha ao consultar o banco de dados.")
                }
            })
    }

}
