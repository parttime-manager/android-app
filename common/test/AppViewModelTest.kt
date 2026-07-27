package com.example.ptmanagercommon

import com.example.ptmanagercommon.domain.model.ShiftDto
import com.example.ptmanagercommon.domain.repository.AppRepository
import com.example.ptmanagercommon.presentation.viewmodel.AppViewModel
import java.lang.reflect.Proxy
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class AppViewModelTest {
    private fun repository(call: (String, Array<out Any?>) -> Any?): AppRepository =
        Proxy.newProxyInstance(
            AppRepository::class.java.classLoader, arrayOf(AppRepository::class.java),
        ) { _, method, args -> call(method.name, args.orEmpty()) } as AppRepository

    @Test fun `requests preserve default arguments and return repository results`() = runBlocking {
        val shifts = listOf(ShiftDto(id = 7))
        val model = AppViewModel(repository { name, args ->
            assertEquals("getShifts", name)
            assertEquals(listOf(null, "me", "2026-10-28", null, null), args.take(5))
            shifts
        })
        assertSame(shifts, model.getShifts(employeeId = "me", from = "2026-10-28"))
    }

    @Test fun `request failures and cancellation reach the caller unchanged`() = runBlocking {
        for (failure in listOf(IllegalStateException("offline"), CancellationException("closed"))) {
            val model = AppViewModel(repository { _, _ -> throw failure })
            try {
                model.logout()
                fail("The caller must receive the failure")
            } catch (actual: Exception) {
                assertSame(failure, actual)
            }
        }
    }
}
