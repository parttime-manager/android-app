package com.example.ptmanagercommon.presentation.view

import com.example.ptmanagercommon.*
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.ptmanagercommon.data.dateTimeLabel
import com.example.ptmanagercommon.data.toUserMessage
import kotlinx.coroutines.launch

class NotificationActivity : AppCompatActivity() {
    private val model by lazy { AppGraph.notifications(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupScreen(R.layout.activity_notification, R.id.inbox_root)
        val container = findViewById<LinearLayout>(R.id.noti_container)
        val empty = findViewById<TextView>(R.id.tv_noti_empty)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                model.state.collect { state ->
                    container.removeAllExcept(R.id.tv_noti_empty)
                    empty.visibility = if (state.loaded && state.items.isEmpty()) View.VISIBLE else View.GONE
                    state.items.forEach { noti ->
                        container.addItem(R.layout.item_notification) { row ->
                            row.text(R.id.tv_title, notificationTitle(noti.type))
                            row.text(R.id.tv_body, noti.message)
                            row.text(R.id.tv_time, dateTimeLabel(noti.createdAt?.takeUnless { it.isBlank() }))
                        }
                    }
                    state.error?.let { toast(it.toUserMessage()); model.errorShown() }
                }
            }
        }
        model.load()
    }
}
