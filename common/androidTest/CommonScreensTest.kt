package com.example.ptmanagercommon

import com.example.ptmanagercommon.presentation.view.*

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CommonScreensTest {
    @Test
    fun loginKeepsEachAppsStoreActionAndSignupToggle() {
        ActivityScenario.launch(LoginActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val store = activity.findViewById<TextView>(R.id.btn_store)
                assertEquals(if (BuildConfig.APP_ROLE == "EMPLOYEE") "초대 코드로 매장 참여하기" else "새 매장 만들기", store.text.toString())
                val name = activity.findViewById<View>(R.id.input_name)
                assertEquals(View.GONE, name.visibility)
                activity.findViewById<View>(R.id.btn_signup).performClick()
                assertEquals(View.VISIBLE, name.visibility)
                activity.findViewById<View>(R.id.btn_signup).performClick()
                assertEquals(View.GONE, name.visibility)
            }
        }
    }

    @Test
    fun commonScreenBackButtonClosesTheActivity() {
        ActivityScenario.launch(ProfileEditActivity::class.java).use { scenario ->
            scenario.onActivity { it.findViewById<View>(R.id.btn_back).performClick() }
            InstrumentationRegistry.getInstrumentation().waitForIdleSync()
            assertEquals(Lifecycle.State.DESTROYED, scenario.state)
        }
    }

    @Test
    fun invalidNoticeKeepsEachAppsOriginalNavigation() {
        ActivityScenario.launch(NoticeDetailActivity::class.java).use { scenario ->
            assertEquals(
                if (BuildConfig.APP_ROLE == "EMPLOYER") Lifecycle.State.DESTROYED else Lifecycle.State.RESUMED,
                scenario.state,
            )
        }
    }

    @Test
    fun categoryChipsStayExclusiveAndListsSwitchEmptyState() {
        ActivityScenario.launch(HandoverWriteActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val stock = activity.findViewById<TextView>(R.id.chip_stock)
                val device = activity.findViewById<TextView>(R.id.chip_device)
                val brand = ContextCompat.getColor(activity, R.color.brand)
                val off = ContextCompat.getColor(activity, R.color.text_secondary)
                assertEquals(brand, stock.currentTextColor)
                device.performClick()
                assertEquals(brand, device.currentTextColor)
                assertEquals(off, stock.currentTextColor)
                stock.performClick()
                assertEquals(brand, stock.currentTextColor)
                assertEquals(off, device.currentTextColor)

                val container = LinearLayout(activity)
                val empty = TextView(activity).apply { id = View.generateViewId() }
                container.addView(empty)
                container.renderItems(empty.id, listOf("first", "second")) { value ->
                    container.addView(TextView(activity).apply { text = value })
                }
                assertEquals(View.GONE, empty.visibility)
                assertEquals("first", (container.getChildAt(1) as TextView).text.toString())
                container.removeAllExcept(empty.id)
                container.renderItems(empty.id, emptyList<String>()) { fail("Empty list must not render a row") }
                assertEquals(View.VISIBLE, empty.visibility)
                assertEquals(1, container.childCount)
            }
        }
    }
}
