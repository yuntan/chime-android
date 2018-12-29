package xyz.untan.chime

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

// 端末再起動時にアプリのプロセスを起動するためのReceiver
class BootCompletedReceiver : BroadcastReceiver() {
    private val TAG: String = BootCompletedReceiver::class.java.simpleName

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) {
            return
        }
        Log.d(TAG, "onReceive: ACTION_BOOT_COMPLETED")
    }
}
