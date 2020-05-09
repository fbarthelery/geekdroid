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
package com.geekorum.geekdroid.battery

import android.app.Application
import android.content.Intent
import android.os.BatteryManager
import android.os.Build
import android.os.PowerManager
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.content.getSystemService
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.mockk
import io.mockk.verifySequence
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowPowerManager
import kotlin.test.BeforeTest

@RunWith(AndroidJUnit4::class)
class BatterySaverLiveDataTest {

    lateinit var liveData: BatterySaverLiveData
    lateinit var shadowPowerManager: ShadowPowerManager
    lateinit var application: Application

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @BeforeTest
    fun setUp() {
        application = ApplicationProvider.getApplicationContext()
        val powerManager: PowerManager = application.getSystemService()!!
        shadowPowerManager = Shadows.shadowOf(powerManager)
        liveData = BatterySaverLiveData(application, powerManager)
    }

    @Test
    fun testThatWhenPowerSaveModeChangedLiveDataIsUpdated() {
        val mockObserver = mockk<Observer<Boolean>>(relaxed = true)
        liveData.observeForever(mockObserver)
        shadowPowerManager.setIsPowerSaveMode(true)
        application.sendBroadcast(Intent(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED))
        shadowPowerManager.setIsPowerSaveMode(false)
        application.sendBroadcast(Intent(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED))
        verifySequence {
            mockObserver.onChanged(false)
            mockObserver.onChanged(true)
            mockObserver.onChanged(false)
        }
    }

}

@RunWith(AndroidJUnit4::class)
class LowBatteryLiveDataTest {

    lateinit var liveData: LowBatteryLiveData
    lateinit var application: Application

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @BeforeTest
    fun setUp() {
        application = ApplicationProvider.getApplicationContext()
        liveData = LowBatteryLiveData(application)
    }

    @Test
    fun testThatBatteryGetLowLiveDataIsUpdated() {
        val mockObserver = mockk<Observer<Boolean>>(relaxed = true)
        liveData.observeForever(mockObserver)
        application.sendBroadcast(Intent(Intent.ACTION_BATTERY_LOW))
        verifySequence {
            // first battery is okay
            mockObserver.onChanged(false)
            mockObserver.onChanged(true)
        }
    }

    @Test
    fun testThatBatteryGetOkayLiveDataIsUpdated() {
        val mockObserver = mockk<Observer<Boolean>>(relaxed = true)
        liveData.observeForever(mockObserver)
        application.sendBroadcast(Intent(Intent.ACTION_BATTERY_OKAY))
        verifySequence {
            // first battery is okay
            mockObserver.onChanged(false)
            // second when broadcast
            mockObserver.onChanged(false)
        }
    }

    @Test
    @Config(minSdk = Build.VERSION_CODES.P)
    fun testThatOnPWhenBatteryIsAlreadyLowLivedataIsCorrect() {
        val mockObserver = mockk<Observer<Boolean>>(relaxed = true)
        application.sendStickyBroadcast(Intent(Intent.ACTION_BATTERY_CHANGED).apply {
            putExtra(BatteryManager.EXTRA_BATTERY_LOW, true)
        })
        liveData.observeForever(mockObserver)
        verifySequence {
            mockObserver.onChanged(true)
        }
    }

    @Test
    @Config(maxSdk = Build.VERSION_CODES.O_MR1)
    fun testThatBeforePWhenBatteryIsAlreadyLowLivedataIsCorrect() {
        val mockObserver = mockk<Observer<Boolean>>(relaxed = true)
        application.sendStickyBroadcast(Intent(Intent.ACTION_BATTERY_CHANGED).apply {
            putExtra(BatteryManager.EXTRA_LEVEL, 5)
            putExtra(BatteryManager.EXTRA_SCALE, 100)
        })
        liveData.observeForever(mockObserver)
        verifySequence {
            mockObserver.onChanged(true)
        }
    }
}
