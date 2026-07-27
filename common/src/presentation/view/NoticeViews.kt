package com.example.ptmanagercommon.presentation.view

import com.example.ptmanagercommon.*
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.ptmanagercommon.data.*
import com.example.ptmanagercommon.domain.model.*

/** 조회 건수와 읽음 처리 순서를 목록·미리보기에서 함께 유지한다. */
internal suspend fun ViewGroup.loadNoticeCards(
    size: Int,
    preview: Boolean = false,
    bindActions: (View, NoticeDto) -> Unit,
) {
    val notices = screenModel.getNotices(TokenStore.workplaceId, page = 0, size = size).content
    if (BuildConfig.APP_ROLE == "EMPLOYEE") runCatching { screenModel.markNoticesRead() }
    renderItems(R.id.tv_notice_empty, if (preview) notices.take(1) else notices) { notice ->
        addItem(R.layout.item_notice) { card ->
            card.bindNotice(notice, preview)
            bindActions(card, notice)
        }
    }
}

internal fun Fragment.loadNoticePreview(root: View, bindActions: (View, NoticeDto) -> Unit) {
    val panel = root.findViewById<LinearLayout>(R.id.panel_notice)
    panel.removeAllExcept(R.id.tv_notice_empty)
    if (TokenStore.workplaceId <= 0) {
        root.findViewById<View>(R.id.tv_notice_empty).visibility = View.VISIBLE
        return
    }
    launchApi { panel.loadNoticeCards(size = 50, preview = true, bindActions = bindActions) }
}

internal fun Fragment.loadHandoverPreview(root: View) {
    val panel = root.findViewById<LinearLayout>(R.id.panel_handover)
    panel.removeAllExcept(R.id.tv_handover_empty)
    if (TokenStore.workplaceId <= 0) {
        root.findViewById<View>(R.id.tv_handover_empty).visibility = View.VISIBLE
        return
    }
    launchApi {
        val notes = screenModel.getHandovers(TokenStore.workplaceId, null)
        panel.renderItems(R.id.tv_handover_empty, notes.take(1)) { note ->
            panel.addItem(R.layout.item_handover) { card ->
                card.bindHandover(note, preview = true)
                card.setOnClickListener {
                    startActivity(Intent(requireContext(), HandoverListActivity::class.java))
                }
            }
        }
    }
}
