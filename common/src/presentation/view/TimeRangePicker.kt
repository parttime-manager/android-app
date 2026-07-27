package com.example.ptmanagercommon.presentation.view

import com.example.ptmanagercommon.*
import android.app.TimePickerDialog
import android.content.Context

internal fun Context.pickTimeRange(
    startHour: Int = 18, startMinute: Int = 0,
    endHour: Int = 22, endMinute: Int = 0,
    onPicked: (String, String) -> Unit,
) {
    TimePickerDialog(this, { _, sh, sm ->
        TimePickerDialog(this, { _, eh, em ->
            onPicked("%02d:%02d:00".format(sh, sm), "%02d:%02d:00".format(eh, em))
        }, endHour, endMinute, true).apply { setTitle("종료 시간") }.show()
    }, startHour, startMinute, true).apply { setTitle("시작 시간") }.show()
}
