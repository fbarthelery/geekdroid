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
import android.os.Build
import android.os.Looper.getMainLooper
import android.os.PowerManager
import androidx.core.content.getSystemService
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowPowerManager
import kotlin.test.BeforeTest


@RunWith(AndroidJUnit4::class)
@Config(minSdk = Build.VERSION_CODES.Q)
class PowerSaveModeFlowTest {
    lateinit var shadowPowerManager: ShadowPowerManager
    lateinit var application: Application
    lateinit var powerManager: PowerManager

    @BeforeTest
    fun setUp() {
        application = ApplicationProvider.getApplicationContext()
        powerManager = application.getSystemService()!!
        shadowPowerManager = shadowOf(powerManager)
    }

    @Test
    fun testThatWhenPowerSaveModeChangedEmitValue() = runTest {
        isPowerSaveModeFlow(application, powerManager).test {
            assertThat(awaitItem()).isFalse()

            shadowPowerManager.setIsPowerSaveMode(true)
            application.sendBroadcast(Intent(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED))
            shadowOf(getMainLooper()).idle()
            assertThat(awaitItem()).isTrue()

            shadowPowerManager.setIsPowerSaveMode(false)
            application.sendBroadcast(Intent(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED))
            shadowOf(getMainLooper()).idle()
            assertThat(awaitItem()).isFalse()

        }
    }

}