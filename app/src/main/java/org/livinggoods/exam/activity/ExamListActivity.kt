package org.livinggoods.exam.activity

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import org.livinggoods.exam.R
import org.livinggoods.exam.activity.adapter.ExamListAdapter
import org.livinggoods.exam.model.Exam
import org.livinggoods.exam.util.UtilFunctions
import com.google.gson.reflect.TypeToken
import com.orm.SugarRecord
import com.vistrav.ask.Ask
import org.livinggoods.exam.model.Answer
import org.livinggoods.exam.model.Trainee
import org.livinggoods.exam.model.Training
import org.livinggoods.exam.persistence.SessionManager
import org.livinggoods.exam.service.ExamSyncServiceAdapter
import org.livinggoods.exam.util.Constants
import org.w3c.dom.Text
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import com.vistrav.ask.annotations.AskDenied
import com.vistrav.ask.annotations.AskGranted
import java.io.FileWriter


class ExamListActivity : BaseActivity() {

    lateinit var lvExams: ListView
    lateinit var adapter: ExamListAdapter
    lateinit var session: SessionManager
    lateinit var tvTrainee: TextView
    lateinit var tvTraining: TextView
    lateinit var tvPendingRecords: TextView

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
        val gson = UtilFunctions.getGsonSerializer()
        val trainee = gson.fromJson<Trainee>(details.get(SessionManager.KEY_TRAINEE_JSON), Trainee::class.java)
        val training = gson.fromJson<Training>(details.get(SessionManager.KEY_TRAINING_JSON), Training::class.java)

        tvTrainee.text = trainee.registration?.name
        tvTraining.text = training.trainingName
        tvPendingRecords.text = SugarRecord.count<Answer>(Answer::class.java, "", arrayOf()).toString()

        lvExams.setOnItemClickListener { parent, view, position, id ->
            val exam = adapter.getItem(position) as Exam

            if (exam.localExamStatus != Constants.EXAM_STATUS_PENDING) {
                Toast.makeText(this@ExamListActivity, getString(R.string.cannot_retake_exam), Toast.LENGTH_LONG)
                        .show()
                return@setOnItemClickListener
            }

            val intent = Intent(this@ExamListActivity, TakeExamActivity::class.java)
            intent.putExtra(TakeExamActivity.KEY_FORM_ID, exam.id)
            startActivity(intent)
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
}
