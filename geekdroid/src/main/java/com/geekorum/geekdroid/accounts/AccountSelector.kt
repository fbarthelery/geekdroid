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
import android.annotation.SuppressLint
import android.app.Application
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

import javax.inject.Inject

/**
 * Helper class to select an user account and save it in order to use it again later.
 */
class AccountSelector internal constructor(
        private val preferences: SharedPreferences,
        private val accountManager: AccountManager
) {

    val savedAccount: Account?
        get() {
            val accountName = preferences.getString(PREF_ACCOUNT_NAME, "")!!
            val accountType = preferences.getString(PREF_ACCOUNT_TYPE, "")!!
            return if (isExistingAccount(accountName, accountType)) {
                Account(accountName, accountType)
            } else null
        }

    @Inject
    constructor(application: Application, accountManager: AccountManager)
            : this(PreferenceManager.getDefaultSharedPreferences(application), accountManager)


    fun saveAccount(account: Account) {
        preferences.edit()
                .putString(PREF_ACCOUNT_NAME, account.name)
                .putString(PREF_ACCOUNT_TYPE, account.type)
                .apply()
    }

    @SuppressLint("MissingPermission")
    fun isExistingAccount(accountName: String, accountType: String): Boolean {
        // we don't need that permission to access how own authenticator
        val accounts = accountManager.getAccountsByType(accountType)
        for (account in accounts) {
            if (accountName == account.name) {
                return true
            }
        }
        return false
    }

    fun isExistingAccount(account: Account?): Boolean {
        account?.let {
            return isExistingAccount(account.name, account.type)
        }
        return false
    }

    companion object {
        private const val PREF_ACCOUNT_NAME = "account_selector_saved_account_name"
        private const val PREF_ACCOUNT_TYPE = "account_selector_saved_account_type"
    }

}
