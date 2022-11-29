/*
 * Geekdroid is a utility library for development on the Android
 * Platform.
 *
 * Copyright (C) 2017-2022 by Frederic-Charles Barthelery.
 *
 * This file is part of Geekdroid.
 *
 * Geekdroid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Geekdroid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Geekdroid.  If not, see <http://www.gnu.org/licenses/>.
 */
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
