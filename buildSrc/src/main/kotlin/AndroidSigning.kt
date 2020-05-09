package com.geekorum.build

import com.android.build.gradle.BaseExtension
import org.gradle.api.Project

internal fun Project.configureReleaseSigningConfig() {
    val releaseStoreFile = findProperty("RELEASE_STORE_FILE") as? String ?: ""
    val releaseStorePassword = findProperty("RELEASE_STORE_PASSWORD") as? String ?: ""
    val releaseKeyAlias= findProperty("RELEASE_KEY_ALIAS") as? String ?: ""
    val releaseKeyPassword= findProperty("RELEASE_KEY_PASSWORD") as? String ?: ""

    extensions.configure<BaseExtension>("android") {
        signingConfigs {
            register("release") {
                storeFile =  file(releaseStoreFile)
                storePassword = releaseStorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
            }
        }

        buildTypes {
            named("release") {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }
}

