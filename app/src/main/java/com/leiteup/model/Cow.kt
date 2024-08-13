package com.leiteup.model

import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties
import com.leiteup.helper.FirebaseHelper
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.util.Date

@Parcelize
@IgnoreExtraProperties
data class Cow(
//    var cowImage: ByteArray? = null,
//    var birthDay: LocalDate? = null,
    var breed: String = "",
    var earring: Int = 0,
    var father: String = "",
    var gender: String = "",
    var isIATF: Boolean = true,
    var id: String = "",
    var mother: String = "",
    var name: String = "",
    var weight: Int = 0,
) : Parcelable {
    init {
        this.id = FirebaseHelper.getDatabase().push().key ?: ""
    }
}
