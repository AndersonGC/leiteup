package com.leiteup.model

import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties
import com.leiteup.helper.FirebaseHelper
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Parcelize
@IgnoreExtraProperties
data class Milking(
    var cowEarring: Int = 0,
    var date: String = "", // Usando String para simplificar as consultas
    var quantity: Double = 0.0,
    var milkingNumber: Int = 0, // Número da ordenha (1, 2, 3)
    var id: String = ""
) : Parcelable {
    init {
        this.id = FirebaseHelper.getDatabase().push().key ?: ""
    }
}
