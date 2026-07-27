package com.example.ptmanagercommon

import com.example.ptmanagercommon.presentation.view.*

import com.example.ptmanagercommon.domain.model.NotificationDto
import com.example.ptmanagercommon.domain.model.PageResponse
import com.example.ptmanagercommon.domain.repository.AppRepository
import com.example.ptmanagercommon.presentation.viewmodel.NotificationsViewModel
import java.lang.reflect.Proxy
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class NotificationsViewModelTest {
    private fun repository(call: (String) -> Any?): AppRepository = Proxy.newProxyInstance(
        AppRepository::class.java.classLoader, arrayOf(AppRepository::class.java),
    ) { _, method, _ -> call(method.name) } as AppRepository

    @Test fun `successful load retains items even when read acknowledgement fails`() = runBlocking {
        var acknowledgements = 0
        val items = listOf(NotificationDto(id = 7, message = "근무 변경"))
        val model = NotificationsViewModel(repository { name ->
            when (name) {
                "getNotifications" -> PageResponse(content = items)
                "markAllNotificationsRead" -> { acknowledgements++; throw IllegalStateException("offline") }
                else -> error("Unexpected operation: $name")
            }
        })
        model.refresh()
        assertEquals(items, model.state.value.items)
        assertTrue(model.state.value.loaded)
        assertFalse(model.state.value.loading)
        assertNull(model.state.value.error)
        assertEquals(1, acknowledgements)
    }

    @Test fun `failed fetch leaves an error and does not mark notifications read`() = runBlocking {
        var acknowledged = false
        val model = NotificationsViewModel(repository { name ->
            if (name == "markAllNotificationsRead") acknowledged = true
            throw IllegalStateException("offline")
        })
        model.refresh()
        assertFalse(model.state.value.loading)
        assertFalse(model.state.value.loaded)
        assertNotNull(model.state.value.error)
        assertFalse(acknowledged)
    }

    @Test fun `cancellation is propagated without becoming a user error`() = runBlocking {
        val model = NotificationsViewModel(repository { throw CancellationException("closed") })
        try { model.refresh(); fail("Cancellation must propagate") }
        catch (_: CancellationException) { }
        assertFalse(model.state.value.loading)
        assertNull(model.state.value.error)
    }
}
