package xyz.untan.chime

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.*

/*
* App: アプリケーションの他のどのコンポーネントよりも前に生成される
*/
class App : Application() {
    val tag: String = App::class.java.simpleName

    // Called when the application is starting, before any activity, service, or receiver objects (excluding content
    // providers) have been created.
    override fun onCreate() {
        Log.d(tag, "onCreate")
        super.onCreate()

        // TODO 設定で変更可能にする
        val intervalMins = 15

        // RepeatingAlarmReceiverの実行タイミングをTTSServiceよりも遅らせる
        val cal = Calendar.getInstance()
        cal.add(Calendar.MINUTE, intervalMins)
        cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) / intervalMins * intervalMins + 5)
        cal.set(Calendar.SECOND, 0)
        val triggerAtMillis = cal.timeInMillis

        // RepeatingAlarmReceiverをAlarmManagerに登録する
        val manager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmType = AlarmManager.RTC // do not wake up the phone
        val interval = AlarmManager.INTERVAL_FIFTEEN_MINUTES
        // intent for AlarmReceiver
        val intent = Intent(applicationContext, RepeatingAlarmReceiver::class.java)
        val operation = PendingIntent.getBroadcast(
            applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        // If an alarm is delayed (by system sleep, for example, for non _WAKEUP alarm types), a skipped repeat will be
        // delivered as soon as possible. After that, future alarms will be delivered according to the original
        // schedule; they do not drift over time.
        manager.setInexactRepeating(alarmType, triggerAtMillis, interval, operation)
    }
}