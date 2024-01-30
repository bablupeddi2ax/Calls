package com.example.calls

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager

class PhoneCallReceiver:BroadcastReceiver()  {
    override fun onReceive(p0: Context?, intent: Intent?) {
        if(intent?.action?.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED) == true){
            val state = intent?.getStringExtra(TelephonyManager.EXTRA_STATE)
            val  phoneNumber =intent?.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
            if(state?.equals(TelephonyManager.EXTRA_STATE_RINGING)==true){

            }
        }
    }
}