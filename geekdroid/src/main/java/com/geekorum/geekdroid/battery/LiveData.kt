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
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.PowerManager
import androidx.lifecycle.LiveData
import kotlin.math.roundToInt

/**
 * Observe the battery to know if a low level was reached
 */
class LowBatteryLiveData(
    private val application: Application
) : LiveData<Boolean>() {
    private val intentFilter = IntentFilter().apply {
        addAction(Intent.ACTION_BATTERY_LOW)
        addAction(Intent.ACTION_BATTERY_OKAY)
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_BATTERY_OKAY -> value = false
                Intent.ACTION_BATTERY_LOW -> value = true
            }
        }
    }

    override fun onActive() {
        application.registerReceiver(broadcastReceiver, intentFilter)
        val batteryStatus = application.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        value  = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            batteryStatus?.getBooleanExtra(BatteryManager.EXTRA_BATTERY_LOW, false) ?: false
        } else {
            val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, 0) ?: 0
            val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, 100) ?: 100
            val percent = level.toFloat() / scale * 100
            percent.roundToInt() <= 15
        }
    }

    override fun onInactive() {
        application.unregisterReceiver(broadcastReceiver)
    }
}


/**
 * Observe the [PowerManager] to know if the system is in Power saving mode.
 */
class BatterySaverLiveData(
    private val application: Application,
    private val powerManager: PowerManager
) : LiveData<Boolean>() {

    private val intentFilter = IntentFilter(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED)

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            value = powerManager.isPowerSaveMode
        }
    }

    override fun onActive() {
        application.registerReceiver(broadcastReceiver, intentFilter)
        value = powerManager.isPowerSaveMode
    }

    override fun onInactive() {
        application.unregisterReceiver(broadcastReceiver)
    }
}
