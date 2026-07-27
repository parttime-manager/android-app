package com.example.ptmanageremployee.presentation.view

import com.example.ptmanageremployee.*
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.ptmanageremployee.domain.model.CreateJoinRequest
import com.example.ptmanageremployee.data.Network
import com.example.ptmanageremployee.data.TokenStore
import com.example.ptmanageremployee.data.toUserMessage
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class JoinStoreActivity : AppCompatActivity() {

    private val screenModel by lazy { com.example.ptmanageremployee.AppGraph.model(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupScreen(R.layout.activity_join_store, R.id.join_root)

        val pendingState = findViewById<View>(R.id.state_pending)
        val inputState = findViewById<View>(R.id.state_input)
        val codeInput = findViewById<EditText>(R.id.input_code)
        val submitBtn = findViewById<TextView>(R.id.btn_submit_code)

        submitBtn.setOnClickListener {
            val code = codeInput.text.toString().trim()
            if (code.isEmpty()) {
                toast("초대 코드를 입력해 주세요.")
                return@setOnClickListener
            }
            submitBtn.isEnabled = false
            lifecycleScope.launch {
                try {
                    screenModel.createJoinRequest(CreateJoinRequest(code))
                    inputState.visibility = View.GONE
                    pendingState.visibility = View.VISIBLE
                } catch (e: Exception) {
                    toast(e.toUserMessage())
                } finally {
                    submitBtn.isEnabled = true
                }
            }
        }

        // 승인 여부를 /me 로 확인한 뒤 메인으로 진입한다. (수동 확인)
        findViewById<View>(R.id.btn_enter_app).setOnClickListener { btn ->
            btn.isEnabled = false
            lifecycleScope.launch {
                try {
                    if (!enterMainIfApproved()) {
                        toast("아직 사장님 승인 대기 중입니다.")
                        btn.isEnabled = true
                    }
                } catch (e: Exception) {
                    toast(e.toUserMessage())
                    btn.isEnabled = true
                }
            }
        }

        // 대기 화면이 떠 있는 동안 승인 여부를 폴링해 자동으로 메인으로 넘어간다.
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                while (isActive) {
                    if (pendingState.visibility == View.VISIBLE) {
                        runCatching { enterMainIfApproved() }
                    }
                    delay(5_000)
                }
            }
        }
    }

    /** /me 로 승인 여부를 확인해 승인됐으면 메인으로 진입하고 true 를 반환한다. */
    private suspend fun enterMainIfApproved(): Boolean {
        val me = screenModel.me()
        TokenStore.updateUser(me)
        if (TokenStore.workplaceId <= 0) return false
        startActivity(Intent(this, MainActivity::class.java))
        finishAffinity()
        return true
    }
}
