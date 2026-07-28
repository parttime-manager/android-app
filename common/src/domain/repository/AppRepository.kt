package com.example.ptmanagercommon.domain.repository

import com.example.ptmanagercommon.domain.model.*

interface AppRepository {
    suspend fun signup(body: SignupRequest): TokenResponse
    suspend fun login(body: LoginRequest): TokenResponse
    suspend fun logout(): Unit
    suspend fun me(): UserDto
    suspend fun getWorkplace(id: Long): WorkplaceDto
    suspend fun getMembers(id: Long, role: String? = null): List<UserDto>
    suspend fun createJoinRequest(body: CreateJoinRequest): JoinRequestDto
    suspend fun updateProfile(body: UpdateProfileRequest): UserDto
    suspend fun getNotificationSetting(): NotificationSettingDto
    suspend fun updateNotificationSetting(body: NotificationSettingUpdate): NotificationSettingDto
    suspend fun getShifts(workplaceId: Long? = null, employeeId: String? = null, from: String? = null, to: String? = null, status: String? = null): List<ShiftDto>
    suspend fun getShift(id: Long): ShiftDto
    suspend fun checkIn(id: Long, body: CheckInRequest): ShiftDto
    suspend fun checkOut(id: Long, body: CheckInRequest): ShiftDto
    suspend fun createSwapRequest(body: CreateSwapRequest): SwapRequestDto
    suspend fun getSwapRequests(workplaceId: Long, view: String, status: String? = null): List<SwapRequestDto>
    suspend fun getSwapRequest(id: Long): SwapRequestDetailDto
    suspend fun applyToSwap(id: Long): SwapApplicationDto
    suspend fun getMySwapApplications(status: String? = null): List<SwapApplicationDto>
    suspend fun getNotices(workplaceId: Long, page: Int = 0, size: Int = 20): PageResponse<NoticeDto>
    suspend fun getNotice(id: Long): NoticeDto
    suspend fun getNoticeUnread(workplaceId: Long): UnreadFlag
    suspend fun markNoticesRead(): Unit
    suspend fun getMyPayroll(yearMonth: String): MyPayrollSummary
    suspend fun getHandovers(workplaceId: Long, category: String? = null): List<HandoverDto>
    suspend fun createHandover(body: CreateHandoverRequest): HandoverDto
    suspend fun deleteHandover(id: Long): Unit
    suspend fun getNotifications(isRead: Boolean? = null, page: Int = 0, size: Int = 20): PageResponse<NotificationDto>
    suspend fun getNotificationUnreadCount(): UnreadCount
    suspend fun markAllNotificationsRead(): Unit
    suspend fun registerDeviceToken(body: RegisterDeviceTokenRequest): Unit
    suspend fun deleteDeviceToken(token: String): Unit
    suspend fun createWorkplace(body: CreateWorkplaceRequest): WorkplaceDto
    suspend fun getJoinRequests(workplaceId: Long, status: String? = null): List<JoinRequestDto>
    suspend fun decideJoinRequest(id: Long, body: DecisionRequest): JoinRequestDto
    suspend fun createShift(body: CreateShiftRequest): ShiftDto
    suspend fun publishShifts(workplaceId: Long, from: String, to: String): PublishResult
    suspend fun updateShift(id: Long, body: UpdateShiftRequest): ShiftDto
    suspend fun deleteShift(id: Long): Unit
    suspend fun getSwapApplications(id: Long): List<SwapApplicationDto>
    suspend fun approveSwap(id: Long, body: ApproveSwapRequest): SwapRequestDto
    suspend fun rejectSwap(id: Long): SwapRequestDto
    suspend fun createNotice(body: CreateNoticeRequest): NoticeDto
    suspend fun deleteNotice(id: Long): Unit
    suspend fun getPayroll(workplaceId: Long, yearMonth: String): PayrollSummary
    suspend fun getWeeklyPayroll(workplaceId: Long, yearMonth: String): WeeklyPayrollSummary
    suspend fun getQrToken(id: Long): QrTokenResponse
    suspend fun updateMemberWage(wid: Long, uid: Long, body: UpdateWageRequest): UserDto
    suspend fun removeMember(wid: Long, uid: Long): Unit
    suspend fun uploadAttachment(file: UploadFile): NoticeAttachmentDto
}
