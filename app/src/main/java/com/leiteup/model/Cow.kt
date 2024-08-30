package com.leiteup.model

import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties
import com.leiteup.helper.FirebaseHelper
import kotlinx.parcelize.Parcelize

@Parcelize
@IgnoreExtraProperties
data class Cow(
//    var cowImage: ByteArray? = null,
    var breed: String = "",
    var earring: Int = 0,
    var father: String = "",
    var gender: String = "",
    var isIATF: Boolean = true,
    var id: String = "",
    var mother: String = "",
    var name: String = "",
    var weight: Int = 0,
    var birthDay: String = "",
) : Parcelable {
    init {
        if (id.isEmpty()) {
            this.id = FirebaseHelper.getDatabase().push().key ?: ""
        }
    }
}
