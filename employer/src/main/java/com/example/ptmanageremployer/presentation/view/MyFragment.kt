package com.example.ptmanageremployer.presentation.view

import com.example.ptmanageremployer.*
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.ptmanageremployer.data.Network
import com.example.ptmanageremployer.data.TokenStore
import com.example.ptmanageremployer.data.mondayOf
import com.example.ptmanageremployer.data.weekBucketIndex
import com.example.ptmanageremployer.data.won
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

class MyFragment : Fragment(R.layout.fragment_my) {
    private val screenModel by lazy { com.example.ptmanageremployer.AppGraph.model(this) }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.text(R.id.tv_my_name, "${TokenStore.name ?: "사장"}님")
        view.text(R.id.tv_my_sub, TokenStore.email ?: "사장")

        loadStoreSummary(view)

        bindAccountActions(view)
        view.findViewById<View>(R.id.row_qr).setOnClickListener {
            startActivity(Intent(requireContext(), QrDisplayActivity::class.java))
        }
    }

    /** 이번 주 매장 요약: 이번 주 인건비 · 알바 수 · 이번 주 근무 건수를 채운다. */
    private fun loadStoreSummary(view: View) {
        val workplaceId = TokenStore.workplaceId
        if (workplaceId <= 0) return
        val today = LocalDate.now()
        val yearMonth = YearMonth.from(today).toString()
        val thisWeek = weekBucketIndex(today) + 1
        val monday = mondayOf(today)
        val sunday = monday.plusDays(6)
        lifecycleScope.launch {
            val cost = runCatching {
                screenModel.getWeeklyPayroll(workplaceId, yearMonth)
                    .weeks.firstOrNull { it.week == thisWeek }?.amount ?: 0L
            }.getOrDefault(0L)
            val staff = runCatching {
                screenModel.getMembers(workplaceId, role = "EMPLOYEE").size
            }.getOrDefault(0)
            val shifts = runCatching {
                screenModel.getShifts(workplaceId, from = monday.toString(), to = sunday.toString()).size
            }.getOrDefault(0)

            view.text(R.id.tv_sum_cost, won(cost))
            view.text(R.id.tv_sum_staff, "${staff}명")
            view.text(R.id.tv_sum_shifts, "${shifts}건")
        }
    }
}
