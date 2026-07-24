package com.example.ptmanageremployer

import com.example.ptmanageremployer.data.metaLine
import org.junit.Assert.assertEquals
import org.junit.Test

/** 공지·인수인계 카드와 상세 화면이 함께 쓰는 메타 줄 형식 고정. */
class FormatTest {

    @Test
    fun metaLine_drops_blank_parts() {
        assertEquals("김사장 · 2026-07-01", metaLine("김사장", "2026-07-01T10:00:00", "작성자"))
        assertEquals("작성자", metaLine(null, null, "작성자"))
        assertEquals("작성자 · 2026-07-01", metaLine(null, "2026-07-01T10:00:00", "작성자"))
    }
}
