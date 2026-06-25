package com.example.ptmanageremployer

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MembersActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_members)
        findViewById<View>(R.id.members_root).applySystemBarInsets()
        findViewById<View>(R.id.btn_back).setOnClickListener { finish() }
        findViewById<View>(R.id.btn_approve_join).setOnClickListener {
            Toast.makeText(this, "가입을 승인했어요", Toast.LENGTH_SHORT).show()
            it.visibility = View.GONE
            findViewById<View>(R.id.pending_join_card).visibility = View.GONE
        }
    }
}
