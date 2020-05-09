package com.geekorum.build

import org.gradle.api.artifacts.repositories.MavenArtifactRepository

/**
 * Setup the content of google() repository
 */
fun MavenArtifactRepository.setupGoogleContent() = apply {
    require(name == "Google") { "Only apply to `google()` repository "}
    content {
        includeGroupByRegex("""android\.arch\..*""")
        includeGroupByRegex("""androidx\..*""")
        includeGroupByRegex("""com\.android\..*""")
        includeGroupByRegex("""com\.google\..*""")
        includeGroup("com.crashlytics.sdk.android")
        includeGroup("io.fabric.sdk.android")
        includeGroup("org.chromium.net")
        includeGroup("zipflinger")
        includeGroup("com.android")
    }
}
