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
import android.os.Bundle
import androidx.annotation.RequiresPermission
import com.geekorum.geekdroid.network.TokenRetriever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Allows to manipulate tokens from the Android AccountManager.
 */
open class AccountTokenRetriever(
    private val accountManager: AccountManager, private val tokenType: String,
    private val account: Account, private val notifyAuthFailure: Boolean
) : TokenRetriever {
    private var lastToken: String? = null

    @RequiresPermission(value = "android.permission.USE_CREDENTIALS", conditional = true)
    @Throws(TokenRetriever.RetrieverException::class)
    override suspend fun getToken(): String {
        val result = try {
            accountManager.getAuthToken(account, tokenType, null, notifyAuthFailure)
        } catch (e: Exception) {
            throw TokenRetriever.RetrieverException("Unable to retrieve token", e)
        }
        return withContext(Dispatchers.Main) {
            lastToken = result
            lastToken ?: throw TokenRetriever.RetrieverException("Unable to retrieve token")
        }
    }

    @RequiresPermission(
        anyOf = ["android.permission.USE_CREDENTIALS", "android.permission.MANAGE_ACCOUNTS"],
        conditional = true)
    override suspend fun invalidateToken() = withContext(Dispatchers.Main) {
        accountManager.invalidateAuthToken(account.type, lastToken)
    }

}

suspend fun AccountManager.getAuthToken(
    account: Account, tokenType: String, options: Bundle?, notifyAuthFailure: Boolean
): String? {
    return suspendCancellableCoroutine { cont ->
        getAuthToken(account, tokenType, options, notifyAuthFailure, {
            try {
                val bundle = it.result
                cont.resume(bundle[AccountManager.KEY_AUTHTOKEN] as String?)
            } catch (e: Exception) {
                cont.resumeWithException(e)
            }
        }, null)
    }
}
