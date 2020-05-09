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
