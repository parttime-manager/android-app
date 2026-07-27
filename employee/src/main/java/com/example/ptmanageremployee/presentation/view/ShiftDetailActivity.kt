package com.example.ptmanageremployee.presentation.view

import com.example.ptmanageremployee.*
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.ptmanageremployee.data.Extras
import com.example.ptmanageremployee.data.shiftTimeRange

class ShiftDetailActivity : AppCompatActivity() {

    private var shiftId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupScreen(R.layout.activity_shift_detail, R.id.shift_root)

        shiftId = intent.getLongExtra(Extras.SHIFT_ID, -1)

        findViewById<View>(R.id.btn_sub_request).setOnClickListener {
            startActivity(
                Intent(this, SubRequestActivity::class.java).putExtra(Extras.SHIFT_ID, shiftId)
            )
        }

        if (shiftId > 0) loadShift()
    }

    private fun loadShift() {
        launchApi {
            val shift = screenModel.getShift(shiftId)
            text(R.id.tv_date, shift.workDate)
            text(R.id.tv_time, shiftTimeRange(shift.startTime, shift.endTime))
            text(R.id.tv_workplace, shift.workplaceName ?: "-")
            text(R.id.tv_coworkers, shift.coworkers?.takeIf { it.isNotEmpty() }?.joinToString(", ") ?: "혼자 근무")
            text(R.id.tv_pay, shift.estimatedPay?.let { "%,d원".format(it) } ?: "-")
        }
    }
}
