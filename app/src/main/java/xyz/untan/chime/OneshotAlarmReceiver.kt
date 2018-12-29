package xyz.untan.chime

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import android.support.v7.preference.PreferenceManager
import android.util.Log
import com.deploygate.sdk.DeployGate

/*
* OneshotAlarmReceiver: AlarmManagerから発行されたIntentを受け取り，TTSServiceを起動するBroadcastReceiver．
* 設定値によりTTSServiceを起動するかどうか判別する．
*/
class OneshotAlarmReceiver : BroadcastReceiver() {
    private val TAG: String = OneshotAlarmReceiver::class.java.simpleName

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive")

        // 設定値によって実行をスキップする
        val enabled = PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(context.getString(R.string.pref_key_enabled), true)
        if (!enabled) {
            Log.d(TAG, "onReceive: enabled = false")
            return
        }

        // Do not Disturb設定を確認
        val dnd = PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(context.getString(R.string.pref_key_dnd), true)
        if (dnd && isDoNotDisturbOn(context)) {
            val msg = "onReceive: alarm postponed due to do not disturb"
            Log.d(TAG, msg)
            DeployGate.logDebug(msg)
            return
        }

        // 鳴動中・通話中でないことを確認
        if (!isAudioModeNormal(context)) {
            val msg = "onReceive: alarm postponed due to audio mode"
            Log.d(TAG, msg)
            DeployGate.logDebug(msg)
            return
        }

        // 音声出力デバイスを確認
        val devices = PreferenceManager.getDefaultSharedPreferences(context)
            .getStringSet(context.getString(R.string.pref_key_devices), null)
        if (devices != null) {
            val speakerOk = devices.contains(context.getString(R.string.pref_entry_speaker))
            val wiredOk = devices.contains(context.getString(R.string.pref_entry_wired))
            val btOk = devices.contains(context.getString(R.string.pref_entry_bt))
            if ((isWiredHeadsetOn(context) && !wiredOk) || (isBtHeadsetOn(context) && !btOk)
                || (!isHeadsetOn(context) && !speakerOk)
            ) {
                val msg = "onReceive: alarm postponed due to audio device"
                Log.d(TAG, msg)
                DeployGate.logDebug(msg)
                return
            }
        }

        val msg = "onReceive: alarm accepted"
        Log.d(TAG, msg)
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

    private fun isHeadsetOn(context: Context): Boolean =
        isWiredHeadsetOn(context) || isBtHeadsetOn(context)

    // 有線ヘッドセットが接続されている
    private fun isWiredHeadsetOn(context: Context): Boolean {
        val manager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            manager.getDevices(AudioManager.GET_DEVICES_OUTPUTS).any {
                it.type == AudioDeviceInfo.TYPE_WIRED_HEADPHONES
            }
        } else {
            @Suppress("DEPRECATION")
            manager.isWiredHeadsetOn
        }
    }

    // Bluetoothヘッドセットが接続されている
    private fun isBtHeadsetOn(context: Context): Boolean {
        val manager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            manager.getDevices(AudioManager.GET_DEVICES_OUTPUTS).any {
                it.type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP
                        || it.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO
            }
        } else {
            @Suppress("DEPRECATION")
            manager.isBluetoothA2dpOn
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
