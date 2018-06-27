package org.livinggoods.exam.activity

import android.Manifest
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.support.v4.content.ContextCompat
import android.text.InputType
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.orm.SugarRecord
import com.vistrav.ask.Ask
import com.vistrav.ask.annotations.AskDenied
import com.vistrav.ask.annotations.AskGranted
import mehdi.sakout.fancybuttons.FancyButton
import okhttp3.ResponseBody
import org.json.JSONObject
import org.livinggoods.exam.R
import org.livinggoods.exam.activity.adapter.ExamListAdapter
import org.livinggoods.exam.model.*
import org.livinggoods.exam.network.API
import org.livinggoods.exam.network.APIClient
import org.livinggoods.exam.persistence.SessionManager
import org.livinggoods.exam.service.ExamSyncServiceAdapter
import org.livinggoods.exam.util.Constants
import org.livinggoods.exam.util.UtilFunctions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*


class ExamListActivity : BaseActivity() {

    lateinit var lvExams: ListView
    lateinit var adapter: ExamListAdapter
    lateinit var session: SessionManager
    lateinit var tvTrainee: TextView
    lateinit var tvTraining: TextView
    lateinit var tvPendingRecords: TextView
    lateinit var trainee: Trainee
    lateinit var training: Training
    lateinit var gson: Gson
    lateinit var btnRefresh: FancyButton

    var registered: Boolean = false
    var isSyncTriggeredManually = false

    val DATA_STORAGE_DIR = "TREMAP"

    internal var examDoneReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            Log.e("STATUS", "Updating list")

            adapter.updateList()
            tvPendingRecords.text = SugarRecord.count<Answer>(Answer::class.java, "", arrayOf()).toString()

        }
    }

    internal var answerUploadReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val params = intent.extras

            tvPendingRecords.text = SugarRecord.count<Answer>(Answer::class.java, "", arrayOf()).toString()

            if (params == null) return

            if (params.containsKey(ExamSyncServiceAdapter.KEY_UPLOAD_MSG) && isSyncTriggeredManually) {
                val msg = params.getString(ExamSyncServiceAdapter.KEY_UPLOAD_MSG)
                Toast.makeText(this@ExamListActivity, msg, Toast.LENGTH_LONG).show()

                isSyncTriggeredManually = false
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        session = SessionManager(this@ExamListActivity)

        if (!session.isSetup) {
            val intent = Intent(this@ExamListActivity, InitialSetupActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        ExamSyncServiceAdapter.initializeSyncAdapter(applicationContext)

        setContentView(R.layout.activity_exam_list)

        lvExams = findViewById<ListView>(R.id.lv_exams)
        val examList = getExamList()
        adapter = ExamListAdapter(this, examList!!)
        lvExams.adapter = adapter

        tvPendingRecords = findViewById(R.id.tv_pending_records) as TextView
        tvTrainee = findViewById(R.id.tv_trainee)
        tvTraining = findViewById(R.id.tv_training)

        val session = SessionManager(this@ExamListActivity)
        val details = session.sessionDetails
        gson = UtilFunctions.getGsonSerializer()
        trainee = gson.fromJson<Trainee>(details.get(SessionManager.KEY_TRAINEE_JSON), Trainee::class.java)
        training = gson.fromJson<Training>(details.get(SessionManager.KEY_TRAINING_JSON), Training::class.java)

        tvTrainee.text = trainee.registration?.name
        tvTraining.text = training.trainingName
        tvPendingRecords.text = SugarRecord.count<Answer>(Answer::class.java, "", arrayOf()).toString()

        btnRefresh = findViewById<FancyButton>(R.id.btn_refresh)
        btnRefresh.setOnClickListener { getExams() }

        lvExams.setOnItemClickListener { parent, view, position, id ->
            val exam = adapter.getItem(position) as Exam

            if (exam.localExamStatus != Constants.EXAM_STATUS_PENDING) {
                Toast.makeText(this@ExamListActivity, getString(R.string.cannot_retake_exam), Toast.LENGTH_LONG)
                        .show()
                return@setOnItemClickListener
            }

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Enter Exam Unlock Code")
            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_NUMBER

            builder.setPositiveButton("OK", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {

                    val unlockCode = input.text.toString().toInt()
                    if (unlockCode == exam.unlockCode) {
                        val intent = Intent(this@ExamListActivity, TakeExamActivity::class.java)
                        intent.putExtra(TakeExamActivity.KEY_FORM_ID, exam.id)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@ExamListActivity, "Wrong unlock code. Please try again", Toast.LENGTH_LONG)
                                .show()
                    }

                    dialog?.dismiss()
                }
            })
            builder.setNegativeButton("CANCEL", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    dialog?.dismiss()
                }

            })

            val dialog = builder.create()
            val dpi = this@ExamListActivity.getResources().getDisplayMetrics().density
            dialog.setView(input, (19*dpi).toInt(), (5*dpi).toInt(), (14*dpi).toInt(), (5*dpi).toInt())

            dialog.show()
        }

        supportActionBar?.title = getString(R.string.title_available_exams)

        registerReceiver(examDoneReceiver, IntentFilter(TakeExamActivity.ACTION_EXAM_DONE))
        registered = true
    }


    override fun onResume() {
        super.onResume()

        registerReceiver(answerUploadReceiver, IntentFilter(ExamSyncServiceAdapter.ACTION_UPLOAD_COMPLETE))
    }

    override fun onPause() {

        unregisterReceiver(answerUploadReceiver)

        super.onPause()
    }

    override fun onDestroy() {

        if (registered) unregisterReceiver(examDoneReceiver)
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        return when (item?.itemId) {

            R.id.menu_settings -> {
                val intent = Intent(this@ExamListActivity, SettingsActivity::class.java)
                startActivity(intent)
                true
            }

            R.id.menu_view_pending -> {
                val intent = Intent(this@ExamListActivity, PendingDataActivity::class.java)
                startActivity(intent)
                true
            }

            R.id.menu_trigger_upload -> {
                Toast.makeText(this@ExamListActivity, "Starting data sync in the background", Toast.LENGTH_LONG).show()
                isSyncTriggeredManually = true
                ExamSyncServiceAdapter.syncImmediately(this@ExamListActivity)
                true
            }

            R.id.menu_reset -> {

                UtilFunctions.showDialog(this@ExamListActivity,
                        getString(R.string.confirm_settings_reset),
                        getString(R.string.confirm_settings_reset_inst),
                        DialogInterface.OnClickListener { dialog, which ->

                            dialog.dismiss()
                            session.reset()
                            this@ExamListActivity.finish()
                        },
                        getString(R.string.confirm),
                        DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() },
                        getString(R.string.cancel)
                )
                true
            }

            R.id.menu_export_data_to_sd -> {
                exportToSdCard()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    fun getExamList(): MutableList<Exam>? {

        val exams = SugarRecord.findAll(Exam::class.java)

        return exams.asSequence().toMutableList()
    }

    fun exportToSdCard() {

        val isGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

        if (isGranted) {
            fileAccessGranted(2000)
        } else {
            Ask.on(this@ExamListActivity)
                    .id(2000)
                    .forPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .withRationales("Please allow permission for this feature to work")
                    .go()
        }
    }

    @AskGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun fileAccessGranted(id: Int) {
        try {
            val file = getOutputMediaFile()!!
            val records = SugarRecord.findAll(Answer::class.java).asSequence().toMutableList()
            val gson = UtilFunctions.getGsonSerializer()

            val writer = FileWriter(file)
            writer.write(gson.toJson(records))
            writer.close()

            Toast.makeText(this@ExamListActivity, "Data written to ${file.path}", Toast.LENGTH_LONG).show()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    @AskDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun fileAccessDenied(id: Int) {
        Toast.makeText(this@ExamListActivity, "Permission denied. Please allow permission for this feature", Toast.LENGTH_LONG)
                .show()
    }

    private fun isExternalStorageReadOnly(): Boolean {
        val extStorageState = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED_READ_ONLY == extStorageState
    }

    private fun isExternalStorageAvailable(): Boolean {
        val extStorageState = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == extStorageState
    }

    private fun getOutputMediaFile(): File? {

        val mediaStorageDir = File(Environment.getExternalStorageDirectory(), DATA_STORAGE_DIR)

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.e(DATA_STORAGE_DIR, "Oops! Failed create "
                        + DATA_STORAGE_DIR + " directory")
                return null
            }
        }

        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())

        val mediaFile = File("${mediaStorageDir.getPath()}${File.separator}EXPORT_${timestamp}.json")

        return mediaFile
    }

    private fun getExams() {

        if (training == null || trainee == null) return

        val trainingId = training.id!!

        val progressDialog = ProgressDialog(this@ExamListActivity)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage(getString(R.string.loading_exams))
        progressDialog.setCancelable(false)
        progressDialog.show()

        val api = APIClient.getClient(this@ExamListActivity).create(API::class.java)
        val call = api.getExams(trainingId)
        call.enqueue(object: Callback<ResponseBody> {

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                progressDialog.dismiss()

                try {

                    val body = response?.body()?.string()
                    val json = JSONObject(body)
                    val exams = json.getJSONArray("exams")

                    val listType = object: TypeToken<ArrayList<Exam>>() {}.type
                    val newExamsList = gson.fromJson<MutableList<Exam>>(exams.toString(), listType)

                    newExamsList.forEach { newExam ->
                        val exam = SugarRecord.find(Exam::class.java, "EXAM_ID=?", "${newExam.examId}").firstOrNull()
                        if (exam != null) {
                            if (exam.localExamStatus == Constants.EXAM_STATUS_DONE)
                                return@forEach

                            val existingQuestions = SugarRecord.find(Question::class.java, "LOCAL_EXAM_ID=?", "${exam.id}")
                            existingQuestions.forEach { eQuestion ->
                                SugarRecord.deleteAll(Topic::class.java, "LOCAL_QUESTION_ID=?", "${eQuestion.id}")
                                SugarRecord.deleteAll(Choice::class.java, "LOCAL_QUESTION_ID=?", "${eQuestion.id}")
                                eQuestion.delete()
                            }

                            exam.delete()
                        }

                        // Proceed to save the exam details
                        newExam.traineeId = trainee.id!!
                        newExam.save()
                        newExam.questions?.forEach { question ->
                            question.localExamId = newExam.id
                            question.save()

                            question.choices!!.forEach {choice ->
                                choice.localQuestionId = question.id
                                choice.save()
                            }

                            question.topics!!.forEach {topic ->
                                topic.localQuestionId = question.id
                                topic.save()
                            }
                        }
                    }

                    adapter.updateList()

                } catch (e: Exception) {
                    onFailure(call, e.cause)
                }
            }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                progressDialog.dismiss()

                showConnectionError(call!!, t!!)
            }
        })
    }
}
