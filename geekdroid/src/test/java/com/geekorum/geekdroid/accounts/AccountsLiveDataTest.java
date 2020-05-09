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
package com.geekorum.geekdroid.accounts;

import android.accounts.Account;
import android.accounts.AccountManager;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.geekorum.geekdroid.utils.LifecycleMock;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAccountManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(AndroidJUnit4.class)
@Config(shadows = {com.geekorum.geekdroid.shadows.ShadowAccountManager.class})
public class AccountsLiveDataTest {

    @Rule
    public InstantTaskExecutorRule archComponentRule = new InstantTaskExecutorRule();
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

    private static final String TEST_ACCOUNT_TYPE = "test.account";

    private final Account[] testAccounts = new Account[]{new Account("test1", TEST_ACCOUNT_TYPE),
            new Account("test2", TEST_ACCOUNT_TYPE),
            new Account("test3", TEST_ACCOUNT_TYPE),
    };
    private final Account testAccountMore = new Account("test4 more", TEST_ACCOUNT_TYPE);

    private static final String OTHER_ACCOUNT_TYPE = "other";
    private final Account otherAccount = new Account("test1", OTHER_ACCOUNT_TYPE);
    private final Account otherAccount2 = new Account("test2", OTHER_ACCOUNT_TYPE);

    @Mock
    private Observer<Account[]> mockObserver;
    private LifecycleMock lifecycleMock = new LifecycleMock();
    private ShadowAccountManager shadowAccountManager;

    private AccountsLiveData accountsLiveData;

    @Before
    public void setUp() throws Exception {
        AccountManager accountManager = AccountManager.get(RuntimeEnvironment.application);
        shadowAccountManager = Shadows.shadowOf(accountManager);
        for (Account account : testAccounts) {
            shadowAccountManager.addAccount(account);
        }
        shadowAccountManager.addAccount(otherAccount);

        accountsLiveData = new AccountsLiveData(accountManager, TEST_ACCOUNT_TYPE);
    }

    @Test
    public void testWhenActiveGetTheCorrectData() throws Exception {
        accountsLiveData.observe(lifecycleMock, mockObserver);
        lifecycleMock.getLifecycle().handleLifecycleEvent(Lifecycle.Event.ON_START);
        Mockito.verify(mockObserver).onChanged(testAccounts);
    }

    @Test
    public void testThatWhenActiveGetCorrectUpdates() throws Exception {
        accountsLiveData.observe(lifecycleMock, mockObserver);
        lifecycleMock.getLifecycle().handleLifecycleEvent(Lifecycle.Event.ON_START);

        Mockito.verify(mockObserver).onChanged(testAccounts);

        // add some accounts
        shadowAccountManager.addAccount(otherAccount2);
        shadowAccountManager.addAccount(testAccountMore);
        List<Account> accountList = new ArrayList<>(Arrays.asList(testAccounts));
        accountList.add(testAccountMore);
        Account[] newTestAccounts = accountList.toArray(new Account[accountList.size()]);

        Mockito.verify(mockObserver, Mockito.times(1)).onChanged(newTestAccounts);
    }

    @Test
    public void testThatWhenInactiveGetLaterUpdates() throws Exception {
        accountsLiveData.observe(lifecycleMock, mockObserver);
        lifecycleMock.getLifecycle().handleLifecycleEvent(Lifecycle.Event.ON_STOP);

        // add some accounts
        shadowAccountManager.addAccount(testAccountMore);
        shadowAccountManager.addAccount(otherAccount2);
        List<Account> accountList = new ArrayList<>(Arrays.asList(testAccounts));
        accountList.add(testAccountMore);
        Account[] newTestAccounts = accountList.toArray(new Account[accountList.size()]);

        lifecycleMock.getLifecycle().handleLifecycleEvent(Lifecycle.Event.ON_START);

        Mockito.verify(mockObserver, Mockito.times(1)).onChanged(newTestAccounts);
    }
}
