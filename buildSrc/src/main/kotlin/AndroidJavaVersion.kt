package com.geekorum.build

import com.android.build.gradle.BaseExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

/**
 * Configure java version compile options based on minSdkVersion value
 */
fun BaseExtension.configureJavaVersion() {
    val api = defaultConfig.minSdkVersion?.apiLevel ?: 0
    val version = when {
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
 * Add missing annotation processord dependencies to build on Java 11
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
                // I guess that on AGP 4.x+ testAnnotationProcessor inherit from annotationProcessor
                // not on 3.6.x
                "testAnnotationProcessor" -> add(name, "javax.xml.bind:jaxb-api:2.3.1")
                "androidTestAnnotationProcessor" -> add(name, "javax.xml.bind:jaxb-api:2.3.1")
            }
        }
    }
}
