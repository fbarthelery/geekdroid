/*
 * Geekdroid is a utility library for development on the Android
 * Platform.
 *
 * Copyright (C) 2017-2020 by Frederic-Charles Barthelery.
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


plugins {
    `kotlin-dsl`
}


version = "1.0"

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}

repositories {
    gradlePluginPortal()
    jcenter()
    google()
    maven {
        // Workaround for genymotion plugin not working on gradle 5.0
        // we publish 1.4.2 version with fixes
        url = uri("https://raw.githubusercontent.com/fbarthelery/genymotion-gradle-plugin/master/repo/")
    }
    maven {
        url = uri("https://kotlin.bintray.com/kotlinx")
    }
}

dependencies {
    implementation("com.android.tools.build:gradle:3.6.3")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.71")
    implementation("com.genymotion:plugin:1.4.2")
    implementation("gradle.plugin.com.hierynomus.gradle.plugins:license-gradle-plugin:0.15.0")
    implementation("com.github.triplet.gradle:play-publisher:2.7.2")

    implementation("com.geekorum.gradle.avdl:plugin:0.0.2")
    implementation("com.geekorum.gradle.avdl:flydroid:0.0.2")
}
