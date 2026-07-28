package com.example.ptmanageremployer.presentation.view

import com.example.ptmanageremployer.*
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ptmanageremployer.data.TokenStore
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val home by lazy { HomeFragment() }
    private val schedule by lazy { ScheduleEditFragment() }
    private val approval by lazy { SubFragment() }
    private val stats by lazy { StatsFragment() }
    private val my by lazy { MyFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bottomNav = setupMainNavigation()

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> show(home)
                R.id.nav_schedule -> show(schedule)
                R.id.nav_approval -> show(approval)
                R.id.nav_stats -> show(stats)
                R.id.nav_my -> show(my)
                else -> return@setOnItemSelectedListener false
            }
            true
        }
        if (savedInstanceState == null) bottomNav.selectedItemId = R.id.nav_home

        // 최신 계정 정보로 로컬 세션 동기화(GET /api/auth/me) — 매장 소속·역할 갱신.
        lifecycleScope.launch {
            runCatching { screenModel.me() }.getOrNull()?.let { TokenStore.updateUser(it) }
        }

        // 알림 권한(Android 13+) 요청 후 FCM 토큰을 백엔드에 등록한다.
        registerNotifications()
    }
}
