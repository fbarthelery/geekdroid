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
package com.geekorum.geekdroid.battery

import android.app.Application
import android.content.Intent
import android.os.BatteryManager
import android.os.Build
import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import kotlin.test.BeforeTest
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
@Config(minSdk = Build.VERSION_CODES.Q)
class LowBatteryFlowTest {

    lateinit var application: Application

    @BeforeTest
    fun setUp() {
        application = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun testThatBatteryGetLowEmitValue() = runTest {
        application.lowBatteryFlow().test {
            assertThat(awaitItem()).isFalse()

            application.sendBroadcast(Intent(Intent.ACTION_BATTERY_LOW))
            shadowOf(Looper.getMainLooper()).idle()

            assertThat(awaitItem()).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun testThatBatteryGetOkayEmitValue() = runTest {
        application.lowBatteryFlow().test {
            // first battery is okay
            assertThat(awaitItem()).isFalse()

            // second when broadcast
            application.sendBroadcast(Intent(Intent.ACTION_BATTERY_OKAY))
            shadowOf(Looper.getMainLooper()).idle()

            assertThat(awaitItem()).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    @Config(minSdk = Build.VERSION_CODES.P)
    fun testThatOnPWhenBatteryIsAlreadyLowFirstEmitValueIsCorrect() = runTest {
        application.sendStickyBroadcast(Intent(Intent.ACTION_BATTERY_CHANGED).apply {
            putExtra(BatteryManager.EXTRA_BATTERY_LOW, true)
        })

        application.lowBatteryFlow().test {
            assertThat(awaitItem()).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }
}