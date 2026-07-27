package com.example.ptmanageremployee.presentation.view

import com.example.ptmanageremployee.*
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.ptmanageremployee.data.Extras
import com.example.ptmanageremployee.data.Network
import com.example.ptmanageremployee.domain.model.SwapRequestDto
import com.example.ptmanageremployee.data.TokenStore
import com.example.ptmanageremployee.data.shiftTitle
import com.example.ptmanageremployee.data.swapStatusBadge
import com.example.ptmanageremployee.data.swapStatusLabel

class CommunicationFragment : Fragment(R.layout.fragment_communication) {
    private val screenModel by lazy { com.example.ptmanageremployee.AppGraph.model(this) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<View>(R.id.btn_notice_header).setOnClickListener {
            startActivity(Intent(requireContext(), NoticeListActivity::class.java))
        }
        view.findViewById<View>(R.id.btn_handover_header).setOnClickListener {
            startActivity(Intent(requireContext(), HandoverListActivity::class.java))
        }
        view.findViewById<View>(R.id.btn_swap_header).setOnClickListener {
            startActivity(Intent(requireContext(), SwapListActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        // 작성/지원 등으로 다른 화면에 다녀와도 최신 내용이 보이도록 재조회한다.
        view?.let {
            loadNotices(it)
            loadHandoverPreview(it)
            loadSwaps(it)
        }
    }

    private fun loadNotices(root: View) = loadNoticePreview(root) { card, notice ->
        card.setOnClickListener {
            startActivity(Intent(requireContext(), NoticeDetailActivity::class.java)
                .putExtra(Extras.NOTICE_ID, notice.id))
        }
    }

    private fun loadSwaps(view: View) {
        val workplaceId = TokenStore.workplaceId
        val panel = view.findViewById<LinearLayout>(R.id.panel_swap)
        val empty = view.findViewById<TextView>(R.id.tv_swap_empty)
        panel.removeAllExcept(R.id.tv_swap_empty)
        if (workplaceId <= 0) {
            empty.visibility = View.VISIBLE
            return
        }
        launchApi {
            // 다른 직원의 대타요청과 내가 신청한 대타요청을 각각 최근 1건씩 노출한다.
            val open = screenModel.getSwapRequests(workplaceId, view = "open")
            val mine = screenModel.getSwapRequests(workplaceId, view = "mine")
            if (open.isEmpty() && mine.isEmpty()) {
                empty.visibility = View.VISIBLE
                return@launchApi
            }
            empty.visibility = View.GONE
            open.firstOrNull()?.let {
                panel.addSwapCaption("다른 직원의 대타요청")
                // 지원 가능한(다른 직원의) 대타 요청. 탭 → 상세(지원).
                addSwapCard(panel, it, "지원 가능", badgeBg = null)
            }
            mine.firstOrNull()?.let {
                panel.addSwapCaption("내가 신청한 대타요청")
                // 내가 신청한 대타 요청은 진행 상태를 배지로 보여준다.
                addSwapCard(panel, it, swapStatusLabel(it.status), swapStatusBadge(it.status))
            }
        }
    }

    /** 대타 요청 카드 1건. 탭 → 상세. */
    private fun addSwapCard(panel: LinearLayout, req: SwapRequestDto, badgeText: String, badgeBg: Int?) {
        panel.addItem(R.layout.item_swap) { row ->
            row.text(R.id.tv_title, shiftTitle(req))
            row.text(R.id.tv_sub, req.reason ?: "사유 없음")
            val badge = row.findViewById<TextView>(R.id.tv_badge)
            badge.text = badgeText
            badgeBg?.let { badge.setBackgroundResource(it) }
            row.setOnClickListener { openSwapDetail(req.id) }
        }
    }

    private fun openSwapDetail(swapRequestId: Long) {
        startActivity(
            Intent(requireContext(), SwapDetailActivity::class.java)
                .putExtra(Extras.SWAP_REQUEST_ID, swapRequestId)
        )
    }
}
