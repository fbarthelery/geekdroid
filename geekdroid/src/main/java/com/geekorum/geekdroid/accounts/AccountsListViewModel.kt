/*
 * Geekdroid is a utility library for development on the Android
 * Platform.
 *
 * Copyright (C) 2017-2023 by Frederic-Charles Barthelery.
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * ViewModel to select and use an account of a specified type.
 */
abstract class AccountsListViewModel(protected val accountManager: AccountManager,
                                     protected val accountSelector: AccountSelector,
                                     vararg accountTypes: String
) : ViewModel() {

    private val mutableSelectedAccount = MutableLiveData<Account>()
    val selectedAccount: LiveData<Account> = mutableSelectedAccount

    val accounts = AccountsLiveData(accountManager, *accountTypes)

    init {
        mutableSelectedAccount.setValue(accountSelector.savedAccount)
    }

    fun selectAccount(account: Account) {
        accountSelector.saveAccount(account)
        mutableSelectedAccount.setValue(account)
    }

}
