package com.example.ptmanageremployer.presentation.view

import com.example.ptmanageremployer.*
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.ptmanageremployer.data.Network
import com.example.ptmanageremployer.data.TokenStore
import com.example.ptmanageremployer.data.toUserMessage
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * 매장 출근용 QR을 표시한다. 서버가 회전형 QR(기본 60초 만료 + 최신 토큰만 유효)이므로,
 * 화면이 떠 있는 동안 주기적으로 새 토큰을 폴링해 QR을 갱신한다.
 *
 * 공기계(전용 단말)에 띄워둘 때를 위해 '고정 모드'를 지원한다:
 * Android 화면 고정(screen pinning) + 뒤로가기 차단 + 시스템 바 숨김으로,
 * 손님·직원이 화면을 벗어나지 못하게 한다. 해제는 잠금 버튼을 길게 눌러야 한다.
 */
class QrDisplayActivity : AppCompatActivity() {

    private val screenModel by lazy { com.example.ptmanageremployer.AppGraph.model(this) }

    /** 서버 만료(기본 60초)보다 짧게 잡아 만료 전에 갱신한다. */
    private val refreshSeconds = 45

    private var locked = false
    private lateinit var backGuard: OnBackPressedCallback
    private lateinit var lockButton: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 직원이 스캔하는 동안 화면이 꺼지거나 잠기지 않도록 유지한다. (없으면 QR을 못 스캔함)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContentView(R.layout.activity_qr_display)
        val brand = ContextCompat.getColor(this, R.color.brand)
        val qrPx = (resources.displayMetrics.widthPixels * 0.72f).toInt().coerceIn(dp(220), dp(360))
        val image = findViewById<ImageView>(R.id.qr_image).apply {
            layoutParams = layoutParams.apply { width = qrPx; height = qrPx }
        }
        val caption = findViewById<TextView>(R.id.qr_caption)
        lockButton = findViewById<TextView>(R.id.btn_lock).apply {
            applyLockButtonStyle(false, brand)
            setOnClickListener { if (!locked) enterLock() }
            setOnLongClickListener {
                if (locked) { exitLock(); true } else false
            }
        }

        // 고정 모드일 때만 뒤로가기를 삼켜 화면을 벗어나지 못하게 한다.
        backGuard = object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                toast("고정 모드입니다. 잠금 버튼을 길게 눌러 해제하세요.")
            }
        }
        onBackPressedDispatcher.addCallback(this, backGuard)

        val workplaceId = TokenStore.workplaceId
        if (workplaceId <= 0) {
            toast("소속 매장이 없습니다.")
            finish()
            return
        }

        // 화면이 보이는 동안(STARTED)만 폴링하고, 백그라운드로 가면 자동 중단된다.
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                while (isActive) {
                    try {
                        val token = screenModel.getQrToken(workplaceId).qrToken
                        image.setImageBitmap(
                            BarcodeEncoder().encodeBitmap(token, BarcodeFormat.QR_CODE, qrPx, qrPx)
                        )
                        var remain = refreshSeconds
                        while (remain > 0 && isActive) {
                            caption.text = "직원에게 이 QR을 스캔하도록 안내하세요\n${remain}초 후 자동 갱신"
                            delay(1000)
                            remain--
                        }
                    } catch (e: Exception) {
                        caption.text = e.toUserMessage()
                        delay(5000) // 오류 시엔 더 빨리 재시도
                    }
                }
            }
        }
    }

    private fun enterLock() {
        locked = true
        backGuard.isEnabled = true
        try { startLockTask() } catch (_: Exception) { /* 정책상 불가한 기기 — 뒤로가기 차단만 적용 */ }
        setSystemBarsVisible(false)
        lockButton.text = "🔓  길게 눌러 잠금 해제"
        lockButton.applyLockButtonStyle(true, ContextCompat.getColor(this, R.color.brand))
        toast("고정 모드 시작 — 버튼을 길게 누르면 해제됩니다.", long = true)
    }

    private fun exitLock() {
        locked = false
        backGuard.isEnabled = false
        try { stopLockTask() } catch (_: Exception) {}
        setSystemBarsVisible(true)
        lockButton.text = "🔒  공기계 고정 모드"
        lockButton.applyLockButtonStyle(false, ContextCompat.getColor(this, R.color.brand))
    }

    private fun setSystemBarsVisible(visible: Boolean) {
        WindowCompat.setDecorFitsSystemWindows(window, visible)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        if (visible) {
            controller.show(WindowInsetsCompat.Type.systemBars())
        } else {
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun TextView.applyLockButtonStyle(active: Boolean, brand: Int) {
        background = GradientDrawable().apply {
            cornerRadius = dp(999).toFloat()
            if (active) setColor(brand) else {
                setColor(Color.WHITE)
                setStroke(dp(1), brand)
            }
        }
        setTextColor(if (active) Color.WHITE else brand)
    }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()
}
