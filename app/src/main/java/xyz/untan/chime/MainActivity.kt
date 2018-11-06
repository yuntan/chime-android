package xyz.untan.chime

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            //            val alarmIntent = Intent(this.applicationContext, AlarmReceiver::class.java)
//            sendBroadcast(alarmIntent)
//            val intent = Intent(this, TTSService::class.java)
//            startService(intent)

            val cal = Calendar.getInstance()
            cal.add(Calendar.SECOND, 30)
            val millis = cal.timeInMillis
            val window = 60 * 1000L // 60s

            val manager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmType = AlarmManager.RTC_WAKEUP // do not wake up phone
            val intent = Intent(this, TTSService::class.java)
            val operation = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                PendingIntent.getForegroundService(
                    this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
                )
            } else {
                PendingIntent.getService(
                    this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
                )
            }
            // If there is already an alarm for this Intent scheduled , then it will be removed and replaced by this one.
            manager.setWindow(alarmType, millis, window, operation)
        }
    }
}
