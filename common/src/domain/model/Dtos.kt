package com.example.ptmanagercommon.domain.model


data class SignupRequest(
    val email: String,
    val password: String,
    val name: String,
    val role: String,
)

data class LoginRequest(
    val email: String,
    val password: String,
)

data class RefreshRequest(
    val refreshToken: String,
)

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String? = "Bearer",
    val expiresIn: Long? = null,
    val user: UserDto,
)

data class UserDto(
    val id: Long,
    val email: String? = null,
    val name: String? = null,
    val role: String? = null,
    val workplaceId: Long? = null,
    val hourlyWage: Int? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

data class WorkplaceDto(
    val id: Long,
    val name: String? = null,
    val address: String? = null,
    val inviteCode: String? = null,
    val createdAt: String? = null,
)

data class CreateJoinRequest(
    val inviteCode: String,
)

data class JoinRequestDto(
    val id: Long,
    val workplaceId: Long? = null,
    val userId: Long? = null,
    val userName: String? = null,
    val status: String? = null,
    val createdAt: String? = null,
)

data class ShiftDto(
    val id: Long,
    val workplaceId: Long? = null,
    val employeeId: Long? = null,
    val employeeName: String? = null,
    val workDate: String? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val checkedInAt: String? = null,
    val checkedOutAt: String? = null,
    val attendanceStatus: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    // 상세 조회에서만 내려온다 (목록에서는 null).
    val workplaceName: String? = null,
    val coworkers: List<String>? = null,
    val estimatedPay: Int? = null,
    val published: Boolean = true,
)

data class CheckInRequest(
    val qrToken: String,
)

data class SwapRequestDto(
    val id: Long,
    val workplaceId: Long? = null,
    val shiftId: Long? = null,
    val requesterId: Long? = null,
    val substituteId: Long? = null,
    val reason: String? = null,
    val status: String? = null,
    val createdAt: String? = null,
    val shift: ShiftDto? = null,
)

data class CreateSwapRequest(
    val shiftId: Long,
    val reason: String,
)

data class SwapApplicationDto(
    val id: Long,
    val swapRequestId: Long? = null,
    val applicantId: Long? = null,
    val applicantName: String? = null,
    val status: String? = null,
    val createdAt: String? = null,
)

data class SwapRequestDetailDto(
    val id: Long,
    val workplaceId: Long? = null,
    val shiftId: Long? = null,
    val requesterId: Long? = null,
    val substituteId: Long? = null,
    val reason: String? = null,
    val status: String? = null,
    val createdAt: String? = null,
    val shift: ShiftDto? = null,
    val applications: List<SwapApplicationDto>? = null,
)

data class NoticeAttachmentDto(
    val id: Long,
    val noticeId: Long? = null,
    val fileUrl: String? = null,
    val createdAt: String? = null,
)

data class NoticeDto(
    val id: Long,
    val workplaceId: Long? = null,
    val authorId: Long? = null,
    val authorName: String? = null,
    val title: String? = null,
    val body: String? = null,
    val attachments: List<NoticeAttachmentDto>? = null,
    val createdAt: String? = null,
)

data class UnreadFlag(val hasUnread: Boolean = false)

data class HandoverDto(
    val id: Long,
    val workplaceId: Long? = null,
    val authorId: Long? = null,
    val authorName: String? = null,
    val category: String? = null,
    val title: String? = null,
    val content: String? = null,
    val createdAt: String? = null,
)

data class CreateHandoverRequest(
    val workplaceId: Long,
    val category: String,
    val title: String,
    val content: String,
)

data class NotificationDto(
    val id: Long,
    val userId: Long? = null,
    val type: String? = null,
    val message: String? = null,
    val targetType: String? = null,
    val targetId: Long? = null,
    val isRead: Boolean = false,
    val createdAt: String? = null,
)

data class UnreadCount(val count: Int = 0)

data class NotificationSettingDto(
    val userId: Long? = null,
    val swapEnabled: Boolean = true,
    val noticeEnabled: Boolean = true,
    val attendanceEnabled: Boolean = true,
    val joinRequestEnabled: Boolean = true,
)

data class NotificationSettingUpdate(
    val swapEnabled: Boolean? = null,
    val noticeEnabled: Boolean? = null,
    val attendanceEnabled: Boolean? = null,
    val joinRequestEnabled: Boolean? = null,
)

data class UpdateProfileRequest(
    val name: String,
)

data class RegisterDeviceTokenRequest(
    val token: String,
    val platform: String = "ANDROID",
)

data class MyPayrollSummary(
    val yearMonth: String? = null,
    val hourlyWage: Int = 0,
    val workedMinutes: Long = 0,
    val amount: Long = 0,
    val weeks: List<WeeklyCost> = emptyList(),
)

data class WeeklyCost(
    val week: Int = 0,
    val amount: Long = 0,
)

data class PageResponse<T>(
    val content: List<T> = emptyList(),
    val page: Int = 0,
    val size: Int = 0,
    val totalElements: Long = 0,
    val totalPages: Int = 0,
)

data class CreateWorkplaceRequest(
    val name: String,
    val address: String? = null,
)

data class DecisionRequest(
    /** APPROVE | REJECT */
    val decision: String,
)

data class PublishResult(val published: Int = 0)

data class CreateShiftRequest(
    val workplaceId: Long,
    val employeeId: Long,
    val workDate: String,
    val startTime: String,
    val endTime: String,
)

data class UpdateShiftRequest(
    val employeeId: Long? = null,
    val workDate: String? = null,
    val startTime: String? = null,
    val endTime: String? = null,
)

data class ApproveSwapRequest(
    val applicantId: Long,
)

data class CreateNoticeRequest(
    val workplaceId: Long,
    val title: String,
    val body: String,
    val attachmentUrls: List<String>? = null,
)

data class QrTokenResponse(val qrToken: String)

data class UpdateWageRequest(val hourlyWage: Int)

data class PayrollItem(
    val employeeId: Long? = null,
    val employeeName: String? = null,
    val hourlyWage: Int? = null,
    val workedMinutes: Int? = null,
    val amount: Long? = null,
)

data class PayrollSummary(
    val workplaceId: Long? = null,
    val yearMonth: String? = null,
    val totalAmount: Long = 0,
    val items: List<PayrollItem> = emptyList(),
)

data class WeeklyPayrollSummary(
    val workplaceId: Long? = null,
    val yearMonth: String? = null,
    val weeks: List<WeeklyCost> = emptyList(),
)
