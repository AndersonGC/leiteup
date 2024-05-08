package com.leiteup.model

import android.os.Parcelable
import com.leiteup.helper.FirebaseHelper
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.util.Date

@Parcelize
data class Cow(

    var id: String = "",
    var cowImage: ByteArray? = null,
    var name: String = "",
    var earring: Int = 0,
    var gender: String = "",
    var breed: String = "",
    var Weight: Int = 0,
    var birthDay: LocalDate? = null,
    var isIATF: Boolean = true,
    var father: String = "",
    var mother: String = "",
) : Parcelable {
    init {
        this.id = FirebaseHelper.getDatabase().push().key ?: ""
    }
}
