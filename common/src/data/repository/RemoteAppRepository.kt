package com.example.ptmanagercommon.data.repository

import com.example.ptmanagercommon.domain.repository.AppRepository
import com.example.ptmanagercommon.domain.model.*
import com.example.ptmanagercommon.data.ApiService
import okhttp3.MultipartBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class RemoteAppRepository(private val api: () -> ApiService) : AppRepository {
    override suspend fun signup(body: SignupRequest): TokenResponse =
        api().signup(body)
    override suspend fun login(body: LoginRequest): TokenResponse =
        api().login(body)
    override suspend fun logout(): Unit =
        api().logout()
    override suspend fun me(): UserDto =
        api().me()
    override suspend fun getWorkplace(id: Long): WorkplaceDto =
        api().getWorkplace(id)
    override suspend fun getMembers(id: Long, role: String?): List<UserDto> =
        api().getMembers(id, role)
    override suspend fun createJoinRequest(body: CreateJoinRequest): JoinRequestDto =
        api().createJoinRequest(body)
    override suspend fun updateProfile(body: UpdateProfileRequest): UserDto =
        api().updateProfile(body)
    override suspend fun getNotificationSetting(): NotificationSettingDto =
        api().getNotificationSetting()
    override suspend fun updateNotificationSetting(body: NotificationSettingUpdate): NotificationSettingDto =
        api().updateNotificationSetting(body)
    override suspend fun getShifts(workplaceId: Long?, employeeId: String?, from: String?, to: String?, status: String?): List<ShiftDto> =
        api().getShifts(workplaceId, employeeId, from, to, status)
    override suspend fun getShift(id: Long): ShiftDto =
        api().getShift(id)
    override suspend fun checkIn(id: Long, body: CheckInRequest): ShiftDto =
        api().checkIn(id, body)
    override suspend fun checkOut(id: Long, body: CheckInRequest): ShiftDto =
        api().checkOut(id, body)
    override suspend fun createSwapRequest(body: CreateSwapRequest): SwapRequestDto =
        api().createSwapRequest(body)
    override suspend fun getSwapRequests(workplaceId: Long, view: String, status: String?): List<SwapRequestDto> =
        api().getSwapRequests(workplaceId, view, status)
    override suspend fun getSwapRequest(id: Long): SwapRequestDetailDto =
        api().getSwapRequest(id)
    override suspend fun applyToSwap(id: Long): SwapApplicationDto =
        api().applyToSwap(id)
    override suspend fun getMySwapApplications(status: String?): List<SwapApplicationDto> =
        api().getMySwapApplications(status)
    override suspend fun getNotices(workplaceId: Long, page: Int, size: Int): PageResponse<NoticeDto> =
        api().getNotices(workplaceId, page, size)
    override suspend fun getNotice(id: Long): NoticeDto =
        api().getNotice(id)
    override suspend fun getNoticeUnread(workplaceId: Long): UnreadFlag =
        api().getNoticeUnread(workplaceId)
    override suspend fun markNoticesRead(): Unit =
        api().markNoticesRead()
    override suspend fun getMyPayroll(yearMonth: String): MyPayrollSummary =
        api().getMyPayroll(yearMonth)
    override suspend fun getHandovers(workplaceId: Long, category: String?): List<HandoverDto> =
        api().getHandovers(workplaceId, category)
    override suspend fun createHandover(body: CreateHandoverRequest): HandoverDto =
        api().createHandover(body)
    override suspend fun deleteHandover(id: Long): Unit =
        api().deleteHandover(id)
    override suspend fun getNotifications(isRead: Boolean?, page: Int, size: Int): PageResponse<NotificationDto> =
        api().getNotifications(isRead, page, size)
    override suspend fun getNotificationUnreadCount(): UnreadCount =
        api().getNotificationUnreadCount()
    override suspend fun markAllNotificationsRead(): Unit =
        api().markAllNotificationsRead()
    override suspend fun registerDeviceToken(body: RegisterDeviceTokenRequest): Unit =
        api().registerDeviceToken(body)
    override suspend fun deleteDeviceToken(token: String): Unit =
        api().deleteDeviceToken(token)
    override suspend fun createWorkplace(body: CreateWorkplaceRequest): WorkplaceDto =
        api().createWorkplace(body)
    override suspend fun getJoinRequests(workplaceId: Long, status: String?): List<JoinRequestDto> =
        api().getJoinRequests(workplaceId, status)
    override suspend fun decideJoinRequest(id: Long, body: DecisionRequest): JoinRequestDto =
        api().decideJoinRequest(id, body)
    override suspend fun createShift(body: CreateShiftRequest): ShiftDto =
        api().createShift(body)
    override suspend fun publishShifts(workplaceId: Long, from: String, to: String): PublishResult =
        api().publishShifts(workplaceId, from, to)
    override suspend fun updateShift(id: Long, body: UpdateShiftRequest): ShiftDto =
        api().updateShift(id, body)
    override suspend fun deleteShift(id: Long): Unit =
        api().deleteShift(id)
    override suspend fun getSwapApplications(id: Long): List<SwapApplicationDto> =
        api().getSwapApplications(id)
    override suspend fun approveSwap(id: Long, body: ApproveSwapRequest): SwapRequestDto =
        api().approveSwap(id, body)
    override suspend fun rejectSwap(id: Long): SwapRequestDto =
        api().rejectSwap(id)
    override suspend fun createNotice(body: CreateNoticeRequest): NoticeDto =
        api().createNotice(body)
    override suspend fun deleteNotice(id: Long): Unit =
        api().deleteNotice(id)
    override suspend fun getPayroll(workplaceId: Long, yearMonth: String): PayrollSummary =
        api().getPayroll(workplaceId, yearMonth)
    override suspend fun getWeeklyPayroll(workplaceId: Long, yearMonth: String): WeeklyPayrollSummary =
        api().getWeeklyPayroll(workplaceId, yearMonth)
    override suspend fun getQrToken(id: Long): QrTokenResponse =
        api().getQrToken(id)
    override suspend fun updateMemberWage(wid: Long, uid: Long, body: UpdateWageRequest): UserDto =
        api().updateMemberWage(wid, uid, body)
    override suspend fun removeMember(wid: Long, uid: Long): Unit =
        api().removeMember(wid, uid)
    override suspend fun uploadAttachment(file: UploadFile): NoticeAttachmentDto =
        api().uploadAttachment(file.toPart())
}

private fun UploadFile.toPart() = MultipartBody.Part.createFormData(fieldName, name, bytes.toRequestBody(contentType?.toMediaTypeOrNull()))
