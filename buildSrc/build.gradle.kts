import java.net.URI

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
    implementation("com.android.tools.build:gradle:4.1.0-alpha06")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.71")
    implementation("com.genymotion:plugin:1.4.2")
    implementation("gradle.plugin.com.hierynomus.gradle.plugins:license-gradle-plugin:0.15.0")
    implementation("com.github.triplet.gradle:play-publisher:2.7.2")

    implementation("com.geekorum.gradle.avdl:plugin:0.0.2")
    implementation("com.geekorum.gradle.avdl:flydroid:0.0.2")
}
