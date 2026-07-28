package com.example.ptmanageremployee.presentation.view

internal enum class HomeShiftAction { SCHEDULE, CHECK_IN, CHECK_OUT, DETAIL }

internal fun homeShiftAction(hasShift: Boolean, checkedIn: Boolean, checkedOut: Boolean): HomeShiftAction = when {
    !hasShift -> HomeShiftAction.SCHEDULE
    checkedOut -> HomeShiftAction.DETAIL
    checkedIn -> HomeShiftAction.CHECK_OUT
    else -> HomeShiftAction.CHECK_IN
}
