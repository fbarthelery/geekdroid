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

import android.accounts.Account
import android.content.AbstractThreadedSyncAdapter
import android.content.ContentProviderClient
import android.content.Context
import android.content.SyncResult
import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.annotation.WorkerThread
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking

/**
 * An [AbstractThreadedSyncAdapter] that support cancellation of syncs using coroutines
 */
abstract class CancellableSyncAdapter(
    context: Context,
    autoInitialize: Boolean = true,
    allowParallelSyncs: Boolean = false
) : AbstractThreadedSyncAdapter(context, autoInitialize, allowParallelSyncs) {

    private val syncs: MutableMap<Thread, Pair<CancellableSync, CoroutineScope>> = mutableMapOf()

    override fun onPerformSync(
        account: Account, extras: Bundle, authority: String, provider: ContentProviderClient, syncResult: SyncResult
    ) {
        try {
            runBlocking(CoroutineName(Thread.currentThread().name)) {
                val cancellableSync = createCancellableSync(account, extras, authority, provider, syncResult)
                syncs[Thread.currentThread()] = (cancellableSync to this)
                cancellableSync.sync()
            }
        } catch (e: CancellationException) {
            // it is expected
        }
    }

    override fun onSyncCanceled() {
        for ((t, _) in syncs) {
            onSyncCanceled(t)
        }
        syncs.clear()
    }

    override fun onSyncCanceled(thread: Thread) {
        syncs.remove(thread)?.let {
            it.first.onSyncCancelled()
            it.second.cancel()
        }
    }

    abstract fun createCancellableSync(
        account: Account, extras: Bundle, authority: String,
        provider: ContentProviderClient, syncResult: SyncResult
    ): CancellableSync


    /** Interface that should be implemented to be allow to cancel a sync */
    abstract class CancellableSync {

        @CallSuper
        open fun onSyncCancelled() {}

        @WorkerThread
        abstract suspend fun sync()

    }
}

