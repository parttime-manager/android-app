package com.example.ptmanagercommon

import com.example.ptmanagercommon.presentation.view.*

import com.example.ptmanagercommon.domain.model.SignupRequest
import com.example.ptmanagercommon.data.swapStatusLabel
import org.junit.Assert.assertEquals
import org.junit.Test

class AppRoleTest {
    @Test
    fun signup_and_notification_titles_keep_the_apps_role() {
        val employee = BuildConfig.APP_ROLE == "EMPLOYEE"
        assertEquals(BuildConfig.APP_ROLE, SignupRequest("a@example.com", "password", "이름", BuildConfig.APP_ROLE).role)
        assertEquals(if (employee) "🙋 대타 지원" else "🔁 대타 지원", notificationTitle("SWAP_APPLICATION"))
        assertEquals(if (employee) "👤 가입 신청" else "🙋 가입 신청", notificationTitle("JOIN_REQUEST"))
        assertEquals(if (employee) "🕘 출근 알림" else "⚠️ 근태 이상", notificationTitle("ATTENDANCE"))
        assertEquals(if (employee) "📢 새 공지" else "📢 공지", notificationTitle("NOTICE"))
        assertEquals(if (employee) "대기 중" else "PENDING", swapStatusLabel("PENDING"))
    }
}
