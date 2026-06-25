package com.example.ptmanageremployer

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class LaborCostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_labor_cost)
        findViewById<View>(R.id.labor_root).applySystemBarInsets()
        findViewById<View>(R.id.btn_back).setOnClickListener { finish() }
    }
}
