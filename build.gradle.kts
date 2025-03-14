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
import com.geekorum.build.SourceLicenseCheckerPlugin
import com.geekorum.build.setupGoogleContent

plugins {
    id("com.android.library") apply false
    kotlin("android") apply false
    kotlin("kapt") apply false
}


// some extra properties
extra["compileSdkInt"] = 35

allprojects {
    repositories {
        google().setupGoogleContent()
        mavenCentral()
    }
    apply<SourceLicenseCheckerPlugin>()
}

subprojects {
    group = "com.geekorum"
    version = "0.0.1"
}

tasks.register("clean", type = Delete::class) {
    doLast {
        delete(layout.buildDirectory)
    }
}


