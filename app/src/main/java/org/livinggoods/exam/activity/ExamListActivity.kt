package org.livinggoods.exam.activity

import android.content.*
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import android.widget.Toast
import org.livinggoods.exam.R
import org.livinggoods.exam.activity.adapter.ExamListAdapter
import org.livinggoods.exam.model.Exam
import org.livinggoods.exam.util.UtilFunctions
import com.google.gson.reflect.TypeToken
import com.orm.SugarRecord
import org.livinggoods.exam.model.Answer
import org.livinggoods.exam.persistence.SessionManager
import org.livinggoods.exam.service.ExamSyncServiceAdapter
import org.livinggoods.exam.util.Constants


class ExamListActivity : BaseActivity() {

    lateinit var lvExams: ListView
    lateinit var adapter: ExamListAdapter
    lateinit var session: SessionManager

    var registered: Boolean = false

    internal var examDoneReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            Log.e("STATUS", "Updating list")

            adapter.updateList()

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

            else -> super.onOptionsItemSelected(item)
        }
    }

    fun getExamList(): MutableList<Exam>? {

        val exams = SugarRecord.findAll(Exam::class.java)

        return exams.asSequence().toMutableList()
    }
}
