package com.example.ptmanageremployer.presentation.view

import com.example.ptmanageremployer.*
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.ptmanageremployer.data.Extras
import com.example.ptmanageremployer.data.Network
import com.example.ptmanageremployer.domain.model.SwapRequestDto
import com.example.ptmanageremployer.data.TokenStore

class SubFragment : Fragment(R.layout.fragment_sub) {
    private val screenModel by lazy { com.example.ptmanageremployer.AppGraph.model(this) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // 공지 헤더 탭 → 공지 목록(작성/조회/삭제는 목록 화면 내부에 있음).
        view.findViewById<View>(R.id.btn_notice_header).setOnClickListener {
            startActivity(Intent(requireContext(), NoticeListActivity::class.java))
        }
        view.findViewById<View>(R.id.btn_handover_header).setOnClickListener {
            startActivity(Intent(requireContext(), HandoverListActivity::class.java))
        }
        // 대타 헤더 탭 → 처리 완료된 대타 목록. 아래에는 대기 중(직원 요청)을 인라인 노출.
        view.findViewById<View>(R.id.btn_swap_header).setOnClickListener {
            startActivity(Intent(requireContext(), SwapDoneActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        view?.let {
            loadNotices(it)
            loadHandoverPreview(it)
            loadPending(it)
        }
    }

    private fun loadNotices(root: View) = loadNoticePreview(root) { card, notice ->
            card.findViewById<View>(R.id.btn_delete).visibility = View.GONE
        card.findViewById<View>(R.id.notice_body).setOnClickListener {
            startActivity(Intent(requireContext(), NoticeDetailActivity::class.java)
                .putExtra(Extras.NOTICE_ID, notice.id))
        }
    }

    /** 대기 중 대타요청 중 지원자 있는 것/없는 것을 각각 최근 1건씩 노출한다. */
    private fun loadPending(root: View) {
        val workplaceId = TokenStore.workplaceId
        val container = root.findViewById<LinearLayout>(R.id.panel_pending)
        val empty = root.findViewById<TextView>(R.id.tv_pending_empty)
        if (workplaceId <= 0) return
        launchApi {
            // 최신순(서버 정렬) 대기 요청을 훑어 지원자 유/무 각 1건을 고른다.
            // ponytail: 요청마다 지원자 목록을 조회. 스캔이 많아지면 목록 응답에 applicantCount 추가.
            val pending = screenModel.getSwapRequests(workplaceId, view = "pending")
            var withApp: Pair<SwapRequestDto, Int>? = null
            var without: SwapRequestDto? = null
            for (req in pending) {
                if (withApp != null && without != null) break
                val count = runCatching { screenModel.getSwapApplications(req.id).size }.getOrDefault(0)
                if (count > 0) { if (withApp == null) withApp = req to count }
                else if (without == null) without = req
            }
            // 빈 상태 뷰(인덱스 0)만 남기고 이전 카드 제거
            container.removeAllExcept(R.id.tv_pending_empty)
            if (withApp == null && without == null) {
                empty.visibility = View.VISIBLE
                return@launchApi
            }
            empty.visibility = View.GONE
            withApp?.let { (req, count) ->
                container.addSwapCaption("지원자가 있는 대타요청")
                addPendingCard(container, req, "지원자 ${count}명 →")
            }
            without?.let { req ->
                container.addSwapCaption("지원자가 없는 대타요청")
                addPendingCard(container, req, "지원자 없음")
            }
        }
    }

    private fun addPendingCard(container: LinearLayout, req: SwapRequestDto, badge: String) {
        container.addItem(R.layout.item_swap_request) { card ->
            card.text(R.id.tv_title, "대타요청 #${req.id}")
            card.text(R.id.tv_sub, req.reason ?: "사유 없음")
            card.text(R.id.tv_badge, badge)
            card.setOnClickListener {
                startActivity(
                    Intent(requireContext(), SubApprovalActivity::class.java)
                        .putExtra(Extras.SWAP_REQUEST_ID, req.id)
                )
            }
        }
    }
}
