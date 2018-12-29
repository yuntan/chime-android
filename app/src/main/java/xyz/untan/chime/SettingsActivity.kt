package xyz.untan.chime

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log

class SettingsActivity : AppCompatActivity() {
    private val TAG: String = SettingsActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)

        // 単一のFragmentを使用
        supportFragmentManager
            .beginTransaction()
            .replace(android.R.id.content, SettingsFragment())
            .commit()
    }
}
