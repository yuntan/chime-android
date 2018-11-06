package xyz.untan.chime

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import java.util.*

/*
* AlarmReceiver: AlarmManagerから発行されたIntentを受け取り，TTSServiceを起動するためのアラームを設定するBroadcastReceiver
*/
class AlarmReceiver : BroadcastReceiver() {
    val TAG: String = AlarmReceiver::class.java.simpleName

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive")

        registerAlarmIntent(context)
    }

    // register intent for next timer execution
    private fun registerAlarmIntent(context: Context) {
        val intervalMin = 15

        val cal = Calendar.getInstance()
        // 分を15の倍数にする
        cal.add(Calendar.MINUTE, intervalMin)
        cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) / intervalMin * intervalMin)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val millis = cal.timeInMillis
        val window = 60 * 1000L // 60s

        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmType = AlarmManager.RTC_WAKEUP // wake up the phone
        // FIXME why applicationContext?
        val intent = Intent(context.applicationContext, TTSService::class.java)
        val operation = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Android Oではバックグラウンド実行制限があるので，フォアグラウンドで起動する必要がある
            // https://developer.android.com/about/versions/oreo/background
            PendingIntent.getForegroundService(
                context.applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
            )
        } else {
            PendingIntent.getService(
                context.applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        // If there is already an alarm for this Intent scheduled , then it will be removed and replaced by this one.
        manager.setWindow(alarmType, millis, window, operation)
    }
}
