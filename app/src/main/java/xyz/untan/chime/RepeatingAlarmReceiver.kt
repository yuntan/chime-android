package xyz.untan.chime

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.*

/*
* RepeatingAlarmReceiver: AlarmManagerから発行されたIntentを受け取り，TTSServiceを起動するためのアラームを設定するBroadcastReceiver．
* おおよそ15分の間隔で繰り返し起動される．
*/
class RepeatingAlarmReceiver : BroadcastReceiver() {
    private val TAG: String = RepeatingAlarmReceiver::class.java.simpleName

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive")

        registerAlarmIntent(context)
    }

    // OneshotAlarmReceiverをAlarmManagerに登録する
    private fun registerAlarmIntent(context: Context) {
        // TODO 設定で変更可能にする
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
        val intent = Intent(context.applicationContext, OneshotAlarmReceiver::class.java)
        val operation =
            PendingIntent.getBroadcast(context.applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        // If there is already an alarm for this Intent scheduled , then it will be removed and replaced by this one.
        manager.setWindow(alarmType, millis, window, operation)
    }
}
