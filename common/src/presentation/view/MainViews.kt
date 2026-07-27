package com.example.ptmanagercommon.presentation.view

import com.example.ptmanagercommon.*
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.ptmanagercommon.data.Push
import com.google.android.material.bottomnavigation.BottomNavigationView

internal fun AppCompatActivity.setupMainNavigation(): BottomNavigationView {
    enableEdgeToEdge()
    setContentView(R.layout.activity_main)
    val host = findViewById<View>(R.id.nav_host)
    val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
    setupFloatingNavigation(
        findViewById(R.id.main), host, bottomNav,
        resources.getDimensionPixelSize(R.dimen.bottom_nav_margin_bottom),
    )
    return bottomNav
}

internal fun AppCompatActivity.show(fragment: Fragment) {
    supportFragmentManager.beginTransaction().replace(R.id.nav_host, fragment).commit()
}

internal fun AppCompatActivity.registerNotifications() {
    val permission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
        ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
    ) {
        permission.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
    Push.registerCurrentToken()
}
