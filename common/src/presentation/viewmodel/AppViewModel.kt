package com.example.ptmanagercommon.presentation.viewmodel

import com.example.ptmanagercommon.domain.repository.AppRepository
import com.example.ptmanagercommon.domain.model.*
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class RequestState(val activeRequests: Int = 0, val error: String? = null)

class AppViewModel(private val repository: AppRepository) : ViewModel(), AppRepository {
    private val _requests = MutableStateFlow(RequestState())
    val requests = _requests.asStateFlow()

    private suspend fun <T> request(block: suspend () -> T): T {
        _requests.update { it.copy(activeRequests = it.activeRequests + 1, error = null) }
        try { return block() }
        catch (cancelled: CancellationException) { throw cancelled }
        catch (error: Exception) { _requests.update { it.copy(error = error.message) }; throw error }
        finally { _requests.update { it.copy(activeRequests = it.activeRequests - 1) } }
    }
    override suspend fun signup(body: SignupRequest): TokenResponse =
        request { repository.signup(body) }
    override suspend fun login(body: LoginRequest): TokenResponse =
        request { repository.login(body) }
    override suspend fun logout(): Unit =
        request { repository.logout() }
    override suspend fun me(): UserDto =
        request { repository.me() }
    override suspend fun getWorkplace(id: Long): WorkplaceDto =
        request { repository.getWorkplace(id) }
    override suspend fun getMembers(id: Long, role: String?): List<UserDto> =
        request { repository.getMembers(id, role) }
    override suspend fun createJoinRequest(body: CreateJoinRequest): JoinRequestDto =
        request { repository.createJoinRequest(body) }
    override suspend fun updateProfile(body: UpdateProfileRequest): UserDto =
        request { repository.updateProfile(body) }
    override suspend fun getNotificationSetting(): NotificationSettingDto =
        request { repository.getNotificationSetting() }
    override suspend fun updateNotificationSetting(body: NotificationSettingUpdate): NotificationSettingDto =
        request { repository.updateNotificationSetting(body) }
    override suspend fun getShifts(workplaceId: Long?, employeeId: String?, from: String?, to: String?, status: String?): List<ShiftDto> =
        request { repository.getShifts(workplaceId, employeeId, from, to, status) }
    override suspend fun getShift(id: Long): ShiftDto =
        request { repository.getShift(id) }
    override suspend fun checkIn(id: Long, body: CheckInRequest): ShiftDto =
        request { repository.checkIn(id, body) }
    override suspend fun checkOut(id: Long, body: CheckInRequest): ShiftDto =
        request { repository.checkOut(id, body) }
    override suspend fun createSwapRequest(body: CreateSwapRequest): SwapRequestDto =
        request { repository.createSwapRequest(body) }
    override suspend fun getSwapRequests(workplaceId: Long, view: String, status: String?): List<SwapRequestDto> =
        request { repository.getSwapRequests(workplaceId, view, status) }
    override suspend fun getSwapRequest(id: Long): SwapRequestDetailDto =
        request { repository.getSwapRequest(id) }
    override suspend fun applyToSwap(id: Long): SwapApplicationDto =
        request { repository.applyToSwap(id) }
    override suspend fun getMySwapApplications(status: String?): List<SwapApplicationDto> =
        request { repository.getMySwapApplications(status) }
    override suspend fun getNotices(workplaceId: Long, page: Int, size: Int): PageResponse<NoticeDto> =
        request { repository.getNotices(workplaceId, page, size) }
    override suspend fun getNotice(id: Long): NoticeDto =
        request { repository.getNotice(id) }
    override suspend fun getNoticeUnread(workplaceId: Long): UnreadFlag =
        request { repository.getNoticeUnread(workplaceId) }
    override suspend fun markNoticesRead(): Unit =
        request { repository.markNoticesRead() }
    override suspend fun getMyPayroll(yearMonth: String): MyPayrollSummary =
        request { repository.getMyPayroll(yearMonth) }
    override suspend fun getHandovers(workplaceId: Long, category: String?): List<HandoverDto> =
        request { repository.getHandovers(workplaceId, category) }
    override suspend fun createHandover(body: CreateHandoverRequest): HandoverDto =
        request { repository.createHandover(body) }
    override suspend fun deleteHandover(id: Long): Unit =
        request { repository.deleteHandover(id) }
    override suspend fun getNotifications(isRead: Boolean?, page: Int, size: Int): PageResponse<NotificationDto> =
        request { repository.getNotifications(isRead, page, size) }
    override suspend fun getNotificationUnreadCount(): UnreadCount =
        request { repository.getNotificationUnreadCount() }
    override suspend fun markAllNotificationsRead(): Unit =
        request { repository.markAllNotificationsRead() }
    override suspend fun registerDeviceToken(body: RegisterDeviceTokenRequest): Unit =
        request { repository.registerDeviceToken(body) }
    override suspend fun deleteDeviceToken(token: String): Unit =
        request { repository.deleteDeviceToken(token) }
    override suspend fun createWorkplace(body: CreateWorkplaceRequest): WorkplaceDto =
        request { repository.createWorkplace(body) }
    override suspend fun getJoinRequests(workplaceId: Long, status: String?): List<JoinRequestDto> =
        request { repository.getJoinRequests(workplaceId, status) }
    override suspend fun decideJoinRequest(id: Long, body: DecisionRequest): JoinRequestDto =
        request { repository.decideJoinRequest(id, body) }
    override suspend fun createShift(body: CreateShiftRequest): ShiftDto =
        request { repository.createShift(body) }
    override suspend fun publishShifts(workplaceId: Long, from: String, to: String): PublishResult =
        request { repository.publishShifts(workplaceId, from, to) }
    override suspend fun updateShift(id: Long, body: UpdateShiftRequest): ShiftDto =
        request { repository.updateShift(id, body) }
    override suspend fun deleteShift(id: Long): Unit =
        request { repository.deleteShift(id) }
    override suspend fun getSwapApplications(id: Long): List<SwapApplicationDto> =
        request { repository.getSwapApplications(id) }
    override suspend fun approveSwap(id: Long, body: ApproveSwapRequest): SwapRequestDto =
        request { repository.approveSwap(id, body) }
    override suspend fun rejectSwap(id: Long): SwapRequestDto =
        request { repository.rejectSwap(id) }
    override suspend fun createNotice(body: CreateNoticeRequest): NoticeDto =
        request { repository.createNotice(body) }
    override suspend fun deleteNotice(id: Long): Unit =
        request { repository.deleteNotice(id) }
    override suspend fun getPayroll(workplaceId: Long, yearMonth: String): PayrollSummary =
        request { repository.getPayroll(workplaceId, yearMonth) }
    override suspend fun getWeeklyPayroll(workplaceId: Long, yearMonth: String): WeeklyPayrollSummary =
        request { repository.getWeeklyPayroll(workplaceId, yearMonth) }
    override suspend fun getQrToken(id: Long): QrTokenResponse =
        request { repository.getQrToken(id) }
    override suspend fun updateMemberWage(wid: Long, uid: Long, body: UpdateWageRequest): UserDto =
        request { repository.updateMemberWage(wid, uid, body) }
    override suspend fun removeMember(wid: Long, uid: Long): Unit =
        request { repository.removeMember(wid, uid) }
    override suspend fun uploadAttachment(file: UploadFile): NoticeAttachmentDto =
        request { repository.uploadAttachment(file) }
}
