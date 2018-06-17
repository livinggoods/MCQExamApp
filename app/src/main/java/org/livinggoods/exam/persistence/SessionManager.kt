package org.livinggoods.exam.persistence

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences

class SessionManager(internal var _context: Context) {

    internal var pref: SharedPreferences

    internal var editor: SharedPreferences.Editor

    internal var PRIVATE_MODE = 0

    companion object {

        private val PREF_NAME = "8eNqmdRPCjqAxWCv8tk23j8PQnTxRcgNVCQpqzqKqAKsdfsdfdsf"

        val IS_SET_UP = "IsSetUp"

        val KEY_TRAINING = "training"
    }

    init {
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref.edit()
    }
}