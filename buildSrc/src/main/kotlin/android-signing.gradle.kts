package com.geekorum.build

/**
 * Define the following properties to set the signing configuration for release build
 *    - RELEASE_STORE_FILE : Path to the keystore file
 *    - RELEASE_STORE_PASSWORD: Password of the keystore file
 *    - RELEASE_KEY_ALIAS: key alias to use to sign
 *    - RELEASE_KEY_PASSWORD: password of the key alias
 */

if (findProperty("RELEASE_STORE_FILE") != null) {
    configureReleaseSigningConfig()
}
