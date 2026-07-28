package com.example.ptmanageremployee.presentation.view

import com.example.ptmanageremployee.*
import com.example.ptmanageremployee.data.durationLabel
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.ptmanageremployee.data.weekBucketIndex
import com.example.ptmanageremployee.data.won
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

/** 직원 본인의 이번 달 급여(실근태 기준)를 보여주는 통계 탭. */
class StatsFragment : Fragment(R.layout.fragment_stats) {

    // 현재 보고 있는 월. 우측 상단 셀렉터로 변경한다.
    private var selected: YearMonth = YearMonth.now()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<View>(R.id.tv_month_selector).setOnClickListener {
            pickMonth { selected = it; loadStats(view) }
        }
        loadStats(view)
    }

    private fun loadStats(view: View) {
        val yearMonth = selected.toString() // 예: 2026-07
        view.text(R.id.tv_month_selector, "${selected.monthValue}월 ▾")
        view.text(R.id.tv_pay_label, "${selected.monthValue}월 급여")

        // 현재 월일 때만 이번 주를 강조한다. 지난 달은 강조 없음(-1).
        val activeWeek = if (selected == YearMonth.now()) weekBucketIndex(LocalDate.now()) else -1
        lifecycleScope.launch {
            val pay = runCatching { screenModel.getMyPayroll(yearMonth) }.getOrNull() ?: return@launch
            val hoursText = durationLabel(pay.workedMinutes)

            view.text(R.id.tv_pay_amount, won(pay.amount))
            view.text(R.id.tv_pay_sub, "$hoursText 근무 · 시급 ${won(pay.hourlyWage.toLong())}")
            view.text(R.id.tv_worked_hours, hoursText)
            view.text(R.id.tv_hourly_wage, won(pay.hourlyWage.toLong()))
            renderWeeklyChart(view, pay.weeks, activeWeek)
        }
    }
}
