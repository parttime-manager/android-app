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
        view.findViewById<View>(R.id.card_sub_request).setOnClickListener {
            activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)
                ?.selectedItemId = R.id.nav_sub
        }
        view.findViewById<View>(R.id.card_labor_cost).setOnClickListener {
            startActivity(Intent(requireContext(), LaborCostActivity::class.java))
        }
        view.findViewById<View>(R.id.btn_bell).setOnClickListener {
            startActivity(Intent(requireContext(), NotificationActivity::class.java))
        }
    }
}
