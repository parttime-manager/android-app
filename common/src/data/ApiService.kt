package com.example.ptmanagercommon.data

import com.example.ptmanagercommon.domain.model.*
import okhttp3.MultipartBody
import retrofit2.http.*

/** Each app exposes its UI actions; the server enforces role and workplace access. */
interface ApiService {
    @POST("api/auth/signup")
    suspend fun signup(@Body body: SignupRequest): TokenResponse

    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequest): TokenResponse

    @POST("api/auth/logout")
    suspend fun logout()

    @GET("api/auth/me")
    suspend fun me(): UserDto

    @GET("api/workplaces/{id}")
    suspend fun getWorkplace(@Path("id") id: Long): WorkplaceDto

    @GET("api/workplaces/{id}/members")
    suspend fun getMembers(
        @Path("id") id: Long,
        @Query("role") role: String? = null,
    ): List<UserDto>

    @POST("api/join-requests")
    suspend fun createJoinRequest(@Body body: CreateJoinRequest): JoinRequestDto

    @PATCH("api/users/me")
    suspend fun updateProfile(@Body body: UpdateProfileRequest): UserDto

    @GET("api/users/me/notification-setting")
    suspend fun getNotificationSetting(): NotificationSettingDto

    @PATCH("api/users/me/notification-setting")
    suspend fun updateNotificationSetting(@Body body: NotificationSettingUpdate): NotificationSettingDto

    @GET("api/shifts")
    suspend fun getShifts(
        @Query("workplaceId") workplaceId: Long? = null,
        @Query("employeeId") employeeId: String? = null,
        @Query("from") from: String? = null,
        @Query("to") to: String? = null,
        @Query("status") status: String? = null,
    ): List<ShiftDto>

    @GET("api/shifts/{id}")
    suspend fun getShift(@Path("id") id: Long): ShiftDto

    @POST("api/shifts/{id}/check-in")
    suspend fun checkIn(@Path("id") id: Long, @Body body: CheckInRequest): ShiftDto

    @POST("api/shifts/{id}/check-out")
    suspend fun checkOut(@Path("id") id: Long, @Body body: CheckInRequest): ShiftDto

    @POST("api/swap-requests")
    suspend fun createSwapRequest(@Body body: CreateSwapRequest): SwapRequestDto

    @GET("api/swap-requests")
    suspend fun getSwapRequests(
        @Query("workplaceId") workplaceId: Long,
        @Query("view") view: String,
        @Query("status") status: String? = null,
    ): List<SwapRequestDto>

    @GET("api/swap-requests/{id}")
    suspend fun getSwapRequest(@Path("id") id: Long): SwapRequestDetailDto

    @POST("api/swap-requests/{id}/applications")
    suspend fun applyToSwap(@Path("id") id: Long): SwapApplicationDto

    @GET("api/swap-applications/me")
    suspend fun getMySwapApplications(@Query("status") status: String? = null): List<SwapApplicationDto>

    @GET("api/notices")
    suspend fun getNotices(
        @Query("workplaceId") workplaceId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
    ): PageResponse<NoticeDto>

    @GET("api/notices/{id}")
    suspend fun getNotice(@Path("id") id: Long): NoticeDto

    @GET("api/notices/unread")
    suspend fun getNoticeUnread(@Query("workplaceId") workplaceId: Long): UnreadFlag

    @POST("api/notices/read")
    suspend fun markNoticesRead()

    @GET("api/payroll/me")
    suspend fun getMyPayroll(@Query("yearMonth") yearMonth: String): MyPayrollSummary

    @GET("api/handovers")
    suspend fun getHandovers(
        @Query("workplaceId") workplaceId: Long,
        @Query("category") category: String? = null,
    ): List<HandoverDto>

    @POST("api/handovers")
    suspend fun createHandover(@Body body: CreateHandoverRequest): HandoverDto

    @DELETE("api/handovers/{id}")
    suspend fun deleteHandover(@Path("id") id: Long)

    @GET("api/notifications")
    suspend fun getNotifications(
        @Query("isRead") isRead: Boolean? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
    ): PageResponse<NotificationDto>

    @GET("api/notifications/unread-count")
    suspend fun getNotificationUnreadCount(): UnreadCount

    @POST("api/notifications/read-all")
    suspend fun markAllNotificationsRead()

    @POST("api/users/me/device-tokens")
    suspend fun registerDeviceToken(@Body body: RegisterDeviceTokenRequest)

    @DELETE("api/users/me/device-tokens/{token}")
    suspend fun deleteDeviceToken(@Path("token") token: String)

    @POST("api/workplaces")
    suspend fun createWorkplace(@Body body: CreateWorkplaceRequest): WorkplaceDto

    @GET("api/join-requests")
    suspend fun getJoinRequests(
        @Query("workplaceId") workplaceId: Long,
        @Query("status") status: String? = null,
    ): List<JoinRequestDto>

    @PATCH("api/join-requests/{id}")
    suspend fun decideJoinRequest(
        @Path("id") id: Long,
        @Body body: DecisionRequest,
    ): JoinRequestDto

    @POST("api/shifts")
    suspend fun createShift(@Body body: CreateShiftRequest): ShiftDto

    @POST("api/shifts/publish")
    suspend fun publishShifts(
        @Query("workplaceId") workplaceId: Long,
        @Query("from") from: String,
        @Query("to") to: String,
    ): PublishResult

    @PATCH("api/shifts/{id}")
    suspend fun updateShift(@Path("id") id: Long, @Body body: UpdateShiftRequest): ShiftDto

    @DELETE("api/shifts/{id}")
    suspend fun deleteShift(@Path("id") id: Long)

    @GET("api/swap-requests/{id}/applications")
    suspend fun getSwapApplications(@Path("id") id: Long): List<SwapApplicationDto>

    @POST("api/swap-requests/{id}/approve")
    suspend fun approveSwap(@Path("id") id: Long, @Body body: ApproveSwapRequest): SwapRequestDto

    @POST("api/swap-requests/{id}/reject")
    suspend fun rejectSwap(@Path("id") id: Long): SwapRequestDto

    @POST("api/notices")
    suspend fun createNotice(@Body body: CreateNoticeRequest): NoticeDto

    @DELETE("api/notices/{id}")
    suspend fun deleteNotice(@Path("id") id: Long)

    @GET("api/payroll")
    suspend fun getPayroll(
        @Query("workplaceId") workplaceId: Long,
        @Query("yearMonth") yearMonth: String,
    ): PayrollSummary

    @GET("api/payroll/weekly")
    suspend fun getWeeklyPayroll(
        @Query("workplaceId") workplaceId: Long,
        @Query("yearMonth") yearMonth: String,
    ): WeeklyPayrollSummary

    @GET("api/workplaces/{id}/qr-token")
    suspend fun getQrToken(@Path("id") id: Long): QrTokenResponse

    @PATCH("api/workplaces/{wid}/members/{uid}/wage")
    suspend fun updateMemberWage(
        @Path("wid") wid: Long,
        @Path("uid") uid: Long,
        @Body body: UpdateWageRequest,
    ): UserDto

    @DELETE("api/workplaces/{wid}/members/{uid}")
    suspend fun removeMember(
        @Path("wid") wid: Long,
        @Path("uid") uid: Long,
    )

    @Multipart
    @POST("api/notices/attachments")
    suspend fun uploadAttachment(@Part file: MultipartBody.Part): NoticeAttachmentDto
}
