package com.example.ptmanageremployer

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class SubFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_sub, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val pending = view.findViewById<View>(R.id.panel_pending)
        val done = view.findViewById<View>(R.id.panel_done)
        val tabPending = view.findViewById<View>(R.id.tab_pending)
        val tabDone = view.findViewById<View>(R.id.tab_done)
        fun select(pendingSel: Boolean) {
            tabPending.setBackgroundResource(if (pendingSel) R.drawable.bg_segment_active else 0)
            tabDone.setBackgroundResource(if (pendingSel) 0 else R.drawable.bg_segment_active)
            pending.visibility = if (pendingSel) View.VISIBLE else View.GONE
            done.visibility = if (pendingSel) View.GONE else View.VISIBLE
        }
        tabPending.setOnClickListener { select(true) }
        tabDone.setOnClickListener { select(false) }
        select(true)

        val open = { _: View ->
            startActivity(Intent(requireContext(), SubApprovalActivity::class.java))
        }
        view.findViewById<View>(R.id.req_1).setOnClickListener(open)
        view.findViewById<View>(R.id.req_2).setOnClickListener(open)
    }
}
