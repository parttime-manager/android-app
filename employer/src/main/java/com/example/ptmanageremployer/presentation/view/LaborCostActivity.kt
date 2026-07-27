package com.example.ptmanageremployer.presentation.view

import com.example.ptmanageremployer.*
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.ptmanageremployer.data.TokenStore
import com.example.ptmanageremployer.data.won
import java.time.YearMonth

class LaborCostActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupScreen(R.layout.activity_labor_cost, R.id.labor_root)

        loadPayroll()
    }

    private fun loadPayroll() {
        val workplaceId = TokenStore.workplaceId
        val yearMonth = YearMonth.now().toString()
        val container = findViewById<LinearLayout>(R.id.cost_container)
        val empty = findViewById<TextView>(R.id.tv_cost_empty)
        text(R.id.tv_subtitle, "$yearMonth 기준")
        if (workplaceId <= 0) {
            empty.visibility = View.VISIBLE
            return
        }
        launchApi {
            val payroll = screenModel.getPayroll(workplaceId, yearMonth)
            text(R.id.tv_total, won(payroll.totalAmount))
            container.renderItems(R.id.tv_cost_empty, payroll.items) { item ->
                container.addItem(R.layout.item_cost_row) { row ->
                    row.bindPayroll(item)
                }
            }
        }
    }
}
