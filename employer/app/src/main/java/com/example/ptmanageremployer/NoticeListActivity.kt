package com.example.ptmanageremployer

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.ptmanageremployer.data.Extras
import com.example.ptmanageremployer.data.Network
import com.example.ptmanageremployer.data.NoticeDto
import com.example.ptmanageremployer.data.TokenStore
import com.example.ptmanageremployer.data.noticeMeta

/** 매장 공지 목록. 조회(탭 → 상세) + 삭제(사장) + 작성 진입. */
class NoticeListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_notice_list)
        findViewById<View>(R.id.notice_list_root).applySystemBarInsets()
        findViewById<View>(R.id.btn_back).setOnClickListener { finish() }
        findViewById<View>(R.id.btn_write).setOnClickListener {
            startActivity(Intent(this, NoticeWriteActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        // 작성/삭제 후 돌아왔을 때 최신 목록으로 갱신한다.
        loadNotices()
    }

    private fun loadNotices() {
        val workplaceId = TokenStore.workplaceId
        val container = findViewById<LinearLayout>(R.id.notice_container)
        val empty = findViewById<TextView>(R.id.tv_notice_empty)
        // 기존에 그려둔 공지 행을 비우고(빈 상태 뷰는 유지) 다시 그린다.
        container.removeAllExcept(R.id.tv_notice_empty)
        if (workplaceId <= 0) {
            empty.visibility = View.VISIBLE
            return
        }
        launchApi {
            val notices = Network.api.getNotices(workplaceId, page = 0, size = 50).content
            if (notices.isEmpty()) {
                empty.visibility = View.VISIBLE
                return@launchApi
            }
            empty.visibility = View.GONE
            notices.forEach { notice ->
                container.addItem(R.layout.item_notice) { card ->
                    card.text(R.id.tv_title, notice.title ?: "(제목 없음)")
                    card.text(R.id.tv_content, notice.body)
                    card.text(R.id.tv_meta, noticeMeta(notice))
                    card.findViewById<View>(R.id.notice_body).setOnClickListener {
                        startActivity(
                            Intent(this@NoticeListActivity, NoticeDetailActivity::class.java)
                                .putExtra(Extras.NOTICE_ID, notice.id)
                        )
                    }
                    card.findViewById<View>(R.id.btn_delete).setOnClickListener {
                        confirmDelete(notice)
                    }
                }
            }
        }
    }

    private fun confirmDelete(notice: NoticeDto) {
        confirm("공지 삭제", "'${notice.title ?: "이 공지"}'를 삭제할까요?", "삭제") {
            launchApi {
                Network.api.deleteNotice(notice.id)
                toast("공지를 삭제했어요")
                loadNotices()
            }
        }
    }
}
