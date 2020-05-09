package com.geekorum.build

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.dsl.TestOptions
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.DependencyConstraint
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.dsl.DependencyConstraintHandler
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.closureOf
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

const val espressoVersion = "3.2.0"
const val androidxTestRunnerVersion = "1.3.0-alpha05"
const val androidxTestCoreVersion = "1.3.0-alpha05"
const val robolectricVersion = "4.3.1"


/*
 * Configuration for espresso and robolectric usage in an Android project
 */
internal fun Project.configureTests() {
    extensions.configure<BaseExtension> {
        defaultConfig {
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            testInstrumentationRunnerArgument("clearPackageData", "true")
            testInstrumentationRunnerArgument("disableAnalytics", "true")
        }

        testOptions {
            execution = "ANDROIDX_TEST_ORCHESTRATOR"
            animationsDisabled = true

            unitTests(closureOf<TestOptions.UnitTestOptions> {
                isIncludeAndroidResources = true
            })
        }
    }


    dependencies {
        dualTestImplementation(kotlin("test-junit"))

        androidTestUtil("androidx.test:orchestrator:$androidxTestRunnerVersion")
        androidTestImplementation("androidx.test:runner:$androidxTestRunnerVersion")
        dualTestImplementation("androidx.test.ext:junit-ktx:1.1.1")

        dualTestImplementation("androidx.test:core-ktx:$androidxTestCoreVersion")
        dualTestImplementation("androidx.test:rules:$androidxTestRunnerVersion")
        // fragment testing is usually declared on debugImplementation configuration and need these dependencies
        constraints {
            debugImplementation("androidx.test:core:$androidxTestCoreVersion")
            debugImplementation("androidx.test:monitor:$androidxTestRunnerVersion")
        }

        dualTestImplementation("androidx.test.espresso:espresso-core:$espressoVersion")
        dualTestImplementation("androidx.test.espresso:espresso-contrib:$espressoVersion")
        dualTestImplementation("androidx.test.espresso:espresso-intents:$espressoVersion")

        // assertions
        dualTestImplementation("com.google.truth:truth:1.0")
        dualTestImplementation("androidx.test.ext:truth:1.3.0-alpha01")

        // mock
        testImplementation("io.mockk:mockk:1.9.3")
        androidTestImplementation("io.mockk:mockk-android:1.9.3")
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
