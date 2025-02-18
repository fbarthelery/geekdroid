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
package com.geekorum.geekdroid.accounts

import android.accounts.Account
import android.content.ContentResolver
import android.os.Build
import android.os.Bundle
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.geekorum.geekdroid.shadows.ShadowContentResolver
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
@Config(shadows = [ShadowContentResolver::class], sdk = [Build.VERSION_CODES.Q])
class SyncIsActiveFlowTest {

    private val account = Account("test", "test")

    private val AUTHORITY: String = "authority"

    @Test
    fun testWhenActiveAndNoSyncGetTheCorrectData() = runTest {
        isSyncActiveFlow(account, AUTHORITY).test {
            assertThat(awaitItem()).isFalse()

            ContentResolver.requestSync(account, AUTHORITY, Bundle())
            assertThat(awaitItem()).isTrue()

            ContentResolver.cancelSync(account, AUTHORITY)
            assertThat(awaitItem()).isFalse()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun testThatWhenInactiveGetLaterUpdates() = runTest {
        // start a sync
        ContentResolver.requestSync(account, AUTHORITY, Bundle())
        isSyncActiveFlow(account, AUTHORITY).test {
            assertThat(awaitItem()).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun testThatWhenInactiveGetOnlyCorrectUpdates() = runTest {
        // start a sync with another account
        val otherAccount = Account("toto", "toto")
        ContentResolver.requestSync(otherAccount, AUTHORITY, Bundle())

        isSyncActiveFlow(account, AUTHORITY).test {
            assertThat(awaitItem()).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
    }

}