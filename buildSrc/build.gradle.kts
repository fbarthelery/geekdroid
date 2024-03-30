/*
 * Geekdroid is a utility library for development on the Android
 * Platform.
 *
 * Copyright (C) 2017-2024 by Frederic-Charles Barthelery.
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

kotlin{
    sourceSets {
        all {
            languageSettings {
                optIn("kotlin.RequiresOptIn")
            }
        }
    }
}
repositories {
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
    gradlePluginPortal()
}

dependencies {
    implementation("com.android.tools.build:gradle:8.3.1")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.23")
    implementation("gradle.plugin.com.hierynomus.gradle.plugins:license-gradle-plugin:0.16.1")
    implementation("com.github.triplet.gradle:play-publisher:3.7.0")

    implementation("com.geekorum.gradle.avdl:plugin:0.0.3")
    implementation("com.geekorum.gradle.avdl:flydroid:0.0.3")
}
