package xyz.untan.chime

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import android.util.Log
import com.deploygate.sdk.DeployGate

/*
* OneshotAlarmReceiver: AlarmManagerから発行されたIntentを受け取り，TTSServiceを起動するBroadcastReceiver．
* 設定値によりTTSServiceを起動するかどうか判別する．
*/
class OneshotAlarmReceiver : BroadcastReceiver() {
    private val tag: String = OneshotAlarmReceiver::class.java.simpleName

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(tag, "onReceive")

        if (isDoNotDisturbOn(context)) {
            val msg = "onReceive: alarm postponed due to do not disturb"
            Log.d(tag, msg)
            DeployGate.logDebug(msg);
            return
        }

        if (!isAudioModeNormal(context)) {
            val msg = "onReceive: alarm postponed due to audio mode"
            Log.d(tag, msg)
            DeployGate.logDebug(msg);
            return
        }

        if (!isHeadsetOn(context)) {
            val msg = "onReceive: alarm postponed due to audio device"
            Log.d(tag, msg)
            DeployGate.logDebug(msg);
            return
        }

        val msg = "onReceive: alarm accepted"
        Log.d(tag, msg)
        DeployGate.logDebug(msg)

        startService(context)
    }

    // Do not Disturb機能が有効になっているか
    private fun isDoNotDisturbOn(context: Context): Boolean {
        // minSdkVersionが23未満で必要
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return false
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // INTERRUPTION_FILTER_ALL: Normal interruption filter - no notifications are suppressed.
        return manager.currentInterruptionFilter != NotificationManager.INTERRUPTION_FILTER_ALL
    }

    // 鳴動中でも通話中でもない
    private fun isAudioModeNormal(context: Context): Boolean {
        val manager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return manager.mode == AudioManager.MODE_NORMAL
    }

    // ヘッドセット（有線イヤホン，Bluetoothヘッドセット等）が接続されている
    private fun isHeadsetOn(context: Context): Boolean {
        val manager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            manager.getDevices(AudioManager.GET_DEVICES_OUTPUTS).any {
                it.type == AudioDeviceInfo.TYPE_WIRED_HEADPHONES
                        || it.type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP
                        || it.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO
            }
        } else {
            manager.isWiredHeadsetOn() || manager.isBluetoothA2dpOn()
        }
    }

    private fun startService(context: Context) {
        val intent = Intent(context, TTSService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Android Oではバックグラウンド実行制限があるので，フォアグラウンドで起動する必要がある
            // https://developer.android.com/about/versions/oreo/background
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }
}
