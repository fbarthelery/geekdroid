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
    implementation(platform(libs.kotlinx.coroutines.bom))
    api(libs.kotlinx.coroutines.core)
    api(libs.kotlinx.coroutines.play.services)

    api(libs.timber)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    api(libs.firebase.firestore.ktx)
    implementation(libs.firebase.auth)

    // not firebase but they often work together so here we are
    implementation(libs.play.services.location)

    api(libs.paging.runtime.ktx)
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
