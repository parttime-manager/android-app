package com.example.ptmanageremployee.presentation.view

import com.example.ptmanageremployee.*
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ptmanageremployee.domain.model.CheckInRequest
import com.example.ptmanageremployee.data.Extras
import com.example.ptmanageremployee.data.shiftTimeRange
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.launch

/**
 * 매장 QR을 스캔해 출근/퇴근을 기록한다.
 * 근무 상태에 따라 모드가 자동 전환된다: 출근 전→출근, 출근함→퇴근, 퇴근함→완료.
 */
class CheckInActivity : AppCompatActivity() {

    private enum class Mode { CHECK_IN, CHECK_OUT, DONE }

    private var shiftId: Long = -1
    private var mode: Mode = Mode.CHECK_IN
    private var shiftLoaded = false

    private val scanLauncher = registerForActivityResult(ScanContract()) { result ->
        val token = result.contents
        if (token.isNullOrBlank()) {
            toast("QR 스캔이 취소되었습니다.")
        } else {
            submit(token)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupScreen(R.layout.activity_checkin, R.id.checkin_root)

        shiftId = intent.getLongExtra(Extras.SHIFT_ID, -1)

        if (shiftId > 0) loadShift()
        else {
            text(R.id.tv_checkin_shift, "근무 정보가 없어요. 뒤로 돌아가 스케줄을 확인해 주세요.")
            findViewById<View>(R.id.btn_checkin_confirm).isEnabled = false
        }

        findViewById<View>(R.id.btn_checkin_confirm).setOnClickListener {
            if (shiftId <= 0) {
                toast("오늘 예정된 근무가 없습니다.")
                return@setOnClickListener
            }
            if (!shiftLoaded) {
                loadShift()
                return@setOnClickListener
            }
            if (mode == Mode.DONE) return@setOnClickListener
            val prompt = if (mode == Mode.CHECK_IN) "매장 출근 QR을 스캔하세요" else "매장 QR을 스캔해 퇴근하세요"
            scanLauncher.launch(
                ScanOptions()
                    .setPrompt(prompt)
                    .setBeepEnabled(false)
                    .setOrientationLocked(false),
            )
        }
    }

    private fun submit(qrToken: String) {
        launchApi(findViewById<View>(R.id.btn_checkin_confirm)) {
            if (mode == Mode.CHECK_IN) {
                screenModel.checkIn(shiftId, CheckInRequest(qrToken = qrToken))
                toast("출근 처리되었습니다")
            } else {
                screenModel.checkOut(shiftId, CheckInRequest(qrToken = qrToken))
                toast("퇴근 처리되었습니다")
            }
            loadShift() // 상태 갱신 → 버튼 모드 전환(출근 후엔 퇴근 버튼으로)
        }
    }

    private fun loadShift() {
        val button = findViewById<TextView>(R.id.btn_checkin_confirm)
        shiftLoaded = false
        button.isEnabled = false
        button.text = "근무 정보 확인 중"
        lifecycleScope.launch {
            try {
                val shift = screenModel.getShift(shiftId)
                text(R.id.tv_checkin_shift, "${shift.workDate ?: ""} · ${shiftTimeRange(shift.startTime, shift.endTime)} 근무")
                text(R.id.tv_checkin_workplace, shift.workplaceName)
                mode = when {
                    shift.checkedOutAt != null -> Mode.DONE
                    shift.checkedInAt != null -> Mode.CHECK_OUT
                    else -> Mode.CHECK_IN
                }
                shiftLoaded = true
                button.text = when (mode) {
                    Mode.CHECK_IN -> "출근하기"
                    Mode.CHECK_OUT -> "퇴근하기"
                    Mode.DONE -> "근무 완료"
                }
                button.isEnabled = mode != Mode.DONE
            } catch (cancelled: kotlinx.coroutines.CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                text(R.id.tv_checkin_shift, "근무 정보를 불러오지 못했어요. 연결 상태를 확인해 주세요.")
                button.text = "다시 시도하기"
                button.isEnabled = true
            }
        }
    }
}
