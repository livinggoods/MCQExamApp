package org.livinggoods.exam.persistence

import android.accounts.AccountManager
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.orm.SugarRecord
import org.livinggoods.exam.R
import org.livinggoods.exam.activity.InitialSetupActivity
import org.livinggoods.exam.model.*
import org.livinggoods.exam.service.ExamSyncServiceAdapter

class SessionManager(internal var _context: Context) {

    internal var pref: SharedPreferences

    internal var editor: SharedPreferences.Editor

    internal var PRIVATE_MODE = 0

    companion object {

        private val PREF_NAME = "8eNqmdRPCjqAxWCv8tk23j8PQnTxRcgNVCQpqzqKqAKsdfsdfdsf"

        val IS_SET_UP = "config_is_setUp"

        val KEY_TRAINING_JSON = "config_training_json"
        val KEY_TRAINEE_JSON = "config_trainee_json"
        val KEY_CLOUD_ENDPOINT = "config_cloud_endpoint"
        val KEY_TRAINERS_IP = "config_trainer_ip"
        val KEY_ONGOING_EXAM = "on_going_exam"
    }


    init {
        pref = PreferenceManager.getDefaultSharedPreferences(_context)
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

    /**
     * {
     *     exam_id: <>
     *     answers: [{...}]
     * }
     *
     */
    fun getCachedExam(examId: String): String {
        val key = "${KEY_ONGOING_EXAM}_${examId}"
        return pref.getString(key, "")
    }

    fun cacheOngoingExam(examId: String, examJson: String) {
        val key = "${KEY_ONGOING_EXAM}_${examId}"
        editor.putString(key, examJson)
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

    val cloudEndpoint: String
        get() {
            //return pref.getString(KEY_CLOUD_ENDPOINT, _context.getString(R.string.config_api_endpoint_default))
            return "http://192.168.100.23:5000/api/v1/";
        }

    fun removeKey(key: String) {
        editor.remove(key)
        editor.commit()
    }

    fun reset() {

        val currentAccount = ExamSyncServiceAdapter.getSyncAccount(_context)

        val accountManager = AccountManager.get(_context)
        accountManager.removeAccount(currentAccount, null, null)

        editor.clear()
        editor.commit()

        SugarRecord.deleteAll(Answer::class.java)
        SugarRecord.deleteAll(Choice::class.java)
        SugarRecord.deleteAll(Exam::class.java)
        SugarRecord.deleteAll(Question::class.java)
        SugarRecord.deleteAll(Topic::class.java)

        val i = Intent(_context, InitialSetupActivity::class.java)

        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        _context.startActivity(i)
    }
}