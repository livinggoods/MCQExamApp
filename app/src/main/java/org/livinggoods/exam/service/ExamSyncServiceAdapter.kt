package org.livinggoods.exam.service

import android.accounts.Account
import android.accounts.AccountManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.*
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.orm.SugarRecord
import okhttp3.ResponseBody
import org.json.JSONObject
import org.livinggoods.exam.R
import org.livinggoods.exam.model.*
import org.livinggoods.exam.network.API
import org.livinggoods.exam.network.APIClient
import org.livinggoods.exam.persistence.SessionManager
import org.livinggoods.exam.util.UtilFunctions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ExamSyncServiceAdapter(context: Context, autoInitialize: Boolean) : AbstractThreadedSyncAdapter(context, autoInitialize) {


    override fun onPerformSync(account: Account, extras: Bundle, authority: String,
                               provider: ContentProviderClient, syncResult: SyncResult) {

        val exams = SugarRecord.findAll(Exam::class.java).asSequence().toMutableList()
        for (exam in exams) {

            val questionsSize = SugarRecord.count<Question>(Question::class.java, "LOCAL_EXAM_ID=?", arrayOf(exam.id.toString()))
            val answers = SugarRecord.find(Answer::class.java, "TRAINING_EXAM_ID=?", exam.examId)
                    .asSequence()
                    .toMutableList()

            if (answers.size.toLong() < questionsSize) {
                continue
            }

            val api = APIClient.getClient(context).create(API::class.java)
            val call = api.saveExamsAnswers(answers)
            call.enqueue(object : Callback<ResponseBody> {

                override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {

                    try {

                        val body = response?.body()?.string()
                        val json = JSONObject(body)
                        val status = json.getBoolean("status")
                        if (status) {

                            // TODO: Log this event
                            answers.forEach { answer -> answer.delete() }

                            val intent = Intent(ACTION_UPLOAD_COMPLETE)
                            context.sendBroadcast(intent)

                        } else {

                            // TODO Log this event
                            throw Exception("Invalid arguments")
                        }

                    } catch (ex: Exception) {
                        onFailure(call, ex.cause)
                    }
                }

                override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {

                    // TODO Log this error
                    val intent = Intent(ACTION_UPLOAD_COMPLETE)
                    intent.putExtra(KEY_UPLOAD_MSG, "Sync failed. Please check your internet connection and try again")
                    context.sendBroadcast(intent)
                }
            })
        }
    }


    companion object {

        val SYNC_INTERVAL = 15
        val SYNC_FLEXTIME = SYNC_INTERVAL / 3
        val NOTIFICATION_ID = 3004
        val KEY_UPLOAD_MSG = "upload_msg"
        val ACTION_UPLOAD_COMPLETE = "upload_complete"



        fun configurePeriodicSync(context: Context, syncInterval: Int, flexTime: Int) {

            val account = getSyncAccount(context)
            val authority = context.getString(R.string.content_authority)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                val request = SyncRequest.Builder()
                        .syncPeriodic(syncInterval.toLong(), flexTime.toLong())
                        .setSyncAdapter(account, authority)
                        .setExtras(Bundle()).build()
                ContentResolver.requestSync(request)

            } else {

                ContentResolver.addPeriodicSync(account, authority, Bundle(), syncInterval.toLong())
            }
        }

        fun syncImmediately(context: Context) {
            Log.e("MyServiceSync", "syncImmediately")
            val bundle = Bundle()
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true)
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true)
            ContentResolver.requestSync(getSyncAccount(context), context.getString(R.string.content_authority), bundle)
        }

        fun getSyncAccount(context: Context): Account? {

            Log.e("MyServiceSync", "getSyncAccount")
            val session = SessionManager(context)
            val gson = UtilFunctions.getGsonSerializer()
            val sessionDetails = session.sessionDetails
            val trainee = gson.fromJson<Trainee>(sessionDetails.get(SessionManager.KEY_TRAINEE_JSON), Trainee::class.java)
            val training = gson.fromJson<Training>(sessionDetails.get(SessionManager.KEY_TRAINING_JSON), Training::class.java)

            val accountManager = context.getSystemService(Context.ACCOUNT_SERVICE) as AccountManager

            val newAccount = Account(trainee.id, context.getString(R.string.sync_account_type))

            if (accountManager.getPassword(newAccount) == null) {
                if (!accountManager.addAccountExplicitly(newAccount, "", null)) {

                    return null
                }
                onAccountCreated(newAccount, context)
            }
            return newAccount
        }

        private fun onAccountCreated(newAccount: Account, context: Context) {
            Log.e("MyServiceSync", "onAccountCreated")
            ExamSyncServiceAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME)
            ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true)
            syncImmediately(context)
        }

        fun initializeSyncAdapter(context: Context) {
            getSyncAccount(context)
        }
    }

}