package com.example.ptmanagercommon.presentation.view

import com.example.ptmanagercommon.*
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ptmanagercommon.data.*
import com.example.ptmanagercommon.domain.model.*
import kotlinx.coroutines.launch

class NoticeDetailActivity : AppCompatActivity() {

    private val screenModel by lazy { com.example.ptmanagercommon.AppGraph.model(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupScreen(R.layout.activity_notice_detail, R.id.detail_root)
        val employer = BuildConfig.APP_ROLE == "EMPLOYER"
        val noticeId = intent.getLongExtra(Extras.NOTICE_ID, -1)
        if (noticeId <= 0) {
            if (employer) {
                toast("공지를 찾을 수 없습니다.")
                finish()
            }
            return
        }
        lifecycleScope.launch {
            try {
                val notice = screenModel.getNotice(noticeId)
                text(R.id.tv_title, if (employer) notice.title ?: "(제목 없음)" else notice.title)
                text(R.id.tv_meta, noticeDetailMeta(notice, employer))
                text(R.id.tv_body, notice.body)
                renderAttachments(notice.attachments.orEmpty())
            } catch (e: Exception) {
                toast(e.toUserMessage())
                if (employer) finish()
            }
        }
    }
}
