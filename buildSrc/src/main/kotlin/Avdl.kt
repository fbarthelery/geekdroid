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

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.DynamicFeaturePlugin
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.TestedExtension
import com.android.build.gradle.api.TestVariant
import com.geekorum.gradle.avdl.AvdlExtension
import com.geekorum.gradle.avdl.providers.flydroid.FlydroidPlugin
import com.geekorum.gradle.avdl.providers.flydroid.flydroid
import com.geekorum.gradle.avdl.tasks.LaunchDeviceTask
import com.geekorum.gradle.avdl.tasks.StopDeviceTask
import com.geekorum.gradle.avdl.tasks.orderForTask
import com.geekorum.gradle.avdl.tasks.registerAvdlDevicesTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.provider.Provider
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.*


fun Project.configureAvdlDevices(flydroidUrl: String, flydroidKey: String) {
    apply<FlydroidPlugin>()

    // as FlydroidPlugin add some repositories to look for dependencies
    // the repositories set in settings.gradle.kts via dependencyResolutionManagement
    // are ignored because we are in mode PREFER_PROJECT
    // to fix that we add the missing repository here too
    repositories { // this mirror contents in settings.gradle.kts
        google {
            content {
                includeGroupByRegex("""android\.arch\..*""")
                includeGroupByRegex("""androidx\..*""")
                includeGroupByRegex("""com\.android\..*""")
                includeGroupByRegex("""com\.google\..*""")
                includeGroup("com.crashlytics.sdk.android")
                includeGroup("io.fabric.sdk.android")
                includeGroup("org.chromium.net")
                includeGroup("zipflinger")
                includeGroup("com.android")
            }
        }
        mavenCentral()
        // for geekdroid
        flatDir {
            dirs("$rootDir/libs")
        }
        maven {
            url = uri("https://jitpack.io")
        }

    }
    val oneInstrumentedTestService = gradle.sharedServices.registerIfAbsent(
            "oneInstrumentedTest", OneInstrumentedTestService::class.java) {
        maxParallelUsages.set(1)
    }

    rootProject.serializeInstrumentedTestTask(oneInstrumentedTestService)

    val android = the<TestedExtension>()
    configure<AvdlExtension> {
        devices {
            android.testVariants.all {
                register("android-p-${project.path}-$baseName") {
                    setup = flydroid {
                        url = flydroidUrl
                        this.flydroidKey = flydroidKey
                        // android-q images fail, don't manage to start the tests
                        image = "android-p"
                         useTunnel = true
                    }
                }
            }
        }
    }

    tasks {
        var lastStopTask: TaskProvider<out Task>? = null
        var lastTestTask: TaskProvider<out Task>? = null
        android.testVariants.all {
            val (startTask, stopTask ) =
                registerAvdlDevicesTaskForVariant(this, listOf("android-p-${project.path}-$baseName"))
            listOf(startTask, stopTask).forEach {
                it.configure {
                    usesService(oneInstrumentedTestService)
                }
            }

            lastStopTask?.let {
                startTask.configure {
                    mustRunAfter(it)
                }
            }
            lastTestTask?.let {
                startTask.configure {
                    mustRunAfter(it)
                }
            }
            lastStopTask = stopTask
            lastTestTask = connectedInstrumentTestProvider
        }
    }

    afterEvaluate {
        // ensure that launchDeviceTask are run after StopDeviceTask of previous project
        rootProject.tasks {
            getByPath(":geekdroid-firebase:launchAvdlDebugAndroidTest")
                    .mustRunAfter(":geekdroid:stopAvdlDebugAndroidTest")
        }
    }
}

private fun TaskContainer.registerAvdlDevicesTaskForVariant(
    variant: TestVariant, devices: List<String>
): Pair<TaskProvider<LaunchDeviceTask>, TaskProvider<StopDeviceTask>> {
    val tasks =
        registerAvdlDevicesTask(variant.name, devices)
    tasks.orderForTask(variant.connectedInstrumentTestProvider)
    return tasks
}


private fun Project.serializeInstrumentedTestTask(oneInstrumentedTestService: Provider<OneInstrumentedTestService>) {
    fun Project.configureTestTasks() {
        extensions.configure<TestedExtension> {
            testVariants.all {
                connectedInstrumentTestProvider.configure {
                    usesService(oneInstrumentedTestService)
                }
            }
        }
    }

    allprojects {
        val project = this
        plugins.withType<AppPlugin> { project.configureTestTasks() }
        plugins.withType<DynamicFeaturePlugin> { project.configureTestTasks() }
        plugins.withType<LibraryPlugin> { project.configureTestTasks() }
    }
}

abstract class OneInstrumentedTestService : BuildService<BuildServiceParameters.None>
