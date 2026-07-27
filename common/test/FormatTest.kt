package com.example.ptmanagercommon

import com.example.ptmanagercommon.presentation.view.*

import com.example.ptmanagercommon.data.*
import com.example.ptmanagercommon.domain.model.*
import com.example.ptmanagercommon.data.shiftMinutes
import com.example.ptmanagercommon.data.weekBucketIndex
import org.junit.Assert.assertEquals
import com.example.ptmanagercommon.data.shiftHours
import org.junit.Test
import java.time.LocalDate

/** 화면 여러 곳에서 함께 쓰는 Format 헬퍼들의 동작 고정. */
class FormatTest {

    @Test
    fun shiftMinutes_counts_overnight_shift_across_midnight() {
        assertEquals(300, shiftMinutes("18:00:00", "23:00:00"))
        assertEquals(480, shiftMinutes("22:00:00", "06:00:00"))
        assertEquals(0, shiftMinutes(null, "06:00:00"))
        assertEquals(0, shiftMinutes("22:00", "이상한값"))
    }

    @Test
    fun weekBucketIndex_matches_backend_buckets() {
        val bucket = { day: Int -> weekBucketIndex(LocalDate.of(2026, 7, day)) }
        assertEquals(0, bucket(1))
        assertEquals(0, bucket(7))
        assertEquals(1, bucket(8))
        assertEquals(2, bucket(21))
        assertEquals(3, bucket(22))
        assertEquals(3, bucket(31))
    }

    @Test
    fun metaLine_drops_blank_parts() {
        assertEquals("김알바 · 2026-07-01", metaLine("김알바", "2026-07-01T10:00:00", "작성자"))
        assertEquals("작성자", metaLine(null, null, "작성자"))
        assertEquals("작성자 · 2026-07-01", metaLine(null, "2026-07-01T10:00:00", "작성자"))
    }
    @Test
    fun equal_start_and_end_keep_each_apps_existing_duration_rule() {
        assertEquals(0L, shiftMinutes("09:00:00", "09:00:00"))
        assertEquals(24.0, shiftHours("09:00:00", "09:00:00"), 0.0)
        assertEquals(8.0, shiftHours("22:00:00", "06:00:00"), 0.0)
        assertEquals(0.0, shiftHours(null, "06:00:00"), 0.0)
        assertEquals(0.0, shiftHours("09:00", "invalid"), 0.0)
        assertEquals(0L, shiftMinutes("09:00:00", "09:00:30"))
        assertEquals(24.0, shiftHours("09:00:00", "09:00:30"), 0.0)
    }
    @Test
    fun commonDisplayFormattingKeepsNullsRemaindersAndRoleDefaults() {
        assertEquals("0시간", durationLabel(0))
        assertEquals("2시간", durationLabel(120))
        assertEquals("2시간 5분", durationLabel(125))
        assertEquals("2026-10-21 09:07", dateTimeLabel("2026-10-21T09:07:59+09:00"))
        assertEquals("", dateTimeLabel(null))
        assertEquals("알바", roleLabel(UserDto(1, role = "EMPLOYEE")))
        assertEquals("사장님", roleLabel(UserDto(1, role = "EMPLOYER")))
        assertEquals("user@example.test", roleLabel(UserDto(1, email = "user@example.test")))
    }

    @Test
    fun noticeDetailKeepsDifferentMissingAuthorAndEmptyTextRules() {
        val notice = NoticeDto(id = 1, createdAt = "2026-10-21T09:07:59Z")
        assertEquals("2026-10-21 09:07", noticeDetailMeta(notice, employer = false))
        assertEquals("사장님 · 2026-10-21 09:07", noticeDetailMeta(notice, employer = true))
        val blankAuthor = notice.copy(authorName = "")
        assertEquals(" · 2026-10-21 09:07", noticeDetailMeta(blankAuthor, employer = false))
        assertEquals("2026-10-21 09:07", noticeDetailMeta(blankAuthor, employer = true))
        assertEquals("", noticeDetailMeta(NoticeDto(id = 1), employer = false))
        assertEquals("사장님", noticeDetailMeta(NoticeDto(id = 1), employer = true))
    }
}
