package org.livinggoods.exam.activity

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.orm.SugarRecord
import org.livinggoods.exam.R
import org.livinggoods.exam.activity.fragment.ExamViewFragment
import org.livinggoods.exam.model.Choice
import org.livinggoods.exam.model.Exam
import org.livinggoods.exam.model.Question
import org.livinggoods.exam.model.Topic
import org.livinggoods.exam.util.UtilFunctions

class TakeExamActivity : BaseActivity(), ExamViewFragment.OnFragmentInteractionListener {

    lateinit var btnSubmit: Button

    lateinit var exam: Exam
    lateinit var gson: Gson


    companion object {
        val KEY_FORM_ID = "form_id"

        val ACTION_EXAM_DONE = "exam_done"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_exam)

        gson = UtilFunctions.getGsonSerializer()

        val bundle = intent.extras
        if (bundle == null) {
            Toast.makeText(this@TakeExamActivity, getString(R.string.no_exam_selected), Toast.LENGTH_LONG)
                    .show()
            finish()
            return
        }

        val formId = bundle.getLong(KEY_FORM_ID)
        exam = SugarRecord.findById(Exam::class.java, formId)
        val actionbar = supportActionBar
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.title = exam.title
        }

        showQuestionFragment()

        btnSubmit = findViewById<Button>(R.id.btn_submit)
        btnSubmit.setOnClickListener { view ->
            val f = supportFragmentManager.findFragmentById(R.id.questions_container) as ExamViewFragment
            f.webView.loadUrl("javascript:submit()")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
         when (item?.itemId) {

            android.R.id.home -> {
                finish()
                return true
            }

            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    private fun showQuestionFragment() {

        val f = ExamViewFragment.newInstance()

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.questions_container, f)
                .commit()
    }

    override fun onNextOrSubmit() {

    }

    override fun onShowError(message: String) {

        UtilFunctions.showDialog(this@TakeExamActivity, "Error",
                message,
                DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() }, "OK",
                null, null)
    }

    override fun getExamJSON(): Exam {

        val examId = exam.id

        val questions = SugarRecord.find(Question::class.java, "LOCAL_EXAM_ID=?", examId.toString())

        for (i in 0 until questions.size) {
            val question = questions.get(i)
            val choices = SugarRecord.find(Choice::class.java, "LOCAL_QUESTION_ID=?", question.id.toString())
            val topics = SugarRecord.find(Topic::class.java, "LOCAL_QUESTION_ID=?", question.id.toString())

            question.choices = choices
            question.topics = topics

            questions.set(i, question)
        }

        exam.questions = questions

        return exam

    }
}
