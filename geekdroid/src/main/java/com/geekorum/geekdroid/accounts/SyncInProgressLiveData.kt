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
package com.geekorum.geekdroid.accounts

import android.Manifest
import android.accounts.Account
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE
import androidx.annotation.RequiresPermission
import androidx.lifecycle.LiveData

/**
 * Allows to observe if a Sync operation is active for a given account.
 *
 * @see ContentResolver.isSyncActive
 */
class SyncInProgressLiveData
@RequiresPermission(Manifest.permission.READ_SYNC_STATS)
constructor(
        private val account: Account,
        private val authority: String
) : LiveData<Boolean?>() {

    private var statusObserverHandle: Any? = null

    override fun onActive() {
        statusObserverHandle = ContentResolver.addStatusChangeListener(SYNC_OBSERVER_TYPE_ACTIVE) { which ->
            if (which == SYNC_OBSERVER_TYPE_ACTIVE) {
                updateValue()
            }
        }
        updateValue()
    }

    override fun onInactive() {
        ContentResolver.removeStatusChangeListener(statusObserverHandle)
    }

    @SuppressLint("MissingPermission")
    private fun updateValue() {
        val isSyncing = ContentResolver.isSyncActive(account, authority)
        postValue(isSyncing)
    }
}
