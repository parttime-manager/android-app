package com.example.ptmanageremployer.presentation.view

import com.example.ptmanageremployer.*
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.ptmanageremployer.domain.model.ApproveSwapRequest
import com.example.ptmanageremployer.data.Extras
import com.example.ptmanageremployer.domain.model.SwapApplicationDto
import com.example.ptmanageremployer.data.shiftTimeRange

class SubApprovalActivity : AppCompatActivity() {

    private var swapRequestId: Long = -1
    private var applicants: List<SwapApplicationDto> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupScreen(R.layout.activity_sub_approval, R.id.approval_root)

        swapRequestId = intent.getLongExtra(Extras.SWAP_REQUEST_ID, -1)

        findViewById<View>(R.id.btn_approve).setOnClickListener { approve() }
        findViewById<View>(R.id.btn_reject).setOnClickListener { reject() }

        if (swapRequestId > 0) loadDetail()
    }

    private fun loadDetail() {
        launchApi {
            val detail = screenModel.getSwapRequest(swapRequestId)
            applicants = detail.applications.orEmpty()
            text(R.id.tv_req_title, "대타요청 #${detail.id}")
            findViewById<TextView>(R.id.tv_shift).text = detail.shift?.let {
                "${it.workDate ?: ""} ${shiftTimeRange(it.startTime, it.endTime)}"
            } ?: "근무 정보 없음"
            text(R.id.tv_reason, "사유 · ${detail.reason ?: "없음"}")
            val names = applicants.mapNotNull { app ->
                app.applicantName ?: app.applicantId?.let { "지원자 #$it" }
            }
            text(R.id.tv_applicants, if (names.isEmpty()) "없음" else "${names.joinToString(", ")} (${names.size}명)")
        }
    }

    /** 첫 번째 지원자를 대체 근무자로 승인한다. */
    private fun approve() {
        val applicantId = applicants.firstOrNull()?.applicantId
        if (applicantId == null) {
            toast("지원자가 없어 승인할 수 없습니다.")
            return
        }
        launchApi {
            screenModel.approveSwap(swapRequestId, ApproveSwapRequest(applicantId))
            toast("대타요청을 승인했어요")
            finish()
        }
    }

    private fun reject() {
        launchApi {
            screenModel.rejectSwap(swapRequestId)
            toast("대타요청을 거절했어요")
            finish()
        }
    }
}
