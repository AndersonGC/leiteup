package com.leiteup.controller

import com.google.firebase.database.DatabaseReference
import com.leiteup.helper.FirebaseHelper
import com.leiteup.model.Farm

class FarmController {

    fun saveData(farmData: Farm, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val farmReference: DatabaseReference = FirebaseHelper
            .getDatabase()
            .child("farm")
            .child(FirebaseHelper.getIdUser() ?: "")
            .child(farmData.id)

        farmReference.setValue(farmData)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onError("Erro ao salvar: ${task.exception?.message}")
                }
            }
            .addOnFailureListener { exception ->
                onError("Erro ao salvar: ${exception.message}")
            }
    }

}
