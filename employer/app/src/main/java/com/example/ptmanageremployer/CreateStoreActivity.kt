package com.example.ptmanageremployer

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class CreateStoreActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_store)
        findViewById<View>(R.id.create_root).applySystemBarInsets()
        findViewById<View>(R.id.btn_back).setOnClickListener { finish() }

        val inputState = findViewById<View>(R.id.state_input)
        val codeState = findViewById<View>(R.id.state_code)
        findViewById<View>(R.id.btn_create).setOnClickListener {
            inputState.visibility = View.GONE
            codeState.visibility = View.VISIBLE
        }
        findViewById<View>(R.id.btn_enter_app).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finishAffinity()
        }
    }
}
