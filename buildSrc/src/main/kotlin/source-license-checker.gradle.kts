/*
 * Geekdroid is a utility library for development on the Android
 * Platform.
 *
 * Copyright (C) 2017-2025 by Frederic-Charles Barthelery.
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
 * You need to define a License header file in "$rootDir/config/license/header.txt"
 * Define the following property to enable check of license headers
 *    - CHECK_LICENSE_HEADERS : true or false. default is false
 */

val checkLicenseHeadersString = findProperty("CHECK_LICENSE_HEADERS") as String?
val checkLicenseHeader =  checkLicenseHeadersString?.toBoolean() ?: false

if (checkLicenseHeader) {
    configureSourceLicenseChecker()
}
