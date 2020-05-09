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
package com.geekorum.geekdroid.shadows;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.OnAccountsUpdateListener;
import android.os.Handler;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Extends {@link org.robolectric.shadows.ShadowAccountManager} to add support for
 * {@link AccountManager#addOnAccountsUpdatedListener(OnAccountsUpdateListener, Handler, boolean, String[])}
 * which was added in API 26. This will probably not needed on later version of Robolectric (current is 3.4.2)
 */
@Implements(AccountManager.class)
public class ShadowAccountManager extends org.robolectric.shadows.ShadowAccountManager {

    private class AccountTypesListener {
        private final OnAccountsUpdateListener listener;
        private final String[] accountTypes;

        AccountTypesListener(OnAccountsUpdateListener listener, String[] accountTypes) {
            this.listener = listener;
            this.accountTypes = accountTypes;
        }
    }

    private List<AccountTypesListener> accountTypesListener = new LinkedList<>();

    @Implementation
    public void addOnAccountsUpdatedListener(final OnAccountsUpdateListener listener,
                                             Handler handler, boolean updateImmediately, String[] accountTypes) {

        if (containsAccountListener(listener)) {
            return;
        }

        addListener(listener, accountTypes);

        if (updateImmediately) {
            listener.onAccountsUpdated(getAccounts(accountTypes));
        }
    }

    @Override
    public void removeOnAccountsUpdatedListener(OnAccountsUpdateListener listener) {
        super.removeOnAccountsUpdatedListener(listener);
        ListIterator<AccountTypesListener> it = accountTypesListener.listIterator();
        while (it.hasNext()) {
            AccountTypesListener next = it.next();
            if (next.listener == listener) {
                it.remove();
            }
        }
    }

    @Override
    public void addAccount(Account account) {
        super.addAccount(account);
        notifyListeners(account.type);
    }

    private boolean containsAccountListener(OnAccountsUpdateListener listener) {
        for (AccountTypesListener list : accountTypesListener) {
            if (list == listener) {
                return true;
            }
        }
        return false;
    }

    private void addListener(OnAccountsUpdateListener listener, String[] accountTypes) {
        AccountTypesListener l = new AccountTypesListener(listener, accountTypes);
        accountTypesListener.add(l);
    }

    private Account[] getAccounts(String[] accountTypes) {
        Account[] accounts = getAccounts();
        List<Account> result = new LinkedList<>();
        for (Account account : accounts) {
            for (String type : accountTypes) {
                if (type.equals(account.type)) {
                    result.add(account);
                }
            }
        }
        return result.toArray(new Account[result.size()]);
    }

    private void notifyListeners(String accountType) {
        for (AccountTypesListener typesListener : accountTypesListener) {
            for (String type : typesListener.accountTypes) {
                if (type.equals(accountType)) {
                    typesListener.listener.onAccountsUpdated(getAccounts(new String[]{accountType}));
                }
            }
        }
    }
}
