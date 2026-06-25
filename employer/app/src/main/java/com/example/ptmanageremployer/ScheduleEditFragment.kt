package com.example.ptmanageremployer

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment

class ScheduleEditFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_schedule_edit, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<View>(R.id.btn_add_shift).setOnClickListener {
            Toast.makeText(requireContext(), "시프트 추가", Toast.LENGTH_SHORT).show()
        }
        view.findViewById<View>(R.id.btn_attendance)?.setOnClickListener {
            startActivity(Intent(requireContext(), ShiftAttendanceActivity::class.java))
        }
    }
}
