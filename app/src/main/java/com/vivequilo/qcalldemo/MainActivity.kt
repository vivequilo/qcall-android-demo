package com.vivequilo.qcalldemo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.gson.JsonObject
import com.vivequilo.qcall_lib.Room
import com.vivequilo.qcall_lib.peerKt.api.responses.Client
import com.vivequilo.qcall_lib.peerKt.observers.RoomObserver
import com.vivequilo.qcall_lib.peerKt.utils.QMediaStream
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object {

        private const val CAMERA_PERMISSION_REQUEST_CODE = 1
        private const val CAMERA_PERMISSION = Manifest.permission.CAMERA
        private const val AUDIO_PERMISSION = Manifest.permission.RECORD_AUDIO
    }
    private lateinit var room: Room



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        room = Room
            .Builder(this, "deploy", "APIKEY", roomId = "1")
            .setMetadata(JsonObject().apply {
                addProperty("name", "Augusto")
            }
            )
            .setRoomObserver(object : RoomObserver(){
                override fun onLocalStream(localPeerId: String, localStream: QMediaStream) {
                    super.onLocalStream(localPeerId, localStream)

                    localStream.videoTracks.firstOrNull()?.let {
                        local_view.setVideoTrack(it)
                    }
                }

                override fun onStreamAdded(caller: Client, remoteStream: QMediaStream) {
                    super.onStreamAdded(caller, remoteStream)
                    remoteStream.videoTracks.firstOrNull()?.let {
                        try {
                            remote_view.setVideoTrack(it)
                        }catch (e: Exception){
                            print(e)
                        }

                    }
                }

                override fun onClientRemoved(remotePeerId: String) {
                    super.onClientRemoved(remotePeerId)
                    runOnUiThread {
                        //Stop call notify a user has logged out of call
                    }

                }
            })
            .build()
        checkCameraPermission()
    }
    private fun checkCameraPermission() {
        if (
            ContextCompat.checkSelfPermission(this, CAMERA_PERMISSION) != PackageManager.PERMISSION_GRANTED
            ||
            ContextCompat.checkSelfPermission(this, AUDIO_PERMISSION) != PackageManager.PERMISSION_GRANTED
        ) {
            requestCameraPermission()
        } else {
            onCameraPermissionGranted()
        }
    }
    private fun onCameraPermissionGranted() {
        room.initView(local_view)
        room.initView(remote_view)
//        local_view.initSurfaceView(rootEglBase = eglBase)
//        room.startVideoCapture(local_view)
        room.startVideoCapture()
        speakerbtn.setOnClickListener {
            room.toggleSpeaker()
        }
        mutebtn.setOnClickListener {
            room.toggleMute()
        }
        camerabtn.setOnClickListener {
            room.toggleCamera()
        }
        call_button.setOnClickListener {
            room.connect({
                print(it)
            }, {
                print(it)
            })
        }
        flip.setOnClickListener {
            room.switchCamera()
//            You should check where is really needed to mirror the image
            local_view.setMirror(!local_view.mirrored)
            remote_view.setMirror(!remote_view.mirrored)
        }
        local_view.setOnClickListener {
            val holder = local_view.track
            local_view.removeCurrentTrack()
            val holder2 = remote_view.track
            remote_view.removeCurrentTrack()
            local_view.setVideoTrack(holder2)
            remote_view.setVideoTrack(holder)
        }
    }

    private fun requestCameraPermission(dialogShown: Boolean = false) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, CAMERA_PERMISSION)
            && !dialogShown) {
            showPermissionRationaleDialog()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(CAMERA_PERMISSION, AUDIO_PERMISSION), CAMERA_PERMISSION_REQUEST_CODE)
        }
    }
    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(this)
            .setTitle("Camera Permission Required")
            .setMessage("This app need the camera to function")
            .setPositiveButton("Grant") { dialog, _ ->
                dialog.dismiss()
                requestCameraPermission(true)
            }
            .setNegativeButton("Deny") { dialog, _ ->
                dialog.dismiss()
                onCameraPermissionDenied()
            }
            .show()
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            onCameraPermissionGranted()
        } else {
            onCameraPermissionDenied()
        }
    }

    private fun onCameraPermissionDenied() {
        Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
//        room.close()
        super.onDestroy()
    }
}