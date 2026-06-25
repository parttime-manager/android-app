package com.example.ptmanageremployer

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class SubApprovalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sub_approval)
        findViewById<View>(R.id.approval_root).applySystemBarInsets()

        findViewById<View>(R.id.btn_back).setOnClickListener { finish() }
        findViewById<View>(R.id.btn_approve).setOnClickListener {
            Toast.makeText(this, "대타 요청을 승인했어요", Toast.LENGTH_SHORT).show()
            finish()
        }
        findViewById<View>(R.id.btn_reject).setOnClickListener {
            Toast.makeText(this, "대타 요청을 거절했어요", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
