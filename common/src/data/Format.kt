package com.example.ptmanagercommon.data

import com.example.ptmanagercommon.domain.model.*
import com.example.ptmanagercommon.BuildConfig
import com.example.ptmanagercommon.R
import java.text.NumberFormat
import java.time.DayOfWeek
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

/** Intent 로 화면 간 전달하는 키 모음. */
object Extras {
    const val SHIFT_ID = "extra_shift_id"
    const val NOTICE_ID = "extra_notice_id"
    const val SWAP_REQUEST_ID = "extra_swap_request_id"
    const val HANDOVER_ID = "extra_handover_id"
    const val HANDOVER_CATEGORY = "extra_handover_category"
    const val HANDOVER_TITLE = "extra_handover_title"
    const val HANDOVER_CONTENT = "extra_handover_content"
    const val HANDOVER_AUTHOR_ID = "extra_handover_author_id"
    const val HANDOVER_AUTHOR_NAME = "extra_handover_author_name"
    const val HANDOVER_CREATED_AT = "extra_handover_created_at"
}

/** 인수인계 카테고리 코드 → 한글 태그 라벨. (STOCK/DEVICE/CUSTOMER) */
fun handoverCategoryLabel(code: String?): String = when (code) {
    "STOCK" -> "#재고"
    "DEVICE" -> "#기기오류"
    "CUSTOMER" -> "#손님"
    else -> "#기타"
}

/** 백엔드 인건비 집계와 동일한 주차 버킷: 1–7→0, 8–14→1, 15–21→2, 22–말일→3 */
fun weekBucketIndex(date: LocalDate): Int = ((date.dayOfMonth - 1) / 7).coerceAtMost(3)

/** 그 날짜가 속한 주(월~일)의 월요일. */
fun mondayOf(date: LocalDate): LocalDate = date.with(DayOfWeek.MONDAY)

/** 주 시작(월)~일요일 범위 라벨: "6/30 – 7/6" */
fun weekRangeLabel(monday: LocalDate): String {
    val sunday = monday.plusDays(6)
    return "${monday.monthValue}/${monday.dayOfMonth} – ${sunday.monthValue}/${sunday.dayOfMonth}"
}

/** LocalDate → "7월 5일 (일) 근무" (선택한 날짜 라벨). */
fun scheduleDateLabel(date: LocalDate): String {
    val dow = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN)
    return "${date.monthValue}월 ${date.dayOfMonth}일 ($dow) 근무"
}

/** "18:00:00" + "23:00:00" → "18:00 – 23:00" */
fun shiftTimeRange(start: String?, end: String?): String {
    val s = start?.take(5).orEmpty()
    val e = end?.take(5).orEmpty()
    return if (s.isBlank() && e.isBlank()) "" else "$s – $e"
}

/** 근무 시간(분). 종료가 시작보다 이르면 자정 넘김으로 보고 +24h. 파싱 실패 시 0. */
fun shiftMinutes(start: String?, end: String?): Long {
    val mins = timeDifferenceMinutes(start, end) ?: return 0
    return if (mins < 0) mins + 24 * 60 else mins
}

/** ISO 시각(오프셋 유무 모두) → "방금 / N분 전 / N시간 전 / N일 전 / M/d". 파싱 실패 시 "". */
fun relativeTime(iso: String?): String {
    val instant = runCatching { OffsetDateTime.parse(iso).toInstant() }.getOrNull()
        ?: runCatching { LocalDateTime.parse(iso).atZone(ZoneId.systemDefault()).toInstant() }
            .getOrNull() ?: return ""
    val mins = Duration.between(instant, Instant.now()).toMinutes()
    return when {
        mins < 1 -> "방금"
        mins < 60 -> "${mins}분 전"
        mins < 60 * 24 -> "${mins / 60}시간 전"
        mins < 60 * 24 * 7 -> "${mins / (60 * 24)}일 전"
        else -> LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
            .let { "${it.monthValue}/${it.dayOfMonth}" }
    }
}

/** 1234567 → "₩1,234,567" */
fun won(amount: Long): String =
    "₩" + NumberFormat.getNumberInstance(Locale.KOREA).format(amount)

/** 카드 메타 줄: "작성자 · yyyy-MM-dd" */
fun metaLine(author: String?, createdAt: String?, unknownAuthor: String): String =
    listOf(author ?: unknownAuthor, createdAt?.take(10) ?: "")
        .filter { it.isNotBlank() }
        .joinToString(" · ")

/** 공지 카드 메타. */
fun noticeMeta(notice: NoticeDto): String = metaLine(notice.authorName, notice.createdAt, "사장님")

/** 인수인계 카드 메타. */
fun handoverMeta(note: HandoverDto): String = metaLine(note.authorName, note.createdAt, "작성자")

/** 대타 요청 카드 제목: "yyyy-MM-dd 18:00 – 23:00" (근무 정보 없으면 "대타요청 #id"). */
fun shiftTitle(req: SwapRequestDto): String {
    val shift = req.shift ?: return "대타요청 #${req.id}"
    return "${shift.workDate ?: ""} ${shiftTimeRange(shift.startTime, shift.endTime)}".trim()
}

/** 대타 상태 한글 라벨. */
fun swapStatusLabel(status: String?): String = when (status) {
    "PENDING" -> if (BuildConfig.APP_ROLE == "EMPLOYEE") "대기 중" else status
    "APPROVED" -> "승인"
    "REJECTED" -> "거절"
    else -> status ?: ""
}

/** 대타 상태 배지 배경. */
fun swapStatusBadge(status: String?): Int = when (status) {
    "APPROVED" -> R.drawable.bg_badge_approved
    "REJECTED" -> R.drawable.bg_badge_rejected
    else -> R.drawable.bg_badge_pending
}

/** 근무 시간(시). 자정 넘는 야간 교대(end≤start)는 +24h 보정. 파싱 실패 시 0. */
fun shiftHours(start: String?, end: String?): Double {
    val mins = timeDifferenceMinutes(start, end) ?: return 0.0
    return (if (mins <= 0) mins + 24 * 60 else mins) / 60.0
}

private fun timeDifferenceMinutes(start: String?, end: String?): Long? = runCatching {
    Duration.between(LocalTime.parse(start), LocalTime.parse(end)).toMinutes()
}.getOrNull()

/** 3.5 → "3.5h", 8.0 → "8h" */
fun hoursLabel(hours: Double): String =
    if (hours % 1.0 == 0.0) "${hours.toInt()}h" else "${Math.round(hours * 10) / 10.0}h"

/** 근태 상태 한글 라벨. */
fun attendanceLabel(status: String?): String = when (status) {
    "PRESENT" -> "출근"
    "LATE" -> "지각"
    "ABSENT" -> "결근"
    "SCHEDULED" -> "예정"
    else -> status ?: ""
}

/** 분 단위 합계를 화면에 표시한다. */
fun durationLabel(minutes: Long): String =
    if (minutes % 60 == 0L) "${minutes / 60}시간" else "${minutes / 60}시간 ${minutes % 60}분"

fun dateTimeLabel(iso: String?): String =
    iso?.replace("T", " ")?.take(16).orEmpty()

internal fun noticeDetailMeta(notice: NoticeDto, employer: Boolean): String {
    val date = notice.createdAt?.let(::dateTimeLabel)
    return if (employer) {
        listOf(notice.authorName ?: "사장님", date.orEmpty()).filter { it.isNotBlank() }.joinToString(" · ")
    } else {
        listOfNotNull(notice.authorName, date).joinToString(" · ")
    }
}

fun roleLabel(user: UserDto): String = when (user.role) {
    "EMPLOYER" -> "사장님"
    "EMPLOYEE" -> "알바"
    else -> user.email ?: ""
}
