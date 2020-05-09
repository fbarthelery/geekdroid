package com.geekorum.build

/**
 Configuration for "com.github.triplet.play" plugin
 This configuration expects the given properties
 PLAY_STORE_JSON_KEY_FILE: google play console service credentials json file to use
 PLAY_STORE_TRACK: track to publish the build, default to internal but can be set to alpha, beta or production
 PLAY_STORE_FROM_TRACK: track from which to promote a build, default to internal but can be set to alpha, beta or production
*/

if (findProperty("PLAY_STORE_JSON_KEY_FILE") != null) {
    configureAndroidPlayStorePublisher()
}
