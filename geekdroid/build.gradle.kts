import com.geekorum.build.configureJavaVersion
import com.geekorum.build.enforcedDaggerPlatform

plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("com.geekorum.build.android-tests")
    id("com.geekorum.build.android-avdl")
}

val archivesBaseName by extra("geekdroid")
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
    implementation("androidx.preference:preference:1.1.0")
    implementation("androidx.core:core-ktx:1.2.0")
    implementation("androidx.fragment:fragment-ktx:1.2.2")

    implementation("com.squareup.picasso:picasso:2.5.2")
    implementation("com.squareup.okhttp3:okhttp:4.1.0")

    val daggerVersion = "2.27"
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

    implementation("androidx.room:room-runtime:2.2.4")
    implementation("androidx.browser:browser:1.2.0")
    implementation("androidx.work:work-runtime:2.3.3")
    implementation("androidx.navigation:navigation-common-ktx:2.2.1")
    implementation("androidx.navigation:navigation-fragment:2.2.1")
}


apply {
    // from("$projectDir/../config/android-checkstyle.gradle")
    from("$projectDir/../config/source-archive.gradle")
    from("$projectDir/../config/android-maven-publication.gradle")
}
