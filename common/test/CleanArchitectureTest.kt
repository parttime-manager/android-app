package com.example.ptmanagercommon

import com.example.ptmanagercommon.presentation.view.*

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class CleanArchitectureTest {
    @Test fun `domain and viewmodels do not reference transport or Android views`() {
        val root = File("../common/src")
        assertTrue(root.resolve("domain/repository").isDirectory)
        val sourcePackage = "com.example." + "ptmanagercommon"
        val violations = root.walkTopDown().filter { it.extension == "kt" }.flatMap { file ->
            val path = file.relativeTo(root).invariantSeparatorsPath
            val core = path.startsWith("domain/") || path.startsWith("presentation/viewmodel/")
            file.readLines().filter { line ->
                core && (line.startsWith("import $sourcePackage.data.") ||
                    listOf("android.content.", "android.view.", "android.widget.", "retrofit2.", "okhttp3.").any { line.startsWith("import $it") }) ||
                    !path.startsWith("data/") && path != "AppGraph.kt" && "Network.api" in line
            }.map { "$path: $it" }
        }.toList()
        assertTrue(violations.joinToString("\n"), violations.isEmpty())
    }
}
