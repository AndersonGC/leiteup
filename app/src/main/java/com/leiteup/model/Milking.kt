package com.leiteup.model

import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties
import com.leiteup.helper.FirebaseHelper
import kotlinx.parcelize.Parcelize

@Parcelize
@IgnoreExtraProperties
data class Milking(
    var cowName: String = "",
    var cowEarring: Int = 0,
    var date: String = "",
    var dateTimestamp: Long = 0L,
    var quantity: Double = 0.0,
    var milkingNumber: Int = 0,
    var id: String = ""
) : Parcelable {
    init {
        if (id.isEmpty()) {
            this.id = FirebaseHelper.getDatabase().push().key ?: ""
        }
    }
}


