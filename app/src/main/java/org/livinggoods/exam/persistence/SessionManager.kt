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

        val KEY_TRAINING_JSON = "training_json"
        val KEY_TRAINEE_JSON = "trainee_json"
    }

    init {
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref.edit()
    }

    var isSetup: Boolean
        get() {
            return pref.getBoolean(IS_SET_UP, false)
        }

        set(value: Boolean) {
            editor.putBoolean(IS_SET_UP, value)
            editor.commit()
        }

    var sessionDetails: HashMap<String, String>
        get() {

            val value = HashMap<String, String>()

            value.put(KEY_TRAINEE_JSON, pref.getString(KEY_TRAINEE_JSON, null))
            value.put(KEY_TRAINING_JSON, pref.getString(KEY_TRAINING_JSON, null))

            return value
        }

        set(value: HashMap<String, String>) {

            val traineeJson = value.get(KEY_TRAINEE_JSON)
            val trainingJson = value.get(KEY_TRAINING_JSON)

            editor.putString(KEY_TRAINING_JSON, trainingJson)
            editor.putString(KEY_TRAINEE_JSON, traineeJson)

            editor.commit()
        }
}