plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services) apply false
}

android { namespace = "com.example.ptmanageremployee" }
extra["ptmCommonDir"] = rootProject.file("common")
apply(from = rootProject.file("common/android.gradle"))
