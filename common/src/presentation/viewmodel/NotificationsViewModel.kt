package com.example.ptmanagercommon.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ptmanagercommon.domain.model.NotificationDto
import com.example.ptmanagercommon.domain.repository.AppRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class NotificationsState(
    val items: List<NotificationDto> = emptyList(),
    val loading: Boolean = false,
    val loaded: Boolean = false,
    val error: Throwable? = null,
)

class NotificationsViewModel(private val repository: AppRepository) : ViewModel() {
    private val _state = MutableStateFlow(NotificationsState())
    val state = _state.asStateFlow()

    fun load() {
        if (_state.value.loaded || _state.value.loading) return
        viewModelScope.launch { refresh() }
    }

    suspend fun refresh() {
        _state.value = _state.value.copy(loading = true, error = null)
        try {
            val page = repository.getNotifications(page = 0, size = 50)
            _state.value = NotificationsState(items = page.content, loaded = true)
            if (page.content.isNotEmpty()) {
                try { repository.markAllNotificationsRead() }
                catch (cancelled: CancellationException) { throw cancelled }
                catch (_: Exception) { /* Inbox remains usable when acknowledgement fails. */ }
            }
        } catch (cancelled: CancellationException) {
            _state.value = _state.value.copy(loading = false)
            throw cancelled
        } catch (error: Exception) {
            _state.value = _state.value.copy(loading = false, error = error)
        }
    }

    fun errorShown() { _state.value = _state.value.copy(error = null) }
}
