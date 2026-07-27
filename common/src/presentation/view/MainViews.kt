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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.example.ptmanagercommon.data.Push
import com.google.android.material.bottomnavigation.BottomNavigationView

internal fun AppCompatActivity.setupMainNavigation(): BottomNavigationView {
    enableEdgeToEdge()
    setContentView(R.layout.activity_main)
    val host = findViewById<View>(R.id.nav_host)
    val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { _, insets ->
        val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        host.updatePadding(top = bars.top)
        bottomNav.updatePadding(bottom = bars.bottom)
        insets
    }
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
