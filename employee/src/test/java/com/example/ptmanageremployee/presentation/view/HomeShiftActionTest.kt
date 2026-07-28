package com.example.ptmanageremployee.presentation.view

import org.junit.Assert.assertEquals
import org.junit.Test

class HomeShiftActionTest {
    @Test fun homeActionFollowsAttendanceState() {
        assertEquals(HomeShiftAction.SCHEDULE, homeShiftAction(false, false, false))
        assertEquals(HomeShiftAction.CHECK_IN, homeShiftAction(true, false, false))
        assertEquals(HomeShiftAction.CHECK_OUT, homeShiftAction(true, true, false))
        assertEquals(HomeShiftAction.DETAIL, homeShiftAction(true, true, true))
    }
}
