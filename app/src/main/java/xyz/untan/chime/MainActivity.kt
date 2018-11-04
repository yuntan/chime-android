package xyz.untan.chime

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            val alarmIntent = Intent(this.applicationContext, AlarmReceiver::class.java)
            sendBroadcast(alarmIntent)
        }
    }
}
