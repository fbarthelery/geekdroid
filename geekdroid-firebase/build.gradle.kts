import com.geekorum.build.configureJavaVersion

plugins {
    id("com.android.library")
    kotlin("android")
    id("com.geekorum.build.android-tests")
    id("com.geekorum.build.android-avdl")
    `maven-publish`
}

val archivesBaseName by extra("geekdroid-firebase")
val artifactId by extra (archivesBaseName)

android {
    val compileSdkVersion: String by rootProject.extra
    compileSdkVersion(compileSdkVersion)

    defaultConfig {
        minSdkVersion(24)
        targetSdkVersion(29)
    }
    configureJavaVersion()

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.pro")
        }
    }

    lintOptions {
        isAbortOnError = false
    }

}

dependencies {
    implementation(enforcedPlatform(kotlin("bom")))
    implementation(kotlin("stdlib-jdk8"))

    implementation(enforcedPlatform("org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.3.5"))
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-play-services")

    implementation("com.jakewharton.timber:timber:4.7.1")

    implementation("com.crashlytics.sdk.android:crashlytics:2.10.1")
    implementation("com.google.firebase:firebase-crashlytics:17.0.0-beta02")

    api("com.google.firebase:firebase-firestore-ktx:21.4.1")
    implementation("com.google.firebase:firebase-auth:19.3.0")

    // not firebase but they often work together so here we are
    implementation("com.google.android.gms:play-services-location:17.0.0")

    // not firebase but similar to gms api
    implementation("com.google.android.play:core:1.7.1")

    // fix for guava conflict
    // firebase depends on a older version of these dependencies while testImplementation dependencies
    // depends on new version
    implementation("org.checkerframework:checker-compat-qual:2.5.5")
    implementation("com.google.guava:guava:27.0.1-android")
}

apply {
    from("$projectDir/../config/source-archive.gradle")
    from("$projectDir/../config/android-maven-publication.gradle")
}
