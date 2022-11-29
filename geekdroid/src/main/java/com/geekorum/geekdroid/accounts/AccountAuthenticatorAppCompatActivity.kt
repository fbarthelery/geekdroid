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

import android.accounts.AccountAuthenticatorActivity
import android.accounts.AccountAuthenticatorResponse
import android.accounts.AccountManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * An [AccountAuthenticatorActivity] that supports and AppCompat theme
 */
open class AccountAuthenticatorAppCompatActivity : AppCompatActivity() {

    private var accountAuthenticatorResponse: AccountAuthenticatorResponse? = null
    var accountAuthenticatorResult: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        accountAuthenticatorResponse = intent.getParcelableExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE)
        accountAuthenticatorResponse?.onRequestContinued()
    }

    override fun finish() {
        accountAuthenticatorResponse?.apply {
            if (accountAuthenticatorResult != null) {
                onResult(accountAuthenticatorResult)
            } else {
                onError(AccountManager.ERROR_CODE_CANCELED, "cancelled")
            }
        }
        super.finish()
    }
}
