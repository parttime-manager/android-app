package com.example.ptmanagercommon.presentation.view

import com.example.ptmanagercommon.*
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.ptmanagercommon.data.Extras
import com.example.ptmanagercommon.data.TokenStore
import com.example.ptmanagercommon.data.handoverCategoryLabel
import com.example.ptmanagercommon.data.metaLine

/** 인수인계 노트 상세. 목록에서 넘겨받은 값을 그대로 표시한다(별도 단건 조회 API 없음). */
class HandoverDetailActivity : AppCompatActivity() {

    private var handoverId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupScreen(R.layout.activity_handover_detail, R.id.detail_root)

        handoverId = intent.getLongExtra(Extras.HANDOVER_ID, -1)
        val category = intent.getStringExtra(Extras.HANDOVER_CATEGORY)
        val title = intent.getStringExtra(Extras.HANDOVER_TITLE)
        val content = intent.getStringExtra(Extras.HANDOVER_CONTENT)
        val authorId = intent.getLongExtra(Extras.HANDOVER_AUTHOR_ID, -1)
        val authorName = intent.getStringExtra(Extras.HANDOVER_AUTHOR_NAME)
        val createdAt = intent.getStringExtra(Extras.HANDOVER_CREATED_AT)

        text(R.id.tv_category, handoverCategoryLabel(category))
        text(R.id.tv_title, title)
        text(R.id.tv_meta, metaLine(authorName, createdAt, "작성자"))
        text(R.id.tv_content, content)

        // 본인이 쓴 노트만 삭제 가능(서버도 동일 규칙).
        if (handoverId > 0 && authorId == TokenStore.userId) {
            findViewById<TextView>(R.id.btn_delete).apply {
                visibility = View.VISIBLE
                setOnClickListener { confirmDelete() }
            }
        }
    }

    private fun confirmDelete() = deleteHandover(handoverId) { finish() }
}
