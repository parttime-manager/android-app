package com.example.ptmanagercommon.presentation.view

import com.example.ptmanagercommon.BuildConfig

internal fun notificationTitle(type: String?): String = when (type) {
    "SWAP_REQUEST" -> "🔁 대타요청"
    "SWAP_APPLICATION" -> if (BuildConfig.APP_ROLE == "EMPLOYEE") "🙋 대타 지원" else "🔁 대타 지원"
    "SWAP_RESULT" -> "🔁 대타 결과"
    "SCHEDULE_CHANGED" -> "🗓 근무 편성 변경"
    "JOIN_REQUEST" -> if (BuildConfig.APP_ROLE == "EMPLOYEE") "👤 가입 신청" else "🙋 가입 신청"
    "ATTENDANCE" -> if (BuildConfig.APP_ROLE == "EMPLOYEE") "🕘 출근 알림" else "⚠️ 근태 이상"
    "NOTICE" -> if (BuildConfig.APP_ROLE == "EMPLOYEE") "📢 새 공지" else "📢 공지"
    else -> "🔔 알림"
}
