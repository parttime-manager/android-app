package com.example.ptmanageremployer

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        findViewById<View>(R.id.login_root).applySystemBarInsets()

        findViewById<TextView>(R.id.btn_request_code).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        findViewById<TextView>(R.id.btn_create_store).setOnClickListener {
            startActivity(Intent(this, CreateStoreActivity::class.java))
        }
    }
}
