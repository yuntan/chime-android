package xyz.untan.chime

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.*

class AlarmReceiver : BroadcastReceiver() {
    val TAG: String = AlarmReceiver::class.java.simpleName

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive")

//        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
//        val vibEnabled = sharedPref.getBoolean(KEY_VIB_ENABLED, false)
//        boolean ledEnabled = sharedPref.getBoolean(KEY_LED_ENABLED, false);

//        if (vibEnabled) {
//            vibrate(context)
//        }

//        updateNotification(context);
//        chime(context)
        context.startService(Intent(context, TTSService::class.java))
        registerAlarmIntent(context)
    }

    private fun getAlarmIntent(context: Context): PendingIntent {
        val alarmIntent = Intent(context.applicationContext, AlarmReceiver::class.java)
        return PendingIntent.getBroadcast(
            context.applicationContext, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    // register intent for next timer execution
    private fun registerAlarmIntent(context: Context) {
        val intent = getAlarmIntent(context)

//        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
//        val intervalMin = Integer.parseInt(sharedPref.getString(SettingsActivity.KEY_INTERVAL, null)!!)
        val intervalMin = 15

        val cal = Calendar.getInstance()
        cal.add(Calendar.MINUTE, intervalMin)
        // make minute a divisible number
        cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) / intervalMin * intervalMin)
        cal.set(Calendar.SECOND, 0)
        val millis = cal.timeInMillis
        val window: Long = 60 * 1000 // 60s

        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            manager.setExactAndAllowWhileIdle(AlarmManager.RTC, millis, intent)
//        } else {
//            manager.setExact(AlarmManager.RTC, millis, intent)
//        }
        manager.setWindow(AlarmManager.RTC, millis, window, intent)
    }

    fun unregisterAlarmIntent(context: Context) {
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.cancel(getAlarmIntent(context))
    }
}
