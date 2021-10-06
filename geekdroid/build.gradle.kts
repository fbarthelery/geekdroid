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
import com.geekorum.build.configureJavaVersion
import com.geekorum.build.enforcedDaggerPlatform

plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("com.geekorum.build.android-tests")
    id("com.geekorum.build.android-avdl")
    `maven-publish`
}

val archivesBaseName by extra("geekdroid")
val artifactId by extra (archivesBaseName)

android {
    val compileSdkInt: Int by rootProject.extra
    compileSdk = compileSdkInt

    defaultConfig {
        minSdk = 24
        targetSdk = 29
    }
    configureJavaVersion()

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"),
                    "proguard-rules.pro")
        }
    }

    lint {
        isAbortOnError = false
    }

    dataBinding {
        isEnabled = true
    }

}

dependencies {
    implementation("androidx.recyclerview:recyclerview:1.1.0")
    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation("com.google.android.material:material:1.1.0")
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.1.0")
    implementation("androidx.annotation:annotation:1.1.0")
    implementation("androidx.preference:preference:1.1.1")
    implementation("androidx.core:core-ktx:1.3.1")
    implementation("androidx.fragment:fragment-ktx:1.2.5")

    implementation("com.squareup.picasso:picasso:2.5.2")
    implementation("com.squareup.okhttp3:okhttp:4.6.0")

    val daggerVersion = "2.28.3"
    implementation(enforcedDaggerPlatform(daggerVersion))
    kapt(enforcedDaggerPlatform(daggerVersion))
    implementation("com.google.dagger:dagger:$daggerVersion")
    kapt("com.google.dagger:dagger-compiler:$daggerVersion")
    compileOnly("com.squareup.inject:assisted-inject-annotations-dagger2:0.5.2")
    kapt("com.squareup.inject:assisted-inject-processor-dagger2:0.5.2")

    implementation(enforcedPlatform(kotlin("bom")))
    implementation(kotlin("stdlib-jdk8"))

    implementation(enforcedPlatform("org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.3.5"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")

    implementation("androidx.lifecycle:lifecycle-livedata-core-ktx:2.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:2.2.0")
    testImplementation("androidx.arch.core:core-testing:2.1.0")

    implementation("androidx.room:room-runtime:2.2.5")
    implementation("androidx.browser:browser:1.2.0")
    implementation("androidx.work:work-runtime:2.4.0")
    implementation("androidx.navigation:navigation-common-ktx:2.3.0")
    implementation("androidx.navigation:navigation-fragment:2.3.0")
}


apply {
    from("$projectDir/../config/source-archive.gradle")
    from("$projectDir/../config/android-maven-publication.gradle")
}
