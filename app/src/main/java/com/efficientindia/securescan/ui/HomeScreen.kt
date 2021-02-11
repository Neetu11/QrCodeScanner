package com.efficientindia.securescan.ui

import android.Manifest
import android.Manifest.permission
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import com.efficientindia.securescan.R


class HomeScreen : AppCompatActivity() , View.OnClickListener{

    var cardview: CardView? =null
    private val REQUEST_CAMERA_PERMISSION = 201
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)
        init()
    }
    fun init(){
        cardview = findViewById(R.id.cardview)
        cardview!!.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
       if(view==cardview){
           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
               if (checkSelfPermission(permission.CAMERA) ==
                   PackageManager.PERMISSION_DENIED
               ) {
                   //permission not enabled, request it
                   val permission = arrayOf(
                       permission.CAMERA
                   )
                   //show popup to request permissions
                   requestPermissions(permission, 1)
               } else {
                   //permission already granted
                   startactivity()
               }
           } else {
               //system os < marshmallow
               startactivity()
           }
           if (ActivityCompat.checkSelfPermission(this@HomeScreen,Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
               //cameraSource.start(surfaceView.getHolder())
               startactivity()

           } else {
               ActivityCompat.requestPermissions(this@HomeScreen,arrayOf(Manifest.permission.CAMERA),REQUEST_CAMERA_PERMISSION)

           }
       }
    }
    fun startactivity(){

            startActivity(Intent(this@HomeScreen,Barcodescanner::class.java))


    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        when (requestCode) {
            1 -> if (grantResults.size > 0) {

                val cameraAccepted =
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                if (cameraAccepted) {
                    startactivity()
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permission.WRITE_EXTERNAL_STORAGE)) {
//                            showMessageOKCancel("You need to allow access to both the permissions",
//                                DialogInterface.OnClickListener { dialog, which ->
//                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                                        requestPermissions(
//                                            arrayOf(
//                                                permission.WRITE_EXTERNAL_STORAGE,
//                                                permission.READ_EXTERNAL_STORAGE
//                                            ),
//                                            1
//                                        )
//                                    }
//                                })
//                            return
                        }
                    }
                }
            }
        }
    }
}