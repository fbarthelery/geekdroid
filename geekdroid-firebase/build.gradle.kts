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
    val compileSdkInt: Int by rootProject.extra
    compileSdk = compileSdkInt
    namespace = "com.geekorum.geekdroid.firebase"

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
        abortOnError = false
    }

}

dependencies {
    implementation(enforcedPlatform(kotlin("bom")))
    implementation(kotlin("stdlib-jdk8"))

    implementation(enforcedPlatform("org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.3.5"))
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-play-services")

    implementation("com.jakewharton.timber:timber:5.0.1")

    implementation("com.crashlytics.sdk.android:crashlytics:2.10.1")
    implementation("com.google.firebase:firebase-crashlytics:18.3.2")

    api("com.google.firebase:firebase-firestore-ktx:24.4.1")
    implementation("com.google.firebase:firebase-auth:21.1.0")

    // not firebase but they often work together so here we are
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // not firebase but similar to gms api
    implementation("com.google.android.play:core:1.10.3")

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
