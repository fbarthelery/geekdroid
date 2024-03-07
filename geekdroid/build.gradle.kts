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
import groovy.util.Node
import groovy.util.NodeList

plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("com.geekorum.build.android-tests")
    id("com.geekorum.build.android-avdl")
    `maven-publish`
}


android {
    val compileSdkInt: Int by rootProject.extra
    compileSdk = compileSdkInt
    namespace = "com.geekorum.geekdroid"

    defaultConfig {
        minSdk = 24
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"),
                    "proguard-rules.pro")
        }
    }

    lint {
        abortOnError = false
    }

    buildFeatures {
        dataBinding = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.androidx.compose.compiler.get()
    }

    packaging {
        resources {
            excludes += listOf("META-INF/LICENSE.md", "META-INF/LICENSE-notice.md")
        }
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
        singleVariant("debug") {
            withSourcesJar()
        }
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    api(libs.recyclerview)
    api(libs.material)
    api(libs.constraintlayout)
    api(libs.coordinatorlayout)
    implementation(libs.annotation)
    implementation(libs.core.ktx)
    api(libs.fragment.ktx)

    api(libs.okhttp)

    implementation(libs.dagger.compiler)
    kapt(libs.dagger.compiler)

    implementation(platform(kotlin("bom")))
    implementation(kotlin("stdlib-jdk8"))

    implementation(platform(libs.kotlinx.coroutines.bom))
    implementation(libs.kotlinx.coroutines.core)

    api(libs.lifecycle.livedata.core.ktx)
    api(libs.lifecycle.viewmodel.savedstate)
    testImplementation(libs.core.testing)

    implementation(libs.room.runtime)
    implementation(libs.browser)
    implementation(libs.work.runtime)
    implementation(libs.preference.ktx)

    // compose stuff
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.activity.compose)

}


apply {
    from("$projectDir/../config/source-archive.gradle")
}

publishing {
    publications {
        val pomConfiguration: (MavenPom).() -> Unit = {
            name.set("Geekdroid")
            description.set("An Android library used in various Android projects. ")
            licenses {
                license {
                    name.set("GPL-3.0-or-later")
                    url.set("https://www.gnu.org/licenses/gpl-3.0.html")
                    distribution.set("repo")
                }
            }
            inceptionYear.set("2017")
        }

        register<MavenPublication>("release") {
            afterEvaluate {
                from(components["release"])
            }
            artifactId = "geekdroid"
            pom(pomConfiguration)
        }

        register<MavenPublication>("debugSnapshot") {
            afterEvaluate {
                from(components["debug"])
            }
            artifactId = "geekdroid"
            version = "$version-SNAPSHOT"
            pom(pomConfiguration)

        }
    }
}
