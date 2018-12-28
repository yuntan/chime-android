package xyz.untan.chime

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import java.util.*

/*
* TTSService: TTSでしゃべってから自身を終了するだけのService
* 音声を再生するのでフォアグラウンドサービスである必要がある
* 参考: https://developer.android.com/about/versions/oreo/background#migration
* > 音声を再生するサービスは常にフォアグラウンド サービスである必要があります。
*/
class TTSService : Service(), TextToSpeech.OnInitListener {
    private val TAG: String = TTSService::class.java.simpleName
    private val notifyId = 1
    private val channelId = "foreground"
    private var tts: TextToSpeech? = null
    private val listener = object : UtteranceProgressListener() {
        override fun onStart(id: String?) {
            Log.d(TAG, "onStart")
        }

        @Suppress("OverridingDeprecatedMember")
        override fun onError(id: String?) {
            Log.d(TAG, "onError")
        }

        override fun onError(id: String?, errorCode: Int) {
            Log.d(TAG, "onError")
        }

        override fun onDone(id: String?) {
            Log.d(TAG, "onDone")
            stopSelf()
        }
    }

    override fun onBind(intent: Intent): IBinder {
        throw NotImplementedError()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        tts = TextToSpeech(this, this)
        tts!!.setOnUtteranceProgressListener(listener)
        startForeground(notifyId, buildNotification())
        return Service.START_STICKY
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        super.onDestroy()
        tts!!.shutdown()
    }

    // TextToSpeech.OnInitListener
    override fun onInit(status: Int) {
        if (status != TextToSpeech.SUCCESS) {
            Log.d(TAG, "TextToSpeech error")
            return
        }
        val text = genSpeechText()
        tts!!.language = Locale.ENGLISH
        tts!!.setSpeechRate(.8F)
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, text)
    }

    private fun buildNotification(): Notification {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && manager.getNotificationChannel(channelId) == null) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT // makes sound
            val channel = NotificationChannel(channelId, channelId, importance)
            manager.createNotificationChannel(channel)
        }
        return NotificationCompat.Builder(applicationContext, channelId)
            .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
            .build()
    }

    private fun genSpeechText(): String {
        val cal = Calendar.getInstance()
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val min = cal.get(Calendar.MINUTE)

        return if (hour == 0 && min == 0) "midnight"
        else if (hour == 12 && min == 0) "noon"
        else when (min) {
            0 -> "$hour o'clock"
            15 -> "quarter past $hour"
            30 -> "half past $hour"
            45 -> "quarter to ${hour + 1}"
            else -> String.format("%d:%02d", hour, min)
        }
    }
}
