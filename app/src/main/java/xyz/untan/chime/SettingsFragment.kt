package xyz.untan.chime

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.support.v14.preference.MultiSelectListPreference
import android.support.v14.preference.SwitchPreference
import android.support.v7.preference.ListPreference
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import android.support.v7.preference.PreferenceManager
import android.util.Log

class SettingsFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener,
    TextToSpeech.OnInitListener {
    private val TAG: String = SettingsFragment::class.java.simpleName

    private var tts: TextToSpeech? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        Log.d(TAG, "onCreatePreferences")

        addPreferencesFromResource(R.xml.pref_settings)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.d(TAG, "onActivityCreated")
        super.onActivityCreated(savedInstanceState)

        tts = TextToSpeech(context, this)

        val prefEnabled = findPreference(getString(R.string.pref_key_enabled))
        bindPreferenceChangeListener(prefEnabled)

        // 音声テスト用ボタン
        val btnTest = findPreference(getString(R.string.pref_key_test))
        btnTest.setOnPreferenceClickListener {
            val intent = Intent(context, TTSService::class.java)
            context!!.startService(intent)
            true
        }

        val prefInterval = findPreference(getString(R.string.pref_key_interval))
        bindPreferenceChangeListener(prefInterval)

        val prefDevices = findPreference(getString(R.string.pref_key_devices))
        bindPreferenceChangeListener(prefDevices)
    }

    // implement PreferenceChangeListener
    override fun onPreferenceChange(pref: Preference?, newValue: Any?): Boolean {
        // ユーザーが新しい設定値を選択した時にUIのテキストを更新する
        when (pref) {
            is SwitchPreference -> {
                pref.title = if (newValue as Boolean) getString(R.string.pref_title_enabled_true)
                else getString(R.string.pref_title_enabled_false)
            }
            is ListPreference -> {
                val i = pref.findIndexOfValue(newValue as String)
                if (i >= 0) pref.summary = pref.entries[i]
            }
            is MultiSelectListPreference -> {
                // entriesの順序に従うためにソート
                val ix = (newValue as Collection<*>).map { pref.findIndexOfValue(it as String) }.sorted()
                pref.summary = ix.joinToString(", ") { pref.entries[it] }
            }
            else -> pref!!.summary = newValue.toString()
        }
        return true
    }

    // implement TextToSpeech.OnInitListener
    override fun onInit(status: Int) {
        Log.d(TAG, "onInit")

        if (status != TextToSpeech.SUCCESS) {
            Log.d(TAG, "TextToSpeech error")
            return
        }

        val voiceNames = tts!!.voices.map { it.name }.sorted().toTypedArray()

        // 声の一覧をリストUIに設定
        val prefListVoice = findPreference(getString(R.string.pref_key_voice)) as ListPreference
        prefListVoice.entries = voiceNames
        prefListVoice.entryValues = voiceNames
        bindPreferenceChangeListener(prefListVoice)
    }

    // Preferenceの表示テキストと設定値を連動させる
    private fun bindPreferenceChangeListener(pref: Preference) {
        // Set the listener to watch for value changes.
        pref.onPreferenceChangeListener = this

        val value = PreferenceManager.getDefaultSharedPreferences(context).let {
            when (pref) {
                is SwitchPreference -> it.getBoolean(pref.key, false)
                is ListPreference -> it.getString(pref.key, "") as Any
                is MultiSelectListPreference -> it.getStringSet(pref.key, null)
                else -> ""
            }
        }
        // Trigger the listener immediately with the preference's current value.
        this.onPreferenceChange(pref, value)
    }
}