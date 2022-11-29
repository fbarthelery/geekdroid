/*
 * Geekdroid is a utility library for development on the Android
 * Platform.
 *
 * Copyright (C) 2017-2022 by Frederic-Charles Barthelery.
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
import android.content.ContentProviderClient
import android.content.SyncResult
import android.os.Bundle
import com.geekorum.geekdroid.accounts.CancellableSyncAdapter.CancellableSync
import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit


class CancellableSyncTest {

    lateinit var sync: MockCancellableSync

    @Before
    fun setUp() {
        sync = MockCancellableSync()
    }

    @Test
    fun testThatJobCompleteNormally() {
        runBlocking {
            sync.sync()
            assertThat(sync.wasCompleted).isTrue()
        }
    }

    @Test
    fun testThatJobCancelledNormally() {
        runBlocking {
            val job = launch { sync.sync() }
            job.cancel()
            assertThat(sync.wasCompleted).isFalse()
        }
    }
}


class CancellableSyncAdapterTest {
    lateinit var syncAdapter: MockCancellableSyncAdapter

    @Before
    fun setUp() {
        syncAdapter = MockCancellableSyncAdapter()
    }


    @Test
    fun testThatSyncCompleteNormally() {
        syncAdapter.syncAction = { delay(50) }
        syncAdapter.onPerformSync(Account("account", "type"),
            Bundle(), "authority", mockk(),
            mockk())
        assertThat(syncAdapter.sync.wasCompleted).isTrue()
        assertThat(syncAdapter.sync.wasCancelled).isFalse()
    }

    @Test
    fun testThatSyncCancelledNormally() {
        syncAdapter.syncAction = { delay(TimeUnit.SECONDS.toMillis(100)) }
        runBlocking {
            launch(Dispatchers.Default) { // with a new context
                syncAdapter.onPerformSync(Account("account", "type"),
                    Bundle(), "authority", mockk(), mockk())
            }
            // wait a bit before cancelling
            delay(1000)
            syncAdapter.onSyncCanceled()
        }
        assertThat(syncAdapter.sync.wasCompleted).isFalse()
        assertThat(syncAdapter.sync.wasCancelled).isTrue()
    }
}


class MockCancellableSyncAdapter : CancellableSyncAdapter(mockk(), false, false) {
    var syncAction: suspend () -> Unit = { delay(50) }

    lateinit var sync: MockCancellableSync

    override fun createCancellableSync(
        account: Account, extras: Bundle, authority: String, provider: ContentProviderClient,
        syncResult: SyncResult
    ): CancellableSync {
        sync = MockCancellableSync(syncAction)
        return sync
    }
}


class MockCancellableSync(
    private val syncAction: suspend () -> Unit = { delay(50) }
) : CancellableSync() {

    var wasCancelled = false
    var wasCompleted = false

    override suspend fun sync() {
        syncAction()
        wasCompleted = true
    }

    override fun onSyncCancelled() {
        wasCancelled = true
    }
}

