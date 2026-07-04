package com.example.ptmanageremployer

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.ptmanageremployer.data.Network
import com.example.ptmanageremployer.data.TokenStore
import com.example.ptmanageremployer.data.relativeTime
import com.example.ptmanageremployer.data.shiftTimeRange
import com.example.ptmanageremployer.data.won
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fun goTab(itemId: Int) {
            activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)?.selectedItemId = itemId
        }

        view.findViewById<View>(R.id.btn_bell).setOnClickListener {
            startActivity(Intent(requireContext(), NotificationActivity::class.java))
        }
        view.findViewById<View>(R.id.card_now_working).setOnClickListener {
            startActivity(Intent(requireContext(), ShiftAttendanceActivity::class.java))
        }
        view.findViewById<View>(R.id.card_labor).setOnClickListener { goTab(R.id.nav_stats) }
        view.findViewById<View>(R.id.btn_make_schedule).setOnClickListener { goTab(R.id.nav_schedule) }
        // 소식 3행 — 직원 앱과 동일 구성
        view.findViewById<View>(R.id.card_notice).setOnClickListener {
            startActivity(Intent(requireContext(), NoticeListActivity::class.java))
        }
        view.findViewById<View>(R.id.card_handover).setOnClickListener {
            startActivity(Intent(requireContext(), HandoverListActivity::class.java))
        }
        view.findViewById<View>(R.id.card_swap_req).setOnClickListener {
            startActivity(Intent(requireContext(), SwapDoneActivity::class.java))
        }

        loadDashboard(view)
    }

    private fun loadDashboard(view: View) {
        val workplaceId = TokenStore.workplaceId
        if (workplaceId <= 0) return
        val today = LocalDate.now().toString()
        val yearMonth = today.substring(0, 7)

        // 서로 의존 없는 대시보드 호출을 병렬 로드한다 → 첫 화면 지연 최소화.
        lifecycleScope.launch {
            listOf(
                async {
                    runCatching { Network.api.getWorkplace(workplaceId) }.getOrNull()?.let {
                        view.findViewById<TextView>(R.id.tv_store_name).text = it.name ?: "매장"
                    }
                },
                // 지금 근무 중 = 오늘 근무 중 시간대가 현재를 포함하는 근무
                async { loadNowWorking(view, workplaceId, today) },
                // 이번 달 인건비
                async {
                    runCatching { Network.api.getPayroll(workplaceId, yearMonth) }.getOrNull()?.let {
                        view.findViewById<TextView>(R.id.tv_month_labor).text = won(it.totalAmount)
                        view.findViewById<TextView>(R.id.tv_today_labor).text = "—"
                    }
                },
                // 소식 3행
                async { loadLatestNotice(view, workplaceId) },
                async { loadLatestHandover(view, workplaceId) },
                async { loadPendingSwapRequest(view, workplaceId) },
                // 안 읽은 알림 개수(GET /api/notifications/unread-count) → 종 아이콘 빨간 점.
                async {
                    val unread = runCatching { Network.api.getNotificationUnreadCount().count }.getOrDefault(0)
                    view.findViewById<View>(R.id.bell_dot).visibility =
                        if (unread > 0) View.VISIBLE else View.GONE
                },
            ).awaitAll()
        }
    }

    /** 오늘 근무 중 시간대가 현재를 포함하는 근무원 수·이름을 히어로 카드에 표시한다. */
    private suspend fun loadNowWorking(view: View, workplaceId: Long, today: String) {
        val countView = view.findViewById<TextView>(R.id.tv_now_count)
        val namesView = view.findViewById<TextView>(R.id.tv_now_names)
        val shifts = runCatching {
            Network.api.getShifts(workplaceId = workplaceId, from = today, to = today)
        }.getOrNull() ?: run {
            countView.text = "—"; namesView.text = ""; return
        }
        val now = shifts.filter { isNow(it.startTime, it.endTime) }
        countView.text = "${now.size}명"
        namesView.text = if (now.isEmpty()) "지금 근무 중인 직원이 없어요."
            else now.mapNotNull { it.employeeName }.joinToString(" · ")
    }

    /** start~end("HH:mm:ss") 시간대가 현재 시각을 포함하면 true. 종료<시작이면 자정 넘김 근무. */
    private fun isNow(start: String?, end: String?): Boolean {
        val s = runCatching { LocalTime.parse(start) }.getOrNull() ?: return false
        val e = runCatching { LocalTime.parse(end) }.getOrNull() ?: return false
        val now = LocalTime.now()
        return if (e >= s) now >= s && now < e else now >= s || now < e
    }

    /** 최신 공지 1건의 제목·내용을 공지 행에 표시한다. */
    private suspend fun loadLatestNotice(view: View, workplaceId: Long) {
        val notice = runCatching {
            Network.api.getNotices(workplaceId, size = 1).content.firstOrNull()
        }.getOrNull()
        view.findViewById<TextView>(R.id.tv_notice_time).text = relativeTime(notice?.createdAt)
        view.findViewById<TextView>(R.id.tv_notice_title).text =
            notice?.title ?: "등록된 공지가 없습니다."
        view.findViewById<TextView>(R.id.tv_notice_body).text = notice?.body ?: ""
    }

    /** 최신 인수인계 1건의 제목·내용을 표시한다(공지 카드와 동일 형식). */
    private suspend fun loadLatestHandover(view: View, workplaceId: Long) {
        val handover = runCatching {
            Network.api.getHandovers(workplaceId).maxByOrNull { it.createdAt ?: "" }
        }.getOrNull()
        view.findViewById<TextView>(R.id.tv_handover_time).text = relativeTime(handover?.createdAt)
        view.findViewById<TextView>(R.id.tv_handover_title).text =
            handover?.title ?: "등록된 인수인계가 없습니다."
        view.findViewById<TextView>(R.id.tv_handover_content).text = handover?.content ?: ""
    }

    /** 승인 대기 중인 대타요청 중 가장 최근 1건을 표시한다. */
    private suspend fun loadPendingSwapRequest(view: View, workplaceId: Long) {
        val req = runCatching {
            Network.api.getSwapRequests(workplaceId, view = "pending").maxByOrNull { it.createdAt ?: "" }
        }.getOrNull()
        view.findViewById<TextView>(R.id.tv_swap_req_time).text = relativeTime(req?.createdAt)
        val titleView = view.findViewById<TextView>(R.id.tv_swap_req_title)
        val subView = view.findViewById<TextView>(R.id.tv_swap_req_sub)
        if (req == null) {
            titleView.text = "승인 대기 중인 대타요청이 없습니다."
            subView.text = ""
        } else {
            val shift = req.shift
            titleView.text = if (shift != null)
                "${shift.workDate ?: ""} ${shiftTimeRange(shift.startTime, shift.endTime)}".trim()
            else "대타요청 #${req.id}"
            subView.text = req.reason ?: "사유 없음"
        }
    }
}
