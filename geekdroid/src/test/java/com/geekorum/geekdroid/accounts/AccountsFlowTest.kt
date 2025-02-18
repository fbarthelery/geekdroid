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
import android.accounts.AccountManager
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowAccountManager
import kotlin.test.BeforeTest
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
@Config(shadows = [com.geekorum.geekdroid.shadows.ShadowAccountManager::class], sdk = [Build.VERSION_CODES.Q])
class AccountsFlowTest {

    private lateinit var shadowAccountManager: ShadowAccountManager
    private lateinit var accountManager: AccountManager

    private val TEST_ACCOUNT_TYPE: String = "test.account"
    private val testAccounts = listOf(
        Account("test1", TEST_ACCOUNT_TYPE),
        Account("test2", TEST_ACCOUNT_TYPE),
        Account("test3", TEST_ACCOUNT_TYPE),
    )
    private val testAccountMore = Account("test4 more", TEST_ACCOUNT_TYPE)


    private val OTHER_ACCOUNT_TYPE: String = "other"
    private val otherAccount = Account("test1", OTHER_ACCOUNT_TYPE)
    private val otherAccount2 = Account("test2", OTHER_ACCOUNT_TYPE)

    @BeforeTest
    fun setUp() {
        accountManager = AccountManager.get(RuntimeEnvironment.getApplication())
        shadowAccountManager = Shadows.shadowOf(accountManager)
        for (account in testAccounts) {
            shadowAccountManager.addAccount(account)
        }
        shadowAccountManager.addAccount(otherAccount)

    }

    @Test
    fun testWhenActiveGetTheCorrectData() = runTest {
        accountManager.accountsFlow(TEST_ACCOUNT_TYPE)
            .test {
                assertThat(awaitItem()).isEqualTo(testAccounts)
                cancelAndIgnoreRemainingEvents()
            }
    }

    @Test
    fun testThatWhenActiveGetCorrectUpdates() = runTest {
        accountManager.accountsFlow(TEST_ACCOUNT_TYPE)
            .test {
                var expected = testAccounts
                assertThat(awaitItem()).isEqualTo(expected)

                // add some accounts
                shadowAccountManager.addAccount(otherAccount2)
                shadowAccountManager.addAccount(testAccountMore)
                expected = testAccounts + testAccountMore

                assertThat(awaitItem()).isEqualTo(expected)
                cancelAndIgnoreRemainingEvents()
            }
    }

    @Test
    fun testThatWhenInactiveGetLaterUpdates() = runTest {
        // add some accounts
        shadowAccountManager.addAccount(testAccountMore)
        shadowAccountManager.addAccount(otherAccount2)

        accountManager.accountsFlow(TEST_ACCOUNT_TYPE)
            .test {
                var expected = testAccounts + testAccountMore

                assertThat(awaitItem()).isEqualTo(expected)
                cancelAndIgnoreRemainingEvents()
            }
    }
}