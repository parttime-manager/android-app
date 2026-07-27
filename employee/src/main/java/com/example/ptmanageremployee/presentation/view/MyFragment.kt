package com.example.ptmanageremployee.presentation.view

import com.example.ptmanageremployee.*
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.ptmanageremployee.data.Network
import com.example.ptmanageremployee.data.shiftMinutes
import com.example.ptmanageremployee.data.TokenStore
import com.example.ptmanageremployee.data.won
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import com.example.ptmanageremployee.data.mondayOf

class MyFragment : Fragment(R.layout.fragment_my) {
    private val screenModel by lazy { com.example.ptmanageremployee.AppGraph.model(this) }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.text(R.id.tv_my_name, "${TokenStore.name ?: "사용자"}님")
        val subView = view.findViewById<TextView>(R.id.tv_my_sub)
        subView.text = TokenStore.email ?: "알바"
        // 소속 매장이 있으면 매장명을 함께 표시한다(GET /api/workplaces/{id}).
        val workplaceId = TokenStore.workplaceId
        if (workplaceId > 0) {
            lifecycleScope.launch {
                runCatching { screenModel.getWorkplace(workplaceId) }.getOrNull()?.let { wp ->
                    subView.text = listOfNotNull(wp.name, "알바").joinToString(" · ")
                }
            }
        }

        loadWeekSummary(view)

        bindAccountActions(view)
    }

    /** 이번 주(월~일) 근무 건수 · 출근 횟수 · 예상 급여를 요약 카드에 채운다. */
    private fun loadWeekSummary(view: View) {
        val today = LocalDate.now()
        val monday = mondayOf(today)
        val sunday = monday.plusDays(6)
        val yearMonth = YearMonth.from(today).toString()
        lifecycleScope.launch {
            val shifts = runCatching {
                screenModel.getShifts(employeeId = "me", from = monday.toString(), to = sunday.toString())
            }.getOrNull() ?: return@launch
            val wage = runCatching { screenModel.getMyPayroll(yearMonth).hourlyWage }.getOrDefault(0)
            val minutes = shifts.sumOf { shiftMinutes(it.startTime, it.endTime) }

            view.text(R.id.tv_sum_shifts, "${shifts.size}건")
            view.text(R.id.tv_sum_checkin, "${shifts.count { it.checkedInAt != null }}회")
            view.text(R.id.tv_sum_pay, won(minutes * wage / 60))
        }
    }
}
