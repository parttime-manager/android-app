package com.example.ptmanagercommon.presentation.view

import com.example.ptmanagercommon.*
import android.content.Intent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.ptmanagercommon.domain.model.NotificationSettingUpdate
import com.example.ptmanagercommon.data.Push
import com.example.ptmanagercommon.data.TokenStore
import kotlinx.coroutines.launch

internal fun Fragment.openNotificationSetting() {
    lifecycleScope.launch {
        val s = runCatching { screenModel.getNotificationSetting() }.getOrNull()
        if (s == null) {
            toast("알림 설정을 불러오지 못했습니다.")
            return@launch
        }
        val labels = arrayOf("대타 알림", "공지 알림", "출퇴근 알림", "가입 신청 알림")
        val checked = booleanArrayOf(
            s.swapEnabled, s.noticeEnabled, s.attendanceEnabled, s.joinRequestEnabled,
        )
        AlertDialog.Builder(requireContext())
            .setTitle("알림 설정")
            .setMultiChoiceItems(labels, checked) { _, which, isChecked -> checked[which] = isChecked }
            .setPositiveButton("저장") { _, _ ->
                launchApi {
                    screenModel.updateNotificationSetting(
                        NotificationSettingUpdate(checked[0], checked[1], checked[2], checked[3]),
                    )
                    toast("알림 설정을 저장했어요")
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }
}

internal fun Fragment.logout() {
    lifecycleScope.launch {
        runCatching { Push.currentToken()?.let { screenModel.deleteDeviceToken(it) } }
        runCatching { screenModel.logout() }
        Push.invalidateLocalToken()
        TokenStore.clear()
        startActivity(Intent(requireContext(), LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
    }
}

internal fun Fragment.bindAccountActions(view: View) {
    view.findViewById<View>(R.id.row_profile).setOnClickListener {
        startActivity(Intent(requireContext(), ProfileEditActivity::class.java))
    }
    view.findViewById<View>(R.id.row_members).setOnClickListener {
        startActivity(Intent(requireContext(), MembersActivity::class.java))
    }
    view.findViewById<View>(R.id.row_noti).setOnClickListener { openNotificationSetting() }
    view.findViewById<View>(R.id.row_logout).setOnClickListener { logout() }
}

internal fun AppCompatActivity.deleteHandover(id: Long, onDeleted: () -> Unit) {
    confirm("인수인계 삭제", "이 노트를 삭제할까요?", "삭제") {
        launchApi {
            screenModel.deleteHandover(id)
            toast("삭제했어요")
            onDeleted()
        }
    }
}
