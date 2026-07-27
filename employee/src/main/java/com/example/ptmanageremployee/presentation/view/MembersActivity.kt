package com.example.ptmanageremployee.presentation.view

import com.example.ptmanageremployee.*
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.ptmanageremployee.data.TokenStore

class MembersActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupScreen(R.layout.activity_members, R.id.members_root)

        loadMembers()
    }

    private fun loadMembers() {
        val workplaceId = TokenStore.workplaceId
        val container = findViewById<LinearLayout>(R.id.members_container)
        val countLabel = findViewById<TextView>(R.id.tv_members_count)
        if (workplaceId <= 0) {
            countLabel.text = "소속된 매장이 없습니다."
            return
        }
        launchApi {
            val members = screenModel.getMembers(workplaceId)
            countLabel.text = "멤버 ${members.size}명"
            members.forEach { member ->
                container.addItem(R.layout.item_member) { row ->
                    row.bindMember(member)
                    val tag = row.findViewById<TextView>(R.id.tv_tag)
                    tag.visibility = if (member.id == TokenStore.userId) View.VISIBLE else View.GONE
                }
            }
        }
    }
}
