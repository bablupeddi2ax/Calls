package com.example.calls

import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.IBinder
import android.telecom.ConnectionService
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.ActivityCompat

class CallService : Service() {

    private lateinit var telephonyManager: TelephonyManager
    private lateinit var phoneStateListener: PhoneStateListener
    private val PERMISSION_REQUEST_READ_PHONE_STATE = 100
    private lateinit var recorder: MediaRecorder
    override fun onCreate() {
        super.onCreate()
        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        phoneStateListener = MyPhoneStateListener()
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
        }


    }


    inner class MyPhoneStateListener : PhoneStateListener() {
        override fun onCallStateChanged(state: Int, phoneNumber: String?) {
            super.onCallStateChanged(state, phoneNumber)

            when (state) {
                TelephonyManager.CALL_STATE_RINGING -> {
                    Log.d("CallService", "Incoming call from: $phoneNumber")
                    // Implement call recording or other actions here
                    startRecording(applicationContext)
                }
                TelephonyManager.CALL_STATE_IDLE -> {
                    Log.d("CallService", "Call ended")
                    // Implement call recording stop or other actions here
                    stopRecording()
                }
                // Add other states if needed (e.g., OFFHOOK for ongoing calls)
            }
        }
    }

    private fun startRecording(activityContext: Context) {
        // Start recording logic
        // Notify the activity about call start
        sendBroadcast(Intent("CALL_START"))

        // Request permissions using the activity context

    }


    private fun stopRecording() {
        // Stop recording logic
        // Notify the activity about call end
        sendBroadcast(Intent("CALL_END"))

        // ... your stop recording logic ...
    }

    override fun onDestroy() {
        super.onDestroy()
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE)
        recorder.release()
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}
