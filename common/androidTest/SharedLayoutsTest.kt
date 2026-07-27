package com.example.ptmanagercommon

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.ptmanagercommon.presentation.view.HandoverWriteActivity
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

/** Exercise the real Android inflater and text layout in both app namespaces. */
@RunWith(AndroidJUnit4::class)
class SharedLayoutsTest {
    @Test
    fun sharedScreensFitSmallPhonesAndTablets() {
        ActivityScenario.launch(HandoverWriteActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val layouts = linkedMapOf(
                    "schedule" to activity.resources.getIdentifier(
                        if (BuildConfig.APP_ROLE == "EMPLOYEE") "fragment_schedule" else "fragment_schedule_edit",
                        "layout", activity.packageName,
                    ),
                    "payroll" to R.layout.fragment_stats,
                    "communication" to activity.resources.getIdentifier(
                        if (BuildConfig.APP_ROLE == "EMPLOYEE") "fragment_communication" else "fragment_sub",
                        "layout", activity.packageName,
                    ),
                    "shift" to R.layout.activity_shift_detail,
                    "swap" to if (BuildConfig.APP_ROLE == "EMPLOYEE")
                        activity.resources.getIdentifier("activity_swap_detail", "layout", activity.packageName)
                        else activity.resources.getIdentifier("activity_sub_approval", "layout", activity.packageName),
                    "members" to R.layout.activity_members,
                    "notice" to R.layout.activity_notice_list,
                    "my" to R.layout.fragment_my,
                )
                val density = activity.resources.displayMetrics.density
                val output = File(activity.getExternalFilesDir(null), "ui-review").apply { mkdirs() }
                for (widthDp in listOf(320, 375, 414, 768)) {
                    for ((name, layout) in layouts) {
                        val root = LayoutInflater.from(activity).inflate(layout, FrameLayout(activity), false)
                        fun sample(name: String, value: String) {
                            val id = activity.resources.getIdentifier(name, "id", activity.packageName)
                            root.findViewById<TextView>(id)?.text = value
                        }
                        sample("tv_date", "2026-11-03 화요일")
                        sample("tv_time", "09:00 – 18:00")
                        sample("tv_week_range", "11월 2일 – 11월 8일")
                        sample("tv_month_selector", "11월 ▾")
                        sample("tv_pay_amount", "1,234,567원")
                        sample("tv_month_total", "1,234,567원")
                        sample("tv_pay_sub", "80시간 근무 · 시급 10,320원")
                        sample("tv_month_label", "2026-11 실근태 기준")
                        sample("tv_sum_shifts", "12회")
                        sample("tv_sum_checkin", "10회")
                        sample("tv_sum_pay", "825,600원")
                        sample("tv_sum_cost", "825,600원")
                        sample("tv_sum_staff", "3명")
                        sample("tv_worker", "김민수 · 박서연")
                        sample("tv_my_name", "김민수")
                        sample("tv_my_sub", "시청점 · 매장 멤버")
                        sample("tv_shift", "11월 3일 09:00 – 18:00")
                        sample("tv_status", "상태 · 대기 중")
                        sample("tv_reason", "개인 일정으로 근무 변경이 필요해요. 가능한 분은 지원해 주세요. ".repeat(8))
                        for (id in listOf(R.id.tv_notice_empty, R.id.tv_handover_empty, R.id.tv_swap_empty)) {
                            root.findViewById<View>(id)?.visibility = View.VISIBLE
                        }
                        if (BuildConfig.APP_ROLE == "EMPLOYER") {
                            root.findViewById<View>(R.id.applicant_section)?.visibility = View.VISIBLE
                            sample("tv_applicants", "김민수 · 박서연 (2명)")
                        }
                        val width = (widthDp * density).toInt()
                        val height = (900 * density).toInt()
                        root.measure(
                            View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                            View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY),
                        )
                        root.layout(0, 0, width, height)
                        fun check(view: View) {
                            if (view.visibility != View.VISIBLE) return
                            if (view is TextView && view.text.isNotEmpty()) {
                                val available = view.width - view.compoundPaddingLeft - view.compoundPaddingRight
                                view.layout?.let { text ->
                                    for (line in 0 until text.lineCount) {
                                        assertTrue("$name at $widthDp dp clips '${view.text}'", text.getLineWidth(line) <= available + 1)
                                    }
                                }
                            }
                            if (view is ViewGroup) for (i in 0 until view.childCount) check(view.getChildAt(i))
                        }
                        check(root)
                        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                        root.draw(Canvas(bitmap))
                        File(output, "$name-$widthDp.png").outputStream().use {
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                        }
                        bitmap.recycle()
                    }
                }
            }
        }
    }
}
