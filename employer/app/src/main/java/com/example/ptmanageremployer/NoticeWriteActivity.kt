package com.example.ptmanageremployer

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class NoticeWriteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_notice_write)
        findViewById<View>(R.id.write_root).applySystemBarInsets()
        findViewById<View>(R.id.btn_back).setOnClickListener { finish() }
        findViewById<View>(R.id.btn_publish).setOnClickListener {
            Toast.makeText(this, "공지를 등록했어요", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
