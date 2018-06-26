package org.livinggoods.exam.activity

import android.os.Bundle
import android.preference.PreferenceFragment
import android.view.MenuItem
import org.livinggoods.exam.R
import org.livinggoods.exam.model.Trainee
import org.livinggoods.exam.model.Training
import org.livinggoods.exam.persistence.SessionManager
import org.livinggoods.exam.util.UtilFunctions


class SettingsActivity : BaseActivity() {


    class SettingsPreferenceFragment : PreferenceFragment() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            this.addPreferencesFromResource(R.xml.settings)

            val traineeId = findPreference("config_trainee_id")
            val traineeName = findPreference("config_trainee_name")
            val trainingId = findPreference("config_training_id")
            val trainingName = findPreference("config_training_name")
            val country = findPreference("config_training_country")

            val session = SessionManager(activity)
            val details = session.sessionDetails
            val gson = UtilFunctions.getGsonSerializer()
            val trainee = gson.fromJson<Trainee>(details.get(SessionManager.KEY_TRAINEE_JSON), Trainee::class.java)
            val training = gson.fromJson<Training>(details.get(SessionManager.KEY_TRAINING_JSON), Training::class.java)

            traineeId.summary = trainee.id
            traineeName.summary = trainee.registration!!.name
            trainingId.summary = training.id
            trainingName.summary = training.trainingName
            country.summary = training.country
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.setContentView(R.layout.activity_settings)

        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val fragment = this.fragmentManager

        val transaction = fragment.beginTransaction()

        val prefFragment = SettingsPreferenceFragment()

        val arguments = Bundle()

        transaction.replace(R.id.container, prefFragment)
        transaction.commit()

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {

            android.R.id.home -> {
                finish()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }
}