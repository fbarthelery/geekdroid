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

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.DefaultConfig
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.dsl.TestOptions
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.DependencyConstraint
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.dsl.DependencyConstraintHandler
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.*

const val espressoVersion = "3.5.1"
const val androidxTestRunnerVersion = "1.5.2"
const val androidxTestCoreVersion = "1.5.0"
const val robolectricVersion = "4.10.2"

private typealias BaseExtension = CommonExtension<*, *, DefaultConfig, *, *, *>

/*
 * Configuration for espresso and robolectric usage in an Android project
 */
@Suppress("UnstableApiUsage")
internal fun Project.configureTests() {
    extensions.configure<BaseExtension>("android") {
        defaultConfig {
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            testInstrumentationRunnerArguments += mapOf(
                "clearPackageData" to "true",
                "disableAnalytics" to "true"
            )
        }

        testOptions {
            execution = "ANDROIDX_TEST_ORCHESTRATOR"
            animationsDisabled = true

            unitTests {
                isIncludeAndroidResources = true
            }
        }
    }


    dependencies {
        dualTestImplementation(kotlin("test-junit"))

        androidTestUtil("androidx.test:orchestrator:1.4.2")
        androidTestImplementation("androidx.test:runner:$androidxTestRunnerVersion")
        dualTestImplementation("androidx.test.ext:junit-ktx:1.1.1")

        dualTestImplementation("androidx.test:core-ktx:$androidxTestCoreVersion")
        dualTestImplementation("androidx.test:rules:1.5.0")
        // fragment testing is usually declared on debugImplementation configuration and need these dependencies
        constraints {
            debugImplementation("androidx.test:core:$androidxTestCoreVersion")
            debugImplementation("androidx.test:monitor:$androidxTestRunnerVersion")
        }

        dualTestImplementation("androidx.test.espresso:espresso-core:$espressoVersion")
        dualTestImplementation("androidx.test.espresso:espresso-contrib:$espressoVersion") {
            exclude("com.google.protobuf", "protobuf-lite")
        }
        dualTestImplementation("androidx.test.espresso:espresso-intents:$espressoVersion")

        // assertions
        dualTestImplementation("com.google.truth:truth:1.0")
        dualTestImplementation("androidx.test.ext:truth:1.3.0-alpha01")

        // mock
        testImplementation("io.mockk:mockk:1.13.5")
        androidTestImplementation("io.mockk:mockk-android:1.13.5")
        testImplementation("org.robolectric:robolectric:$robolectricVersion")

        constraints {
            dualTestImplementation(kotlin("reflect")) {
                because("Use the kotlin version that we use")
            }
            androidTestImplementation("org.objenesis:objenesis") {
                because("3.x version use instructions only available with minSdk 26 (Android O)")
                version {
                    strictly("2.6")
                }
            }
        }
    }
}

fun DependencyHandler.dualTestImplementation(dependencyNotation: Any) {
    add("androidTestImplementation", dependencyNotation)
    add("testImplementation", dependencyNotation)
}

fun DependencyHandler.dualTestImplementation(dependencyNotation: Any, action: ExternalModuleDependency.() -> Unit) {
    val closure = closureOf(action)
    add("androidTestImplementation", dependencyNotation, closure)
    add("testImplementation", dependencyNotation, closure)
}

internal fun DependencyHandler.androidTestImplementation(dependencyNotation: Any): Dependency? =
    add("androidTestImplementation", dependencyNotation)

internal fun DependencyHandler.androidTestImplementation(dependencyNotation: Any, action: ExternalModuleDependency.() -> Unit) {
    val closure = closureOf(action)
    add("androidTestImplementation", dependencyNotation, closure)
}

internal fun DependencyHandler.androidTestUtil(dependencyNotation: Any): Dependency? =
    add("androidTestUtil", dependencyNotation)

internal fun DependencyHandler.testImplementation(dependencyNotation: Any): Dependency? =
    add("testImplementation", dependencyNotation)

internal fun DependencyConstraintHandler.debugImplementation(dependencyConstraintNotation: Any): DependencyConstraint? =
    add("debugImplementation", dependencyConstraintNotation)
