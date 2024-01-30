package com.example.calls

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.io.File
import java.io.IOException
import java.util.Calendar


class MainActivity : AppCompatActivity() {
    private var recorder: MediaRecorder? = null
    private val broadcastReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.S)
        override fun onReceive(context: Context?, intent: Intent?) {
            // Handle broadcasts from the service
            val action = intent?.action
            if (action == "CALL_START") {
                // Handle call start, if needed
                startRecording()
            } else if (action == "CALL_END") {
                // Handle call end, if needed
                stopRecording()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun startRecording() {
        val ctx = applicationContext
        val audioDir = File(
            ctx.getExternalFilesDir(Environment.DIRECTORY_RECORDINGS),
            "CallRecordings"
        )
        audioDir.mkdirs()
        val audioDirPath = audioDir.absolutePath
        val time = Calendar.getInstance().time
        val recordingFile = File(audioDirPath.plus("/$time.m4a"))
        recorder = MediaRecorder(this)
        if (recorder != null) {
            recorder?.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL)
            recorder?.setOutputFile(recordingFile.absolutePath)
            try {
                recorder?.prepare()
                recorder?.start()
                startService(Intent(this@MainActivity, CallService::class.java))

            }
            catch(e:IllegalStateException){
                e.printStackTrace()
                Log.i("RecorException", e.message.toString())
            }catch (e: IOException) {
                e.printStackTrace()
                Log.i("AudioError", e.message.toString())
            }


        } else {
            // Request the permission at runtime
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.CALL_PHONE,
                    android.Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                12
            )
        }

    }


    private fun stopRecording() {
        try {
            recorder?.stop()
            recorder?.reset()
            recorder?.release()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } finally {
            recorder = null
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // ... your recording logic ...
        } else {
            // Request the permission at runtime
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_PHONE_STATE),
                1000
            )
        }
        registerReceiver(broadcastReceiver, IntentFilter().apply {
            addAction("CALL_START")
            addAction("CALL_END")
        }, RECEIVER_EXPORTED)
        val edtPhone = findViewById<EditText>(R.id.edtPhone)
        val btnCall = findViewById<Button>(R.id.btnCall)

        val phoneNumber = "tel:" + "9390589284"

        btnCall.setOnClickListener {
        checkAndInitiateCall()
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun checkAndInitiateCall() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is granted, initiate the phone call
            val dial = Intent(Intent.ACTION_CALL, Uri.parse("tel:9390431750"))
            startActivity(dial)
            // Start recording after the call is initiated
            startRecordingAfterCall()
        } else {
            // Request the permission at runtime
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.CALL_PHONE,
                    android.Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                12
            )
        }
    }
    @RequiresApi(Build.VERSION_CODES.S)
    private fun startRecordingAfterCall() {
        val ctx = applicationContext
        val audioDir = File(
            ctx.getExternalFilesDir(Environment.DIRECTORY_RECORDINGS),
            "CallRecordings"
        )
        audioDir.mkdirs()
        val audioDirPath = audioDir.absolutePath
        val time = Calendar.getInstance().time
        val recordingFile = File(audioDirPath.plus("/$time.m4a"))

        recorder = MediaRecorder(this)
        recorder?.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL)
        recorder?.setOutputFile(recordingFile.absolutePath)

        try {
            recorder?.prepare()
            recorder?.start()
            startService(Intent(this@MainActivity, CallService::class.java))
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            // Handle the exception or log the error
        } catch (e: IOException) {
            e.printStackTrace()
            // Handle the exception or log the error
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        try {
            recorder?.stop()
            recorder?.reset()
            recorder?.release()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } finally {
            recorder = null
        }
        unregisterReceiver(broadcastReceiver)
    }

}