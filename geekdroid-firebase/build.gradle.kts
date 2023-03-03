import com.geekorum.build.configureJavaVersion

plugins {
    id("com.android.library")
    kotlin("android")
    id("com.geekorum.build.android-tests")
    id("com.geekorum.build.android-avdl")
    `maven-publish`
}

android {
    val compileSdkInt: Int by rootProject.extra
    compileSdk = compileSdkInt
    namespace = "com.geekorum.geekdroid.firebase"

    defaultConfig {
        minSdk = 24
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

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
        singleVariant("debug") {
            withSourcesJar()
        }
    }

}

dependencies {
    implementation(platform(kotlin("bom")))
    implementation(kotlin("stdlib-jdk8"))

    implementation(platform("org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.6.4"))
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-play-services")

    implementation("com.jakewharton.timber:timber:5.0.1")

    implementation("com.google.firebase:firebase-crashlytics:18.3.5")

    api("com.google.firebase:firebase-firestore-ktx:24.4.3")
    implementation("com.google.firebase:firebase-auth:21.1.0")

    // not firebase but they often work together so here we are
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // fix for guava conflict
    // firebase depends on a older version of these dependencies while testImplementation dependencies
    // depends on new version
    implementation("org.checkerframework:checker-compat-qual:2.5.5")
    implementation("com.google.guava:guava:27.0.1-android")
}

apply {
    from("$projectDir/../config/source-archive.gradle")
}

publishing {
    publications {
        val pomConfiguration: (MavenPom).() -> Unit = {
            name.set("Geekdroid-Firebase")
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
            artifactId = "geekdroid-firebase"
            pom(pomConfiguration)
        }

        register<MavenPublication>("debugSnapshot") {
            afterEvaluate {
                from(components["debug"])
            }
            artifactId = "geekdroid-firebase"
            version = "$version-SNAPSHOT"
            pom(pomConfiguration)

        }
    }
}
