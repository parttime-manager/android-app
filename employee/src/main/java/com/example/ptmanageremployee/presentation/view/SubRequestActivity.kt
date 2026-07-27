package com.example.ptmanageremployee.presentation.view

import com.example.ptmanageremployee.*
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ptmanageremployee.domain.model.CreateSwapRequest
import com.example.ptmanageremployee.data.Extras
import com.example.ptmanageremployee.data.shiftTimeRange
import kotlinx.coroutines.launch

class SubRequestActivity : AppCompatActivity() {

    private var shiftId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupScreen(R.layout.activity_sub_request, R.id.sub_root, R.id.btn_cancel)

        shiftId = intent.getLongExtra(Extras.SHIFT_ID, -1)
        if (shiftId > 0) loadSummary()

        bindChips(listOf(R.id.chip_all to true, R.id.chip_pick to false), selected = true) { }

        findViewById<View>(R.id.btn_send).setOnClickListener { btn ->
            if (shiftId <= 0) {
                toast("대상 근무가 없습니다.")
                return@setOnClickListener
            }
            val reasonText = findViewById<EditText>(R.id.input_reason).text.toString().trim()
            val reason = reasonText.ifBlank { "대타요청합니다." }
            launchApi(btn) {
                screenModel.createSwapRequest(CreateSwapRequest(shiftId, reason))
                toast("대타요청을 보냈어요")
                finish()
            }
        }
    }

    private fun loadSummary() {
        lifecycleScope.launch {
            try {
                val shift = screenModel.getShift(shiftId)
                text(R.id.tv_shift_summary, shift.workDate)
                text(R.id.tv_shift_time, shiftTimeRange(shift.startTime, shift.endTime))
            } catch (_: Exception) {
            }
        }
    }

}
