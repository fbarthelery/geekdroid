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
package com.geekorum.geekdroid.shadows;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.SyncStatusObserver;
import android.os.Bundle;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

import java.util.HashSet;
import java.util.Set;

/**
 * Extends {@link org.robolectric.shadows.ShadowContentResolver} to add support for
 * {@link ContentResolver#addStatusChangeListener(int, SyncStatusObserver)}
 * which is not implemented in Robolectric's {@link org.robolectric.shadows.ShadowContentResolver}.
 * This will maybe not needed on later version of Robolectric (current is 3.4.2)
 */
@Implements(ContentResolver.class)
public class ShadowContentResolver extends org.robolectric.shadows.ShadowContentResolver {

    private static Set<SyncStatusObserver> observers = new HashSet<>();

    @Implementation
    public static Object addStatusChangeListener(int mask, SyncStatusObserver observer) {
        observers.add(observer);
        return observer;
    }

    @Implementation
    public static void removeStatusChangeListener(Object handle) {
        observers.remove(handle);
    }

    @Implementation
    public static void requestSync(Account account, String authority, Bundle extras) {
        Status status = getStatus(account, authority, true);
        int oldSyncRequest = status.syncRequests;
        org.robolectric.shadows.ShadowContentResolver.requestSync(account, authority, extras);
        if (oldSyncRequest != status.syncRequests) {
            notifyListeners(ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE);
        }
    }

    @Implementation
    public static void cancelSync(Account account, String authority) {
        Status status = getStatus(account, authority);
        int oldSyncRequest = status.syncRequests;
        org.robolectric.shadows.ShadowContentResolver.cancelSync(account, authority);
        if (oldSyncRequest != status.syncRequests) {
            notifyListeners(ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE);
        }
    }

    private static void notifyListeners(int which) {
        for (SyncStatusObserver observer : observers) {
            observer.onStatusChanged(which);
        }
    }

}
