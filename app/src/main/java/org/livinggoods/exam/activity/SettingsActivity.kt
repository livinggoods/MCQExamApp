package org.livinggoods.exam.activity

import android.os.Bundle
import android.preference.PreferenceFragment
import android.view.MenuItem
import org.livinggoods.exam.R


class SettingsActivity : BaseActivity() {


    class SettingsPreferenceFragment : PreferenceFragment() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            this.addPreferencesFromResource(R.xml.settings)
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