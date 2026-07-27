package com.example.ptmanageremployer.presentation.view

import com.example.ptmanageremployer.*
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.ptmanageremployer.domain.model.PayrollItem
import com.example.ptmanageremployer.data.TokenStore
import com.example.ptmanageremployer.data.weekBucketIndex
import com.example.ptmanageremployer.data.won
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

class StatsFragment : Fragment(R.layout.fragment_stats) {

    // 현재 보고 있는 월. 우측 상단 셀렉터로 변경한다.
    private var selected: YearMonth = YearMonth.now()

    // 시급 저장 후 돌아오면 통계를 갱신한다.
    private val wageEditLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result -> if (result.resultCode == Activity.RESULT_OK) view?.let { loadStats(it) } }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // 인건비 상세는 별도 화면에서 제공한다.
        view.findViewById<View>(R.id.tv_month_total).setOnClickListener {
            startActivity(Intent(requireContext(), LaborCostActivity::class.java))
        }
        view.findViewById<View>(R.id.tv_month_selector).setOnClickListener {
            pickMonth { selected = it; loadStats(view) }
        }
        loadStats(view)
    }

    private fun loadStats(view: View) {
        val yearMonth = selected.toString() // 예: 2026-07
        view.text(R.id.tv_month_selector, "${selected.monthValue}월 ▾")
        view.text(R.id.tv_month_label, "$yearMonth 실근태 기준")

        val workplaceId = TokenStore.workplaceId
        if (workplaceId <= 0) return

        // 현재 월일 때만 이번 주를 강조한다. 지난 달은 강조 없음(-1).
        val activeWeek = if (selected == YearMonth.now()) weekBucketIndex(LocalDate.now()) else -1
        loadPayroll(view, workplaceId, yearMonth)
        loadWeekly(view, workplaceId, yearMonth, activeWeek)
    }

    private fun loadPayroll(view: View, workplaceId: Long, yearMonth: String) {
        val container = view.findViewById<LinearLayout>(R.id.cost_container)
        val empty = view.findViewById<TextView>(R.id.tv_cost_empty)
        lifecycleScope.launch {
            val payroll = runCatching { screenModel.getPayroll(workplaceId, yearMonth) }.getOrNull()
                ?: return@launch
            view.text(R.id.tv_month_total, won(payroll.totalAmount))

            // 플레이스홀더/이전 결과 제거 후 실제 항목으로 채운다. (empty 뷰는 유지)
            container.removeAllExcept(R.id.tv_cost_empty)
            container.renderItems(R.id.tv_cost_empty, payroll.items) { item ->
                container.addItem(R.layout.item_cost_row) { row ->
                    row.bindPayroll(item)
                    if (item.employeeId != null) row.setOnClickListener { openWageEdit(item) }
                }
            }
        }
    }

    /** 직원별 인건비 행을 탭하면 시급 설정 화면을 연다. (인건비 계산의 기준값) */
    private fun openWageEdit(item: PayrollItem) {
        val employeeId = item.employeeId ?: return
        val intent = Intent(requireContext(), WageEditActivity::class.java)
            .putExtra(WageEditActivity.EXTRA_EMPLOYEE_ID, employeeId)
            .putExtra(WageEditActivity.EXTRA_EMPLOYEE_NAME, item.employeeName)
            .putExtra(WageEditActivity.EXTRA_HOURLY_WAGE, item.hourlyWage ?: 0)
            .putExtra(WageEditActivity.EXTRA_WORKED_MINUTES, item.workedMinutes ?: 0)
        wageEditLauncher.launch(intent)
    }

    private fun loadWeekly(view: View, workplaceId: Long, yearMonth: String, activeWeek: Int) {
        lifecycleScope.launch {
            val weekly = runCatching { screenModel.getWeeklyPayroll(workplaceId, yearMonth) }.getOrNull()
                ?: return@launch
            renderWeeklyChart(view, weekly.weeks, activeWeek)
        }
    }
}
