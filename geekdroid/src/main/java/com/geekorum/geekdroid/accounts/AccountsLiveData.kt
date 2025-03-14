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
package com.geekorum.geekdroid.accounts

import android.accounts.Account
import android.accounts.AccountManager
import android.accounts.OnAccountsUpdateListener
import android.annotation.SuppressLint
import android.os.Build
import androidx.lifecycle.LiveData
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Allows to observe the list of [Account] for a specified Account type.
 */
@Deprecated("Use AccountManager.accountsFlow()")
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

/**
 * Allows to observe the list of [Account] for a specified Account type.
 */
fun AccountManager.accountsFlow(vararg accountType: String): Flow<List<Account>> = callbackFlow {
    val listener = object : OnAccountsUpdateListener {
        override fun onAccountsUpdated(accounts: Array<out Account?>?) {
            val forTypes = accounts?.filter { it?.type in accountType }?.filterNotNull() ?: emptyList()
            trySend(forTypes)
        }
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        addOnAccountsUpdatedListener(
            listener, null, true,
            accountType
        )
    } else {
        addOnAccountsUpdatedListener(listener, null, true)
    }

    awaitClose {
        removeOnAccountsUpdatedListener(listener)
    }
}

/**
 * Allows to observe the list of [Account]
 */
fun AccountManager.accountsFlow(): Flow<List<Account>> = callbackFlow {
    val listener = object : OnAccountsUpdateListener {
        override fun onAccountsUpdated(accounts: Array<out Account?>?) {
            trySend(accounts?.filterNotNull() ?: emptyList())
        }
    }
    addOnAccountsUpdatedListener(listener, null, true)

    awaitClose {
        removeOnAccountsUpdatedListener(listener)
    }
}