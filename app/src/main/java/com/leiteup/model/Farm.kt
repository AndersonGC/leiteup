package com.leiteup.model

import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties
import com.leiteup.helper.FirebaseHelper
import kotlinx.parcelize.Parcelize

@Parcelize
@IgnoreExtraProperties
data class Farm(
    var totalCows: Int = 0,
    var totalPregnant: Int = 0,
    var totalProductionToday: Int = 0,
    var totalMonthlyProduction: Int = 0,
    var totalWeeklyProduction: Int = 0,
    var bestCow: String = "",
    var worstCow: String = "",
    var id: String = "",
) : Parcelable {
    init {
        if (id.isEmpty()) {
            this.id = FirebaseHelper.getDatabase().push().key ?: ""
        }
    }
}
