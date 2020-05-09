/**
 * Geekdroid is a utility library for development on the Android
 * Platform.
 *
 * Copyright (C) 2017-2020 by Frederic-Charles Barthelery.
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
package com.geekorum.geekdroid.firebase.logging

import android.util.Log
import com.crashlytics.android.Crashlytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

/**
 * A [Timber.Tree] to log message in Firebase Crashlytics using Fabric.
 */
@Deprecated("Use FirebaseCrashlyticsLoggingTree",
    ReplaceWith("FirebaseCrashlyticsLoggingTree(FirebaseCrashlytics.getInstance())", "com.google.firebase.crashlytics.FirebaseCrashlytics"))
class CrashlyticsLoggingTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        Crashlytics.log(priority, tag, message)
        if (priority >= Log.ERROR) {
            t?.let {
                Crashlytics.logException(it)
            }
        }
    }
}

/**
 * A [Timber.Tree] to log message in Firebase Crashlytics.
 */
class FirebaseCrashlyticsLoggingTree(
    private val crashlytics: FirebaseCrashlytics
) : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        crashlytics.log("${priorityLetter(priority)}/$tag: $message")
        if (priority >= Log.ERROR) {
            t?.let {
                crashlytics.recordException(t)
            }
        }
    }

    private fun priorityLetter(priority: Int) = when (priority) {
        Log.ERROR -> 'E'
        Log.WARN -> 'W'
        Log.DEBUG -> 'D'
        Log.ASSERT -> 'A'
        Log.INFO -> 'I'
        Log.VERBOSE -> 'V'
        else -> 'V'
    }
}
