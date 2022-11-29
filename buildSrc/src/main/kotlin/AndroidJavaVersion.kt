/*
 * Geekdroid is a utility library for development on the Android
 * Platform.
 *
 * Copyright (C) 2017-2022 by Frederic-Charles Barthelery.
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

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.DefaultConfig
import com.android.build.gradle.BaseExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

/**
 * Configure java version compile options based on minSdkVersion value
 */
@Suppress("UNCHECKED_CAST")
fun BaseExtension.configureJavaVersion() {
    (this as CommonExtension<*, *, DefaultConfig, *>).configureJavaVersion()
}

fun CommonExtension<*, *, DefaultConfig, *>.configureJavaVersion() {
    val api = defaultConfig.minSdk ?: 1
    val version = when {
        api >= 30 -> JavaVersion.VERSION_11
        api >= 24 -> JavaVersion.VERSION_1_8
        api >= 19 -> JavaVersion.VERSION_1_7
        else -> JavaVersion.VERSION_1_6
    }
    compileOptions {
        sourceCompatibility = version
        targetCompatibility = version
    }

    (this as ExtensionAware).extensions.findByType(KotlinJvmOptions::class.java)?.apply {
        if (version >= JavaVersion.VERSION_1_8) {
            jvmTarget = "1.8"
        }
    }
}

/**
 * Add missing annotation processor dependencies to build on Java 11
 */
fun Project.configureAnnotationProcessorDeps() {
    dependencies {
        configurations.whenObjectAdded {
            when (name) {
                "kapt" -> {
                    add(name, "javax.xml.bind:jaxb-api:2.3.1")
                    add(name, "com.sun.xml.bind:jaxb-core:2.3.0.1")
                    add(name, "com.sun.xml.bind:jaxb-impl:2.3.2")
                }

                "annotationProcessor" -> add(name, "javax.xml.bind:jaxb-api:2.3.1")
            }
        }
    }
}
