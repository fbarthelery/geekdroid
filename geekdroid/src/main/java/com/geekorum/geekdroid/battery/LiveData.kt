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
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlin.math.roundToInt


fun Application.lowBatteryFlow() = callbackFlow {
    val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_BATTERY_OKAY -> trySend(false)
                Intent.ACTION_BATTERY_LOW -> trySend(true)
            }
        }
    }
    val intentFilter = IntentFilter().apply {
        addAction(Intent.ACTION_BATTERY_LOW)
        addAction(Intent.ACTION_BATTERY_OKAY)
    }
    registerReceiver(broadcastReceiver, intentFilter)

    val batteryStatus = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    val value  = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        batteryStatus?.getBooleanExtra(BatteryManager.EXTRA_BATTERY_LOW, false) == true
    } else {
        val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, 0) ?: 0
        val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, 100) ?: 100
        val percent = level.toFloat() / scale * 100
        percent.roundToInt() <= 15
    }
    trySend(value)

    awaitClose {
        unregisterReceiver(broadcastReceiver)
    }
}


fun Application.isPowerSaveModeFlow(powerManager: PowerManager) = callbackFlow {
    val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            trySend(powerManager.isPowerSaveMode)
        }
    }
    registerReceiver(broadcastReceiver, IntentFilter(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED))

    trySend(powerManager.isPowerSaveMode)

    awaitClose {
        unregisterReceiver(broadcastReceiver)
    }
}