package com.geekorum.build

import com.hierynomus.gradle.license.LicenseBasePlugin
import com.hierynomus.gradle.license.tasks.LicenseCheck
import com.hierynomus.gradle.license.tasks.LicenseFormat
import nl.javadude.gradle.plugins.license.LicenseExtension
import nl.javadude.gradle.plugins.license.LicensePlugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.named

internal fun Project.configureSourceLicenseChecker(): Unit {
    apply<LicensePlugin>()

    configure<LicenseExtension> {
        header = file("$rootDir/config/license/header.txt")
        // ignore failures for now until we set the final license
        ignoreFailures = true

        excludes(listOf("**/*.webp", "**/*.png"))
    }

    tasks {
        val checkKotlinFilesLicenseTask = register("checkKotlinFilesLicense", LicenseCheck::class.java) {
            source = fileTree("src").apply {
                include("**/*.kt")
            }
        }

        val formatKotlinFilesLicenseTask = register("formatKotlinFilesLicense", LicenseFormat::class.java) {
            source = fileTree("src").apply {
                include("**/*.kt")
            }
        }

        named<Task>(LicenseBasePlugin.getLICENSE_TASK_BASE_NAME()) {
            dependsOn(checkKotlinFilesLicenseTask)
        }

        named<Task>(LicenseBasePlugin.getFORMAT_TASK_BASE_NAME()) {
            dependsOn(formatKotlinFilesLicenseTask)
        }
    }
}
