package com.example.ptmanageremployer

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.ptmanageremployer.data.Network
import com.example.ptmanageremployer.data.TokenStore
import com.example.ptmanageremployer.data.won
import java.time.LocalDate

class LaborCostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_labor_cost)
        findViewById<View>(R.id.labor_root).applySystemBarInsets()
        findViewById<View>(R.id.btn_back).setOnClickListener { finish() }

        loadPayroll()
    }

    private fun loadPayroll() {
        val workplaceId = TokenStore.workplaceId
        val yearMonth = LocalDate.now().toString().substring(0, 7)
        val container = findViewById<LinearLayout>(R.id.cost_container)
        val empty = findViewById<TextView>(R.id.tv_cost_empty)
        text(R.id.tv_subtitle, "$yearMonth 기준")
        if (workplaceId <= 0) {
            empty.visibility = View.VISIBLE
            return
        }
        launchApi {
            val payroll = Network.api.getPayroll(workplaceId, yearMonth)
            text(R.id.tv_total, won(payroll.totalAmount))
            if (payroll.items.isEmpty()) {
                empty.visibility = View.VISIBLE
                return@launchApi
            }
            payroll.items.forEach { item ->
                container.addItem(R.layout.item_cost_row) { row ->
                    row.text(R.id.tv_name, item.employeeName ?: "직원 #${item.employeeId}")
                    val hours = (item.workedMinutes ?: 0) / 60
                    row.text(R.id.tv_hours, "${hours}h")
                    row.text(R.id.tv_amount, won(item.amount ?: 0))
                }
            }
        }
    }
}
