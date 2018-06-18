package org.livinggoods.exam.activity

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import mehdi.sakout.fancybuttons.FancyButton
import okhttp3.ResponseBody
import org.json.JSONObject
import org.livinggoods.exam.R
import org.livinggoods.exam.model.Trainee
import org.livinggoods.exam.model.Training
import org.livinggoods.exam.network.API
import org.livinggoods.exam.network.APIClient
import org.livinggoods.exam.util.UtilFunctions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class InitialSetupActivity : BaseActivity(), View.OnClickListener, AdapterView.OnItemSelectedListener {

    lateinit var btnCacheOffline: Button
    lateinit var spTraining: Spinner
    lateinit var spTrainee: Spinner
    lateinit var tvAvailableExams: TextView
    lateinit var layoutNoInternet: LinearLayout
    lateinit var btnRefresh: FancyButton
    lateinit var progressDialog: ProgressDialog
    lateinit var gson: Gson

    lateinit var trainingList: MutableList<Training>
    lateinit var traineeList: MutableList<Trainee>

    var training: Training? = null
    var trainee: Trainee? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_initial_setup)

        progressDialog = ProgressDialog(this@InitialSetupActivity)
        trainingList = mutableListOf<Training>()
        traineeList = mutableListOf<Trainee>();
        gson = UtilFunctions.getGsonSerializer()

        btnCacheOffline = findViewById<Button>(R.id.btn_cache_offline)

        spTraining = findViewById<Spinner>(R.id.sp_training)
        spTraining.onItemSelectedListener = this@InitialSetupActivity

        spTrainee = findViewById<Spinner>(R.id.sp_trainees)
        spTrainee.onItemSelectedListener = this@InitialSetupActivity

        tvAvailableExams = findViewById<TextView>(R.id.tv_available_exams)

        btnCacheOffline.isEnabled = false
        spTrainee.isEnabled = false
        tvAvailableExams.isEnabled = false
        spTraining.isEnabled = false

        btnCacheOffline.setOnClickListener(this@InitialSetupActivity)

        getTrainings()
    }

    override fun onStop() {
        super.onStop()

        if (progressDialog != null)
            progressDialog.dismiss()
    }


    override fun onClick(v: View?) {
        // TODO
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        when (parent?.id) {
            spTraining.id -> onTrainingSelected(parent!!, position, id)

            spTrainee.id -> onTraineeSelected(parent!!, position, id)

            else -> {
                // Do Nothing
            }
        }
    }

    private fun onTrainingSelected(parent: AdapterView<*>, position: Int, id: Long) {
        if (trainingList.size == 0) return
        training = trainingList.get(position)
        getTrainees()
    }

    private fun onTraineeSelected(parent: AdapterView<*>, position: Int, id: Long) {
        if (traineeList.size == 0) return
        trainee = traineeList.get(position)
        getExams()
    }

    private fun getTrainings() {

        progressDialog.isIndeterminate = true
        progressDialog.setMessage(getString(R.string.loading_trainings))
        progressDialog.setCancelable(false)
        progressDialog.show()

        val api = APIClient.getClient(this@InitialSetupActivity).create(API::class.java)
        val call = api.getTrainings()
        call.enqueue(object: Callback<ResponseBody> {

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                progressDialog.dismiss()

                try {

                    val body = response?.body()?.string()
                    val json = JSONObject(body)
                    val trainings = json.getJSONArray("trainings")

                    val listType = object : TypeToken<ArrayList<Training>>() {}.type
                    trainingList = gson.fromJson<MutableList<Training>>(trainings.toString(), listType)

                    // update the UI
                    val adapter = ArrayAdapter<Training>(this@InitialSetupActivity, android.R.layout.simple_spinner_dropdown_item, trainingList)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spTraining.adapter = adapter
                    spTraining.isEnabled = true

                } catch (e: Exception) {
                    onFailure(call, e.cause)
                }
            }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                progressDialog.dismiss()

                t?.printStackTrace()

                // TODO add error handler
            }
        })
    }

    private fun getTrainees() {
        if (training == null) return

        val trainingId = training?.id!!

        progressDialog.isIndeterminate = true
        progressDialog.setMessage(getString(R.string.loading_trainees))
        progressDialog.setCancelable(false)
        progressDialog.show()

        val api = APIClient.getClient(this@InitialSetupActivity).create(API::class.java)
        val call = api.getTrainees(trainingId)
        call.enqueue(object: Callback<ResponseBody> {

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                progressDialog.dismiss()

                try {

                    val body = response?.body()?.string()
                    val json = JSONObject(body)
                    val trainees = json.getJSONArray("trainees")

                    val listType = object : TypeToken<ArrayList<Trainee>>() {}.type
                    traineeList = gson.fromJson<MutableList<Trainee>>(trainees.toString(), listType)

                    // update the UI
                    val adapter = ArrayAdapter<Trainee>(this@InitialSetupActivity, android.R.layout.simple_spinner_dropdown_item, traineeList)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spTrainee.adapter = adapter
                    spTrainee.isEnabled = true

                } catch (e: Exception) {
                    onFailure(call, e.cause)
                }
            }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                progressDialog.dismiss()

                t?.printStackTrace()

                // TODO add error handler
            }
        })
    }

    private fun getExams() {

        if (training == null || trainee == null) return

        progressDialog.isIndeterminate = true
        progressDialog.setMessage(getString(R.string.loading_trainees))
        progressDialog.setCancelable(false)
        progressDialog.show()
    }
}
