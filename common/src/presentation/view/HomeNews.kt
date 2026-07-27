package com.example.ptmanagercommon.presentation.view

import com.example.ptmanagercommon.*
import android.view.View
import com.example.ptmanagercommon.data.*
import com.example.ptmanagercommon.domain.model.*

internal suspend fun View.loadLatestNotice(workplaceId: Long) {
    val notice = runCatching {
        screenModel.getNotices(workplaceId, size = 1).content.firstOrNull()
    }.getOrNull()
    text(R.id.tv_notice_time, relativeTime(notice?.createdAt))
    text(R.id.tv_notice_title, when {
        notice == null -> context.getString(R.string.home_no_notice)
        BuildConfig.APP_ROLE == "EMPLOYEE" -> notice.title ?: context.getString(R.string.common_no_title)
        else -> notice.title ?: context.getString(R.string.home_no_notice)
    })
    text(R.id.tv_notice_body, notice?.body)
}

internal suspend fun View.loadLatestHandover(workplaceId: Long) {
    val note = runCatching {
        screenModel.getHandovers(workplaceId).maxByOrNull { it.createdAt ?: "" }
    }.getOrNull()
    text(R.id.tv_handover_time, relativeTime(note?.createdAt))
    text(R.id.tv_handover_title, when {
        note == null -> context.getString(R.string.home_no_handover)
        BuildConfig.APP_ROLE == "EMPLOYEE" -> note.title ?: context.getString(R.string.common_no_title)
        else -> note.title ?: context.getString(R.string.home_no_handover)
    })
    text(R.id.tv_handover_content, note?.content)
}
