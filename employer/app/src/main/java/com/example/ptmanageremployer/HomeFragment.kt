package com.example.ptmanageremployer

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fun goTab(itemId: Int) {
            activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)?.selectedItemId = itemId
        }

        view.findViewById<View>(R.id.btn_bell).setOnClickListener {
            startActivity(Intent(requireContext(), NotificationActivity::class.java))
        }
        view.findViewById<View>(R.id.card_approval).setOnClickListener { goTab(R.id.nav_approval) }
        view.findViewById<View>(R.id.card_labor).setOnClickListener { goTab(R.id.nav_stats) }
        view.findViewById<View>(R.id.btn_make_schedule).setOnClickListener { goTab(R.id.nav_schedule) }
        view.findViewById<View>(R.id.btn_write_notice).setOnClickListener {
            startActivity(Intent(requireContext(), NoticeWriteActivity::class.java))
        }
    }
}
