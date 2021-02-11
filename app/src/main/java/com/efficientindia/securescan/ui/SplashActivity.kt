package com.efficientindia.securescan.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.efficientindia.securescan.R


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash)
        //                +"<font color='#E15616'>" + "world" + "</font>"));
        val handler = Handler()

        handler.postDelayed(object : Runnable {
            override fun run() {
               startActivity(Intent(this@SplashActivity,HomeScreen::class.java))
            }
        }, 6000)

    }
}