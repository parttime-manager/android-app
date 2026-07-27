package com.example.ptmanagercommon.presentation.view

import com.example.ptmanagercommon.*
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.ptmanagercommon.data.*
import com.example.ptmanagercommon.domain.model.*

internal fun View.bindNotice(notice: NoticeDto, preview: Boolean = false) {
    text(R.id.tv_title, notice.title ?: "(제목 없음)")
    bindContent(notice.body, preview)
    text(R.id.tv_meta, noticeMeta(notice))
}

internal fun View.bindHandover(note: HandoverDto, preview: Boolean = false) {
    text(R.id.tv_category, handoverCategoryLabel(note.category))
    text(R.id.tv_title, note.title)
    bindContent(note.content, preview)
    text(R.id.tv_meta, handoverMeta(note))
}

private fun View.bindContent(content: String?, preview: Boolean) {
    findViewById<TextView>(R.id.tv_content).apply {
        if (preview) maxLines = 1
        text = if (preview) content.orEmpty().replace("\n", " ").replace("\r", " ").trim() else content
    }
}

internal fun View.bindPayroll(item: PayrollItem) {
    text(R.id.tv_name, item.employeeName ?: "직원 #${item.employeeId}")
    text(R.id.tv_hours, "${(item.workedMinutes ?: 0) / 60}h")
    text(R.id.tv_amount, won(item.amount ?: 0))
}

internal fun ViewGroup.addSwapCaption(text: String) {
    addView(TextView(context).apply {
        this.text = text
        setTextColor(ContextCompat.getColor(context, R.color.cat_swap))
        textSize = 12f
        setTypeface(typeface, android.graphics.Typeface.BOLD)
        setPadding(0, (14 * resources.displayMetrics.density).toInt(), 0, 0)
    })
}

internal fun View.bindMember(member: UserDto, subtitle: String = roleLabel(member)) {
    text(R.id.tv_name, member.name ?: "이름 없음")
    text(R.id.tv_sub, subtitle)
}
