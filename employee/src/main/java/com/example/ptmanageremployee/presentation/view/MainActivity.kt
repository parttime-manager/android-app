package com.example.ptmanageremployee.presentation.view

import com.example.ptmanageremployee.*
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ptmanageremployee.data.Network
import com.example.ptmanageremployee.data.TokenStore
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val screenModel by lazy { com.example.ptmanageremployee.AppGraph.model(this) }

    private val home by lazy { HomeFragment() }
    private val schedule by lazy { ScheduleFragment() }
    private val communication by lazy { CommunicationFragment() }
    private val stats by lazy { StatsFragment() }
    private val my by lazy { MyFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bottomNav = setupMainNavigation()

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> show(home)
                R.id.nav_schedule -> show(schedule)
                R.id.nav_communication -> {
                    show(communication)
                    // 공지 탭에 들어가면 읽음 처리되므로 레드닷 제거.
                    bottomNav.removeBadge(R.id.nav_communication)
                }
                R.id.nav_stats -> show(stats)
                R.id.nav_my -> show(my)
                else -> return@setOnItemSelectedListener false
            }
            true
        }
        if (savedInstanceState == null) bottomNav.selectedItemId = R.id.nav_home
        loadNoticeBadge(bottomNav)

        // 알림 권한(Android 13+) 요청 후 FCM 토큰을 백엔드에 등록한다.
        registerNotifications()
    }

    override fun onStart() {
        super.onStart()
        syncMembership()
    }

    /**
     * 매장에서 내보내진 경우(사장이 멤버 삭제 → 소속 해제)를 감지해 매장 참여 화면으로 돌려보낸다.
     * 서버가 최신 소속을 알려주므로, workplaceId 가 사라졌으면 로컬 세션에서도 지우고 이동한다.
     * 네트워크 실패 등 불확실한 경우엔 아무 것도 하지 않는다(오탐으로 내쫓지 않음).
     */
    private fun syncMembership() {
        lifecycleScope.launch {
            val user = runCatching { screenModel.me() }.getOrNull() ?: return@launch
            TokenStore.updateUser(user)
            if (user.workplaceId == null) {
                toast("매장에서 내보내졌어요. 다시 매장에 참여해 주세요.", long = true)
                startActivity(
                    Intent(this@MainActivity, JoinStoreActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK),
                )
                finish()
            }
        }
    }

    /** 미확인 공지 여부(GET /api/notices/unread)를 소통 탭 레드닷으로 표시한다. */
    private fun loadNoticeBadge(bottomNav: BottomNavigationView) {
        val workplaceId = TokenStore.workplaceId
        if (workplaceId <= 0) return
        lifecycleScope.launch {
            val hasUnread = runCatching {
                screenModel.getNoticeUnread(workplaceId).hasUnread
            }.getOrDefault(false)
            if (hasUnread && bottomNav.selectedItemId != R.id.nav_communication) {
                bottomNav.getOrCreateBadge(R.id.nav_communication)
            }
        }
    }
}
