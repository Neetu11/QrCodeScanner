package com.efficientindia.securescan.ui

import android.Manifest
import android.Manifest.permission
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.efficientindia.securescan.R
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Detector.Detections
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import java.io.IOException


class MainActivity : AppCompatActivity() {

    var surfaceView: SurfaceView? = null
    var barcodeDetector: BarcodeDetector? = null
    var cameraSource: CameraSource? = null
    private var REQUEST_CAMERA_PERMISSION = 201
    var intentData = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main)
        init()
//        if(!checkPermission()) {
//            requestPermission()
//            //  createFile();
//            //  Toast.makeText(DownloadCoupon.this,"",Toast.LENGTH_SHORT).show();
//        }
    }

    fun init(){
        surfaceView = findViewById(R.id.surfaceView);
    }

    fun initialiseDetectorsAndSources() {

      //  Toast.makeText(getApplicationContext(), "Barcode scanner started", Toast.LENGTH_SHORT).show();
        barcodeDetector =  BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();


        cameraSource = CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build()
        surfaceView!!.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    if (ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource!!.start(surfaceView!!.holder)
                    } else {
                        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource!!.stop()
            }
        })

        barcodeDetector!!.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {
                Toast.makeText(
                    applicationContext,
                    "To prevent memory leaks barcode scanner has been stopped",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun receiveDetections(detections: Detections<Barcode>) {
                val barcodes = detections.detectedItems
                if (barcodes.size() != 0) {
//                    txtBarcodeValue.post(Runnable {
//                        if (barcodes.valueAt(0).email != null) {
//                            txtBarcodeValue.removeCallbacks(null)
//                            intentData = barcodes.valueAt(0).email.address
//                            txtBarcodeValue.setText(intentData)
//                            isEmail = true
//                            btnAction.setText("ADD CONTENT TO THE MAIL")
//                        } else {

                            intentData = barcodes.valueAt(0).displayValue
                            //txtBarcodeValue.setText(intentData)
                            Toast.makeText(this@MainActivity,""+intentData,Toast.LENGTH_SHORT).show()
                            //openDialog("S","1","data")
                        //}
                    }
                }
            })

}

    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            applicationContext,
            permission.CAMERA
        )
//        val result1 = ContextCompat.checkSelfPermission(
//            applicationContext,
//            permission.READ_EXTERNAL_STORAGE
//        )
        return result == PackageManager.PERMISSION_GRANTED
    }
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                permission.WRITE_EXTERNAL_STORAGE,
                permission.READ_EXTERNAL_STORAGE
            ),
            1
        )
    }
    private fun showMessageOKCancel(
        message: String,
        okListener: DialogInterface.OnClickListener
    ) {
        AlertDialog.Builder(this@MainActivity)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

//    barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
//        @Override
//        public void release() {
//            Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void receiveDetections(Detector.Detections<Barcode> detections) {
//            final SparseArray<Barcode> barcodes = detections.getDetectedItems();
//            if (barcodes.size() != 0) {
//                txtBarcodeValue.post(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        if (barcodes.valueAt(0).email != null) {
//                            txtBarcodeValue.removeCallbacks(null);
//                            intentData = barcodes.valueAt(0).email.address;
//                            txtBarcodeValue.setText(intentData);
//                            isEmail = true;
//                            btnAction.setText("ADD CONTENT TO THE MAIL");
//                        } else {
//                            isEmail = false;
//                            btnAction.setText("LAUNCH URL");
//                            intentData = barcodes.valueAt(0).displayValue;
//                            txtBarcodeValue.setText(intentData);
//                        }
//                    }
//                });
//            }
//        }
//    });
//}

override fun onPause() {
        super.onPause()
        cameraSource!!.release();
    }

    override fun onResume() {
        super.onResume()
        initialiseDetectorsAndSources();
    }

}