/*
 * Geekdroid is a utility library for development on the Android
 * Platform.
 *
 * Copyright (C) 2017-2025 by Frederic-Charles Barthelery.
 *
 * This file is part of Geekdroid.
 *
 * Geekdroid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Geekdroid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Geekdroid.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.geekorum.build

import com.android.build.api.dsl.AndroidSourceSet
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.DynamicFeaturePlugin
import com.hierynomus.gradle.license.LicenseBasePlugin
import com.hierynomus.gradle.license.tasks.LicenseCheck
import com.hierynomus.gradle.license.tasks.LicenseFormat
import nl.javadude.gradle.plugins.license.License
import nl.javadude.gradle.plugins.license.LicenseExtension
import nl.javadude.gradle.plugins.license.LicensePlugin
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinAndroidPluginWrapper
import org.jetbrains.kotlin.gradle.plugin.KotlinJsPluginWrapper
import org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper
import java.util.Locale

internal fun Project.configureSourceLicenseChecker() {
    apply<LicensePlugin>()

    configure<LicenseExtension> {
        header = file("$rootDir/config/license/header.txt")
        mapping("java", "SLASHSTAR_STYLE")
        mapping("kt", "SLASHSTAR_STYLE")

        excludes(listOf("**/*.webp", "**/*.png"))
    }

    // the LicensePlugin doesn't configure itself properly on DynamicFeaturePlugin
    // Copied the code to configure it
    plugins.withType(DynamicFeaturePlugin::class.java) {
        configureAndroid()
    }
    // make the license tasks looks for kotlin files in an Android project
    plugins.withType(KotlinAndroidPluginWrapper::class.java) {
        configureKotlinAndroid()
    }

    // make the license tasks for kotlin js project
    plugins.withType(KotlinJsPluginWrapper::class.java) {
        configureKotlin()
    }

    plugins.withType(KotlinMultiplatformPluginWrapper::class.java) {
        configureKotlin()
    }
}

@OptIn(ExperimentalStdlibApi::class)
private fun Project.configureKotlin() {
    val kotlin = the<KotlinProjectExtension>()
    val taskInfix = ""
    kotlin.sourceSets.configureEach {
        val kotlinSource = this
        val sourceSetTaskName =
            "${LicenseBasePlugin.getLICENSE_TASK_BASE_NAME()}${taskInfix}${name.capitalize()}"
        logger.info("Adding $sourceSetTaskName task for sourceSet ${kotlinSource.name}")
        if (sourceSetTaskName in tasks.names) {
            // tasks may have already been added by configuration for the Android plugin
            logger.info("Tasks $sourceSetTaskName already exists. Skip")
            return@configureEach
        }
        tasks.register(sourceSetTaskName, LicenseCheck::class.java) {
            source(kotlinSource.kotlin)
        }
        val sourceSetFormatTaskName =
            "${LicenseBasePlugin.getFORMAT_TASK_BASE_NAME()}${taskInfix}${name.capitalize()}"
        tasks.register(sourceSetFormatTaskName, LicenseFormat::class.java) {
            source(kotlinSource.kotlin)
        }
    }
}

@OptIn(ExperimentalStdlibApi::class)
private fun Project.configureKotlinAndroid() {
    val kotlin = the<KotlinProjectExtension>()
    val android = the<BaseExtension>()
    val taskInfix = "Android"
    android.sourceSets.configureEach {
        val kotlinSource = kotlin.sourceSets[name]
        logger.info("Adding kotlin sources from sourceSet $name to License plugin tasks")
        val sourceSetTaskName =
            "${LicenseBasePlugin.getLICENSE_TASK_BASE_NAME()}${taskInfix}${name.capitalize()}"
        tasks.named(sourceSetTaskName, LicenseCheck::class.java) {
            source(kotlinSource.kotlin, manifest.srcFile)
        }
        val sourceSetFormatTaskName =
            "${LicenseBasePlugin.getFORMAT_TASK_BASE_NAME()}${taskInfix}${name.capitalize()}"
        tasks.named(sourceSetFormatTaskName, LicenseFormat::class.java) {
            source(kotlinSource.kotlin, manifest.srcFile)
        }
    }
}


private fun Project.configureAndroid() {
    val android = the<BaseExtension>()
    configureSourceSetRule(android.sourceSets, "Android") { ss ->
        @Suppress("DEPRECATION")
        when (ss) {
            // the dsl.AndroidSourceSet don't expose any getter, so we still need to cast it
            is com.android.build.gradle.api.AndroidSourceSet -> {
        ss.java.getSourceFiles() + ss.res.getSourceFiles() + fileTree(ss.manifest.srcFile)
            }
            else -> fileTree()
        }
    }
}

/**
 * Dynamically create a task for each sourceSet, and register with check
 */
@Suppress("DefaultLocale")
private fun Project.configureSourceSetRule(androidSourceSetContainer: NamedDomainObjectContainer<out AndroidSourceSet>,
                                           taskInfix: String, sourceSetSources: (AndroidSourceSet) -> FileTree) {
    // This follows the other check task pattern
    androidSourceSetContainer.configureEach {
        val sourceSetTaskName = "${LicenseBasePlugin.getLICENSE_TASK_BASE_NAME()}${taskInfix}${name.capitalize()}"
        logger.info("Adding $sourceSetTaskName task for sourceSet $name")

        val checkTask = tasks.register(sourceSetTaskName, LicenseCheck::class.java)
        configureForSourceSet(this, checkTask, sourceSetSources)

        // Add independent license task, which will perform format
        val sourceSetFormatTaskName = "${LicenseBasePlugin.getFORMAT_TASK_BASE_NAME()}${taskInfix}${name.capitalize()}"
        val formatTask = tasks.register(sourceSetFormatTaskName, LicenseFormat::class.java)
        configureForSourceSet(this, formatTask, sourceSetSources)
    }
}

private fun configureForSourceSet(sourceSet: AndroidSourceSet, task: TaskProvider<out License>, sourceSetSources: (AndroidSourceSet) -> FileTree) {
    task.configure {
        // Explicitly set description
        description = "Scanning license on ${sourceSet.name} files"

        // Default to all source files from SourceSet
        source = sourceSetSources(sourceSet)
    }
}

private fun String.capitalize() =
    replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
