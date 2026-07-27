package com.example.ptmanageremployer.presentation.view

import com.example.ptmanageremployer.*
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.ptmanageremployer.data.dateTimeLabel
import com.example.ptmanageremployer.data.Network
import com.example.ptmanageremployer.domain.model.ShiftDto
import com.example.ptmanageremployer.data.TokenStore
import com.example.ptmanageremployer.data.attendanceLabel
import com.example.ptmanageremployer.data.shiftTimeRange
import java.time.LocalDate

class ShiftAttendanceActivity : AppCompatActivity() {

    private val screenModel by lazy { com.example.ptmanageremployer.AppGraph.model(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupScreen(R.layout.activity_shift_attendance, R.id.att_root)

        loadAttendance()
    }

    private fun loadAttendance() {
        val workplaceId = TokenStore.workplaceId
        val today = LocalDate.now().toString()
        val container = findViewById<LinearLayout>(R.id.att_container)
        val empty = findViewById<TextView>(R.id.tv_att_empty)
        text(R.id.tv_date, "$today · 우리 매장")
        if (workplaceId <= 0) {
            empty.visibility = View.VISIBLE
            return
        }
        launchApi {
            val shifts = screenModel.getShifts(workplaceId = workplaceId, from = today, to = today)
            text(R.id.tv_present, shifts.count { it.attendanceStatus == "PRESENT" }.toString())
            text(R.id.tv_late, shifts.count { it.attendanceStatus == "LATE" }.toString())
            text(R.id.tv_absent, shifts.count { it.attendanceStatus == "ABSENT" }.toString())
            if (shifts.isEmpty()) {
                empty.visibility = View.VISIBLE
                return@launchApi
            }
            shifts.forEach { shift ->
                container.addItem(R.layout.item_attendance) { row ->
                    row.text(R.id.tv_name, shift.employeeName ?: "직원 #${shift.employeeId}")
                    row.text(R.id.tv_sub, subOf(shift))
                    row.text(R.id.tv_tag, attendanceLabel(shift.attendanceStatus))
                }
            }
        }
    }

    private fun subOf(shift: ShiftDto): String {
        val range = shiftTimeRange(shift.startTime, shift.endTime)
        val checkedIn = shift.checkedInAt?.let { "출근 ${dateTimeLabel(it)}" } ?: "출근 기록 없음"
        return "$range · $checkedIn"
    }
}
