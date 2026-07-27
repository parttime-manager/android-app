package com.example.ptmanagercommon.presentation.view

import com.example.ptmanagercommon.*
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.ptmanagercommon.data.Extras
import com.example.ptmanagercommon.domain.model.HandoverDto
import com.example.ptmanagercommon.data.TokenStore

/** 인수인계 노트 목록. 카테고리 필터 + 작성 진입 + 본인 노트 삭제. */
class HandoverListActivity : AppCompatActivity() {

    // 선택된 카테고리 코드(null = 전체).
    private var selectedCategory: String? = null

    // 칩 뷰 id → 카테고리 코드(전체는 null).
    private val chips: List<Pair<Int, String?>> by lazy {
        listOf(
            R.id.chip_all to null,
            R.id.chip_stock to "STOCK",
            R.id.chip_device to "DEVICE",
            R.id.chip_customer to "CUSTOMER",
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupScreen(R.layout.activity_handover_list, R.id.handover_list_root)
        findViewById<View>(R.id.btn_write).setOnClickListener {
            startActivity(Intent(this, HandoverWriteActivity::class.java))
        }
        bindChips(chips, selectedCategory) { category ->
            selectedCategory = category
            loadHandovers()
        }
    }

    override fun onResume() {
        super.onResume()
        // 작성/삭제 후 돌아왔을 때 갱신.
        loadHandovers()
    }

    private fun loadHandovers() {
        val workplaceId = TokenStore.workplaceId
        val container = findViewById<LinearLayout>(R.id.handover_container)
        val empty = findViewById<TextView>(R.id.tv_handover_empty)
        // 기존 행 제거(빈 상태 뷰는 유지).
        container.removeAllExcept(R.id.tv_handover_empty)
        if (workplaceId <= 0) {
            empty.visibility = View.VISIBLE
            return
        }
        launchApi {
            val notes = screenModel.getHandovers(workplaceId, selectedCategory)
            container.renderItems(R.id.tv_handover_empty, notes) { note ->
                container.addItem(R.layout.item_handover) { card ->
                    card.bindHandover(note)
                    val delete = card.findViewById<View>(R.id.btn_delete)
                    // 본인이 쓴 노트만 삭제 가능(서버도 동일 규칙).
                    if (note.authorId == TokenStore.userId) {
                        delete.visibility = View.VISIBLE
                        delete.setOnClickListener { confirmDelete(note) }
                    }
                    card.setOnClickListener { openDetail(note) }
                }
            }
        }
    }

    private fun openDetail(note: HandoverDto) {
        startActivity(
            Intent(this, HandoverDetailActivity::class.java)
                .putExtra(Extras.HANDOVER_ID, note.id)
                .putExtra(Extras.HANDOVER_CATEGORY, note.category)
                .putExtra(Extras.HANDOVER_TITLE, note.title)
                .putExtra(Extras.HANDOVER_CONTENT, note.content)
                .putExtra(Extras.HANDOVER_AUTHOR_ID, note.authorId ?: -1L)
                .putExtra(Extras.HANDOVER_AUTHOR_NAME, note.authorName)
                .putExtra(Extras.HANDOVER_CREATED_AT, note.createdAt)
        )
    }

    private fun confirmDelete(note: HandoverDto) = deleteHandover(note.id) { loadHandovers() }
}
