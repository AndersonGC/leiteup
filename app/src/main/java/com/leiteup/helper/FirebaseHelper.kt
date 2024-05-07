package com.leiteup.helper

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.leiteup.R

class FirebaseHelper {

    companion object{


        fun getDatabase() = FirebaseDatabase.getInstance().reference

        private fun getAuth() = FirebaseAuth.getInstance()

        fun getIdUser() = getAuth().uid

        fun isAutenticated() = getAuth().currentUser != null

        fun validError(error: String) : Int {
            return when {
                error.contains("The supplied auth credential is incorrect, malformed or has expired") -> {
                    R.string.account_not_registered_fragment
                }
                error.contains("The email address is already in use by another account") -> {
                    R.string.email_in_use_register_fragment
                }
                error.contains("The email address is badly formatted") -> {
                    R.string.invalid_register_fragment
                }
                error.contains("The given password is invalid. [ Password should be at least 6 characters ]") -> {
                    R.string.strong_password_register_fragment
                }
                else->{
                    R.string.generic_error
                }
            }
        }
    }
}