package com.example.ptmanageremployer

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class ShiftAttendanceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_shift_attendance)
        findViewById<View>(R.id.att_root).applySystemBarInsets()
        findViewById<View>(R.id.btn_back).setOnClickListener { finish() }
    }
}
