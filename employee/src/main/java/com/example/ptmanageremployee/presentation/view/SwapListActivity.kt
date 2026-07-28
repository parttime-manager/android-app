package com.example.ptmanageremployee.presentation.view

import com.example.ptmanageremployee.*
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.ptmanageremployee.data.Extras
import com.example.ptmanageremployee.domain.model.SwapApplicationDto
import com.example.ptmanageremployee.domain.model.SwapRequestDto
import com.example.ptmanageremployee.data.TokenStore
import com.example.ptmanageremployee.data.shiftTitle
import com.example.ptmanageremployee.data.swapStatusBadge
import com.example.ptmanageremployee.data.swapStatusLabel

/**
 * 직원 대타 화면. 지원 가능(open)·내 요청(mine)·내 지원(applications) 3개 관점을
 * GET /api/swap-requests, GET /api/swap-applications/me 로 조회한다.
 */
class SwapListActivity : AppCompatActivity() {

    private enum class Tab { OPEN, MINE, APPLIED }

    private var tab = Tab.OPEN

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupScreen(R.layout.activity_swap_list, R.id.swap_list_root)

        bindChips(
            listOf(R.id.chip_open to Tab.OPEN, R.id.chip_mine to Tab.MINE, R.id.chip_applied to Tab.APPLIED),
            tab, R.color.text_tertiary,
        ) { selected -> tab = selected; load() }
    }

    override fun onResume() {
        super.onResume()
        load()
    }

    private fun load() {
        val workplaceId = TokenStore.workplaceId
        val container = findViewById<LinearLayout>(R.id.swap_container)
        val empty = findViewById<TextView>(R.id.tv_swap_empty)
        container.removeAllExcept(R.id.tv_swap_empty)
        if (workplaceId <= 0) {
            empty.visibility = View.VISIBLE
            return
        }
        launchApi {
            when (tab) {
                Tab.OPEN -> renderRequests(
                    screenModel.getSwapRequests(workplaceId, view = "open"), forOpen = true,
                )
                Tab.MINE -> renderRequests(
                    screenModel.getSwapRequests(workplaceId, view = "mine"), forOpen = false,
                )
                Tab.APPLIED -> renderApplications(screenModel.getMySwapApplications())
            }
        }
    }

    private fun renderRequests(list: List<SwapRequestDto>, forOpen: Boolean) = findViewById<View>(R.id.swap_container).renderItems(R.id.tv_swap_empty, list) { req ->
        addRow(
            title = shiftTitle(req),
            sub = req.reason ?: "사유 없음",
            badgeText = if (forOpen) "지원 가능" else swapStatusLabel(req.status),
            badgeBg = if (forOpen) R.drawable.bg_badge_pending else swapStatusBadge(req.status),
            onClick = { openDetail(req.id) },
        )
    }

    private fun renderApplications(list: List<SwapApplicationDto>) = findViewById<View>(R.id.swap_container).renderItems(R.id.tv_swap_empty, list) { app ->
        addRow(
            title = "대타 지원 #${app.swapRequestId ?: app.id}",
            sub = "지원 상태 · ${swapStatusLabel(app.status)}",
            badgeText = swapStatusLabel(app.status),
            badgeBg = swapStatusBadge(app.status),
            onClick = app.swapRequestId?.let { id -> { openDetail(id) } },
        )
    }

    /** 목록이 비면 빈 상태만 보여주고, 아니면 각 항목을 카드로 그린다. */

    private fun addRow(
        title: String,
        sub: String,
        badgeText: String,
        badgeBg: Int,
        onClick: (() -> Unit)?,
    ) {
        val container = findViewById<LinearLayout>(R.id.swap_container)
        container.addItem(R.layout.item_swap) { row ->
            row.text(R.id.tv_title, title)
            row.text(R.id.tv_sub, sub)
            val badge = row.findViewById<TextView>(R.id.tv_badge)
            badge.text = badgeText
            badge.setBackgroundResource(badgeBg)
            onClick?.let { row.setOnClickListener { _ -> it() } }
        }
    }

    private fun openDetail(swapRequestId: Long) {
        startActivity(
            Intent(this, SwapDetailActivity::class.java)
                .putExtra(Extras.SWAP_REQUEST_ID, swapRequestId)
        )
    }
}
