package xyz.untan.chime

import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import android.util.Log

class SettingsFragment : PreferenceFragmentCompat() {
    private val TAG: String = SettingsFragment::class.java.simpleName

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        Log.d(TAG, "onCreatePreferences")

        addPreferencesFromResource(R.xml.pref_settings)
//            setHasOptionsMenu(true)

        // Bind the summaries of EditText/List/Dialog/Ringtone preferences
        // to their values. When their values change, their summaries are
        // updated to reflect the new value, per the Android Design
        // guidelines.
//            bindPreferenceSummaryToValue(findPreference("sound_devices"))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.d(tag, "onActivityCreated")
        super.onActivityCreated(savedInstanceState)

        val btnTest = findPreference(getString(R.string.pref_key_speak_test))
        btnTest.setOnPreferenceClickListener {
            // TODO
            true
        }
    }

//        override fun onOptionsItemSelected(item: MenuItem): Boolean {
//            val id = item.itemId
//            if (id == android.R.id.home) {
//                startActivity(Intent(activity, SettingsActivity::class.java))
//                return true
//            }
//            return super.onOptionsItemSelected(item)
//        }
}