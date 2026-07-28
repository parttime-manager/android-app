package com.example.ptmanagercommon.presentation.view

import com.example.ptmanagercommon.*
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.ptmanagercommon.domain.model.WeeklyCost
import java.time.YearMonth

private val barIds = intArrayOf(R.id.bar_w1, R.id.bar_w2, R.id.bar_w3, R.id.bar_w4)
private val weekLabelIds = intArrayOf(R.id.tv_w1, R.id.tv_w2, R.id.tv_w3, R.id.tv_w4)

internal fun Fragment.renderWeeklyChart(view: View, weeks: List<WeeklyCost>, activeWeek: Int) {
    // week 값으로 정렬해 1~4주 순서를 보장한다.
    val amounts = LongArray(4)
    weeks.forEach { if (it.week in 1..4) amounts[it.week - 1] = it.amount }
    val max = amounts.max().coerceAtLeast(1L)
    val density = resources.displayMetrics.density

    for (i in 0..3) {
        val heightDp = MIN_BAR_DP + (MAX_BAR_DP - MIN_BAR_DP) * (amounts[i].toFloat() / max)
        val bar = view.findViewById<View>(barIds[i])
        bar.layoutParams = bar.layoutParams.apply { height = (heightDp * density).toInt() }
        val active = i == activeWeek
        bar.setBackgroundResource(if (active) R.drawable.bg_bar_active else R.drawable.bg_bar)
        view.findViewById<TextView>(weekLabelIds[i]).setTextColor(
            ContextCompat.getColor(requireContext(), if (active) R.color.brand else R.color.text_tertiary),
        )
    }
}

internal fun Fragment.pickMonth(onPicked: (YearMonth) -> Unit) {
    val now = YearMonth.now()
    val months = (0 until 12).map { now.minusMonths(it.toLong()) }
    val labels = months.map { "${it.year}년 ${it.monthValue}월" }.toTypedArray()
    AlertDialog.Builder(requireContext())
        .setTitle("월 선택")
        .setItems(labels) { _, which ->
            onPicked(months[which])
        }
        .show()
}

private const val MIN_BAR_DP = 6f
private const val MAX_BAR_DP = 68f
