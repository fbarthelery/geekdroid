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
import android.accounts.AccountManager
import android.accounts.OnAccountsUpdateListener
import android.annotation.SuppressLint
import android.os.Build
import androidx.lifecycle.LiveData

/**
 * Allows to observe the list of [Account] for a specified Account type.
 */
class AccountsLiveData(
        private val accountManager: AccountManager,
        vararg accountType: String?
) : LiveData<Array<Account>>() {

    private val accountTypes: List<String> = accountType.asList().requireNoNulls()

    private val accountsListener = OnAccountsUpdateListener {
        val accounts = retainAccounts(it)
        if (value?.contentEquals(accounts) != true){
            value = accounts
        }
    }

    @SuppressLint("MissingPermission")
    override fun onActive() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            accountManager.addOnAccountsUpdatedListener(accountsListener, null, true,
                    accountTypes.toTypedArray())
        } else {
            accountManager.addOnAccountsUpdatedListener(accountsListener, null, true)
        }
    }

    override fun onInactive() {
        accountManager.removeOnAccountsUpdatedListener(accountsListener)
    }

    private fun retainAccounts(accounts: Array<Account>): Array<Account> {
        return accounts.filter { it.type in accountTypes }.toTypedArray()
    }

}
