package com.example.ptmanageremployer.presentation.view

import com.example.ptmanageremployer.*
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.ptmanageremployer.domain.model.CreateWorkplaceRequest
import com.example.ptmanageremployer.data.Network
import com.example.ptmanageremployer.data.TokenStore

class CreateStoreActivity : AppCompatActivity() {

    private val screenModel by lazy { com.example.ptmanageremployer.AppGraph.model(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupScreen(R.layout.activity_create_store, R.id.create_root)

        val inputState = findViewById<View>(R.id.state_input)
        val codeState = findViewById<View>(R.id.state_code)
        val nameInput = findViewById<EditText>(R.id.input_store)
        val typeInput = findViewById<EditText>(R.id.input_type)
        val codeView = findViewById<TextView>(R.id.tv_invite_code)

        findViewById<TextView>(R.id.btn_create).setOnClickListener { btn ->
            val name = nameInput.text.toString().trim()
            if (name.isEmpty()) {
                toast("매장 이름을 입력해 주세요.")
                return@setOnClickListener
            }
            val address = typeInput.text.toString().trim().ifBlank { null }
            launchApi(btn) {
                val workplace = screenModel.createWorkplace(CreateWorkplaceRequest(name, address))
                TokenStore.setWorkplaceId(workplace.id)
                codeView.text = workplace.inviteCode ?: "------"
                inputState.isVisible = false
                codeState.isVisible = true
            }
        }
        findViewById<View>(R.id.btn_enter_app).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finishAffinity()
        }
    }
}
