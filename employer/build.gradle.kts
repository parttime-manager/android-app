plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services) apply false
}

android { namespace = "com.example.ptmanageremployer" }
extra["ptmCommonDir"] = rootProject.file("common")
apply(from = rootProject.file("common/android.gradle"))
