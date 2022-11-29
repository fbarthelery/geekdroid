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
package com.geekorum.geekdroid.accounts;

import android.accounts.Account;
import android.content.ContentResolver;
import android.os.Build;
import android.os.Bundle;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.geekorum.geekdroid.shadows.ShadowContentResolver;
import com.geekorum.geekdroid.utils.LifecycleMock;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.annotation.Config;

@RunWith(AndroidJUnit4.class)
@Config(shadows = {ShadowContentResolver.class},
        sdk = Build.VERSION_CODES.Q)
public class SyncInProgressLiveDataTest {

    @Rule
    public final InstantTaskExecutorRule archComponentRule = new InstantTaskExecutorRule();
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();
    private final LifecycleMock lifecycleMock = new LifecycleMock();

    private SyncInProgressLiveData syncInProgressLiveData;
    @Mock
    private Observer<Boolean> mockObserver;

    private final Account account = new Account("test", "test");

    private static final String AUTHORITY = "authority";

    @Before
    public void setUp() throws Exception {
        syncInProgressLiveData = new SyncInProgressLiveData(account, AUTHORITY);
    }

    @Test
    public void testWhenActiveAndNoSyncGetTheCorrectData() throws Exception {
        syncInProgressLiveData.observe(lifecycleMock, mockObserver);
        lifecycleMock.getLifecycle().handleLifecycleEvent(Lifecycle.Event.ON_START);
        Mockito.verify(mockObserver).onChanged(false);

        ContentResolver.requestSync(account, AUTHORITY, new Bundle());
        Mockito.verify(mockObserver).onChanged(true);

        ContentResolver.cancelSync(account, AUTHORITY);
        Mockito.verify(mockObserver, Mockito.times(2)).onChanged(false);
    }

    @Test
    public void testThatWhenInactiveGetLaterUpdates() throws Exception {
        syncInProgressLiveData.observe(lifecycleMock, mockObserver);

        // start a sync
        ContentResolver.requestSync(account, AUTHORITY, new Bundle());
        lifecycleMock.getLifecycle().handleLifecycleEvent(Lifecycle.Event.ON_START);
        Mockito.verify(mockObserver, Mockito.times(1)).onChanged(true);
    }

    @Test
    public void testThatWhenInactiveGetOnlyCorrectUpdates() throws Exception {
        syncInProgressLiveData.observe(lifecycleMock, mockObserver);

        // start a sync with another account
        Account otherAccount = new Account("toto", "toto");
        ContentResolver.requestSync(otherAccount, AUTHORITY, new Bundle());

        lifecycleMock.getLifecycle().handleLifecycleEvent(Lifecycle.Event.ON_START);
        Mockito.verify(mockObserver, Mockito.times(1)).onChanged(false);
    }

    @Test
    public void testThatWhenGettingInactiveNoMoreUpdates() throws Exception {
        syncInProgressLiveData.observe(lifecycleMock, mockObserver);

        lifecycleMock.getLifecycle().handleLifecycleEvent(Lifecycle.Event.ON_START);
        lifecycleMock.getLifecycle().handleLifecycleEvent(Lifecycle.Event.ON_STOP);

        // start a sync
        ContentResolver.requestSync(account, AUTHORITY, new Bundle());
        Mockito.verify(mockObserver, Mockito.never()).onChanged(true);
    }
}
