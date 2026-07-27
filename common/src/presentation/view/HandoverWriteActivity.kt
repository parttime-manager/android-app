package com.example.ptmanagercommon.presentation.view

import com.example.ptmanagercommon.*
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.ptmanagercommon.domain.model.CreateHandoverRequest
import com.example.ptmanagercommon.data.TokenStore

/** 인수인계 노트 작성. 분류(칩) 1개 선택 + 내용. */
class HandoverWriteActivity : AppCompatActivity() {

    private var selectedCategory: String = "STOCK"

    private val chips: List<Pair<Int, String>> by lazy {
        listOf(
            R.id.chip_stock to "STOCK",
            R.id.chip_device to "DEVICE",
            R.id.chip_customer to "CUSTOMER",
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupScreen(R.layout.activity_handover_write, R.id.write_root)

        bindChips(chips, selectedCategory) { category ->
            selectedCategory = category
        }

        val titleInput = findViewById<EditText>(R.id.input_title)
        val contentInput = findViewById<EditText>(R.id.input_content)
        findViewById<View>(R.id.btn_publish).setOnClickListener { btn ->
            val title = titleInput.text.toString().trim()
            val content = contentInput.text.toString().trim()
            if (title.isEmpty()) {
                toast("제목을 입력해 주세요.")
                return@setOnClickListener
            }
            if (content.isEmpty()) {
                toast("내용을 입력해 주세요.")
                return@setOnClickListener
            }
            val workplaceId = TokenStore.workplaceId
            if (workplaceId <= 0) {
                toast("소속 매장이 없습니다.")
                return@setOnClickListener
            }
            launchApi(btn) {
                screenModel.createHandover(
                    CreateHandoverRequest(workplaceId, selectedCategory, title, content),
                )
                toast("등록했어요")
                finish()
            }
        }
    }
}
