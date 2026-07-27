package com.example.ptmanageremployee.presentation.view

import com.example.ptmanageremployee.*
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.ptmanageremployee.data.Extras
import com.example.ptmanageremployee.data.TokenStore

/** 직원용 공지 전체 목록(읽기 전용). 탭 → 상세. */
class NoticeListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupScreen(R.layout.activity_notice_list, R.id.notice_list_root)
    }

    override fun onResume() {
        super.onResume()
        loadNotices()
    }

    private fun loadNotices() {
        val workplaceId = TokenStore.workplaceId
        val container = findViewById<LinearLayout>(R.id.notice_container)
        val empty = findViewById<TextView>(R.id.tv_notice_empty)
        container.removeAllExcept(R.id.tv_notice_empty)
        if (workplaceId <= 0) {
            empty.visibility = View.VISIBLE
            return
        }
        launchApi {
            container.loadNoticeCards(size = 100) { card, notice ->
                card.setOnClickListener {
                    startActivity(Intent(this@NoticeListActivity, NoticeDetailActivity::class.java)
                        .putExtra(Extras.NOTICE_ID, notice.id))
                }
            }
        }
    }
}
