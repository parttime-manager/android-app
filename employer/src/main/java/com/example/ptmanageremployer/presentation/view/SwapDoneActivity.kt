package com.example.ptmanageremployer.presentation.view

import com.example.ptmanageremployer.*
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.ptmanageremployer.data.Extras
import com.example.ptmanageremployer.data.Network
import com.example.ptmanageremployer.domain.model.SwapRequestDto
import com.example.ptmanageremployer.data.TokenStore
import com.example.ptmanageremployer.data.shiftTitle
import com.example.ptmanageremployer.data.swapStatusLabel

/**
 * 대타요청 화면. view=all 로 전체를 받아 대기 중(PENDING)은 상단에 탭 가능한 카드로,
 * 처리 완료(승인/거절)는 아래에 읽기 전용으로 나눠 표시한다.
 * 대기 카드를 탭하면 승인 화면(SubApprovalActivity)으로 이동한다.
 */
class SwapDoneActivity : AppCompatActivity() {

    private val screenModel by lazy { com.example.ptmanageremployer.AppGraph.model(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupScreen(R.layout.activity_swap_done, R.id.swap_done_root)
    }

    override fun onResume() {
        super.onResume()
        load()
    }

    private fun load() {
        val workplaceId = TokenStore.workplaceId
        val container = findViewById<LinearLayout>(R.id.swap_done_container)
        val empty = findViewById<TextView>(R.id.tv_swap_done_empty)
        container.removeAllExcept(R.id.tv_swap_done_empty)
        if (workplaceId <= 0) {
            empty.visibility = View.VISIBLE
            return
        }
        launchApi {
            val all = screenModel.getSwapRequests(workplaceId, view = "all")
            val pending = all.filter { it.status == "PENDING" }
            val done = all.filter { it.status == "APPROVED" || it.status == "REJECTED" }
            empty.visibility = if (pending.isEmpty() && done.isEmpty()) View.VISIBLE else View.GONE
            // 대기 중(직원 대타요청) — 탭하면 승인 화면으로.
            pending.forEach { req -> addCard(container, req, "지원자 확인 →", badgeColor = null) }
            // 처리 완료(승인/거절) — 읽기 전용.
            done.forEach { req ->
                val color = if (req.status == "APPROVED") R.color.accent_green else R.color.accent_red
                addCard(container, req, swapStatusLabel(req.status), color)
            }
        }
    }

    /** 대타 요청 카드 1건. badgeColor 가 null 이면 탭 가능한 대기 카드로 그린다. */
    private fun addCard(
        container: LinearLayout,
        req: SwapRequestDto,
        badgeText: String,
        badgeColor: Int?,
    ) {
        container.addItem(R.layout.item_swap_request) { card ->
            card.text(R.id.tv_title, shiftTitle(req))
            card.text(R.id.tv_sub, req.reason ?: "사유 없음")
            val badge = card.findViewById<TextView>(R.id.tv_badge)
            badge.text = badgeText
            if (badgeColor == null) {
                card.setOnClickListener {
                    startActivity(
                        Intent(this, SubApprovalActivity::class.java)
                            .putExtra(Extras.SWAP_REQUEST_ID, req.id)
                    )
                }
            } else {
                badge.setTextColor(ContextCompat.getColor(this, badgeColor))
            }
        }
    }
}
