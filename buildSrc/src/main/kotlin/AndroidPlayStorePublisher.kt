package com.geekorum.build

import com.android.build.gradle.AppExtension
import com.github.triplet.gradle.play.PlayPublisherExtension
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.the


// Configuration for "com.github.triplet.play" plugin
// This configuration expects the given properties
// PLAY_STORE_JSON_KEY_FILE: google play console service credentials json file to use
// PLAY_STORE_TRACK: track to publish the build, default to internal but can be set to alpha, beta or production
// PLAY_STORE_FROM_TRACK: track from which to promote a build, default to internal but can be set to alpha, beta or production

internal fun Project.configureAndroidPlayStorePublisher(): Unit {
    apply(plugin = "com.github.triplet.play")
    configure<PlayPublisherExtension> {
        defaultToAppBundles = true
        serviceAccountCredentials = file(properties["PLAY_STORE_JSON_KEY_FILE"]!!)
        track = properties.getOrDefault("PLAY_STORE_TRACK", "internal") as String
        fromTrack = properties.getOrDefault("PLAY_STORE_FROM_TRACK", "internal") as String
    }

    val android = the<AppExtension>() as ExtensionAware

    tasks.apply {
        register("publishToGooglePlayStore") {
            group = "Continuous Delivery"
            description = "Publish project to Google play store"
            dependsOn(named("publish"))
        }

        // only there for consistent naming scheme
        register("promoteOnGooglePlayStore") {
            group = "Continuous Delivery"
            description = "Promote project Google play store"
            dependsOn(named("promoteArtifact"))
        }
    }

}
