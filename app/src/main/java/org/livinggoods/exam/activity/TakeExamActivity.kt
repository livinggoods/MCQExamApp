package org.livinggoods.exam.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.livinggoods.exam.R
import org.livinggoods.exam.activity.fragment.ExamViewFragment
import org.livinggoods.exam.model.Exam
import org.livinggoods.exam.util.UtilFunctions

class TakeExamActivity : AppCompatActivity(), ExamViewFragment.OnFragmentInteractionListener {

    lateinit var btnSubmit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_exam)

        showQuestionFragment()

        btnSubmit = findViewById<Button>(R.id.btn_submit)
        btnSubmit.setOnClickListener { view ->
            val f = supportFragmentManager.findFragmentById(R.id.questions_container) as ExamViewFragment
            f.webView.loadUrl("javascript:submit()")
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
        Toast.makeText(this@TakeExamActivity, message, Toast.LENGTH_LONG).show()
    }

    override fun getExamJSON(): Exam {
        val json = UtilFunctions.getJsonFromAssets(this, "sample_exam.json")
        val gson = Gson()
        val listType = object : TypeToken<ArrayList<Exam>>() {}.type
        val model = gson.fromJson<MutableList<Exam>>(json, listType)

        return model.get(0)
    }
}
