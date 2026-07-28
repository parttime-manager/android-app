package com.example.ptmanagercommon.presentation.view

import com.example.ptmanagercommon.*
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.ptmanagercommon.data.TokenStore
import com.example.ptmanagercommon.domain.model.UpdateProfileRequest

class ProfileEditActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupScreen(R.layout.activity_profile_edit, R.id.profile_root)

        val nameInput = findViewById<EditText>(R.id.input_name)
        nameInput.setText(TokenStore.name ?: "")

        findViewById<TextView>(R.id.btn_save).setOnClickListener { btn ->
            val name = nameInput.text.toString().trim()
            if (name.isEmpty()) {
                toast("이름을 입력해 주세요.")
                return@setOnClickListener
            }
            launchApi(btn) {
                TokenStore.updateUser(screenModel.updateProfile(UpdateProfileRequest(name)))
                toast("저장되었습니다")
                finish()
            }
        }
    }
}
