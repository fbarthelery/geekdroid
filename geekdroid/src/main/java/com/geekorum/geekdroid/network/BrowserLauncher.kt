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
package com.geekorum.geekdroid.network

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsService
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.browser.customtabs.CustomTabsSession
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import javax.inject.Inject

/**
 * Allow to easily launch and use a Browser [CustomTabsService]
 */
class BrowserLauncher
@Inject constructor(
    private val application: Application,
    private val packageManager: PackageManager
) {
    private var customTabsClient: CustomTabsClient? = null
    private var customTabsSession: CustomTabsSession? = null
    private var serviceBinded: Boolean = false

    private val customTabsConnection = object : CustomTabsServiceConnection() {
        override fun onCustomTabsServiceConnected(name: ComponentName, client: CustomTabsClient) {
            customTabsClient = client
            customTabsClient?.warmup(0)
            customTabsSession = customTabsClient?.newSession(null)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            customTabsSession = null
            customTabsClient = null
        }
    }

    private val browserPackageNames: List<String>
        get() {
            val activityIntent = Intent(Intent.ACTION_VIEW, "http://".toUri()).apply {
                addCategory(Intent.CATEGORY_BROWSABLE)
            }
            return packageManager.queryIntentActivities(activityIntent, PackageManager.MATCH_ALL)
                .map { it.activityInfo.packageName }
        }

    /**
     * Warms up the [CustomTabsService] application.
     * Must be called first
     *
     * preferredPackageSelector: allows to select preferred service to use.
     */
    fun warmUp(preferredPackageSelector: (List<String>) -> List<String> = { it }) {
        val packageName = CustomTabsClient.getPackageName(application, preferredPackageSelector(browserPackageNames), true)
        if (packageName.isNullOrEmpty()) {
            return
        }
        serviceBinded = CustomTabsClient.bindCustomTabsService(application, packageName, customTabsConnection)
    }

    @JvmOverloads
    fun warmUp(preferredPackageSelector: PreferredPackageSelector? = null) {
        warmUp { preferredPackageSelector?.orderByPreference(it) ?: it }
    }

    /**
     * Shutdown the CustomTabsService
     */
    fun shutdown() {
        if (serviceBinded) {
            application.unbindService(customTabsConnection)
        }
    }

    /**
     * Try to preloads uris.
     *
     * @see [CustomTabsSession.mayLaunchUrl]
     */
    fun mayLaunchUrl(vararg uris: Uri) {
        if (customTabsSession != null) {
            uris.firstOrNull()?.let {
                val otherLikelyBundles = createLikelyBundles(*uris)
                customTabsSession?.mayLaunchUrl(it, null, otherLikelyBundles)
            }
        }
    }

    private fun createLikelyBundles(vararg uris: Uri): List<Bundle> {
        return uris.asSequence()
            .drop(1)
            .map { bundleOf(CustomTabsService.KEY_URL to it) }
            .toList()
    }

    fun launchUrl(context: Context, uri: Uri, customizer: (CustomTabsIntent.Builder.() -> Unit)? = null) {
        if (customTabsSession != null) {
            val builder = CustomTabsIntent.Builder(customTabsSession)
            customizer?.invoke(builder)
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(context, uri)
        } else {
            launchUriInOtherApp(context, uri)
        }
    }

    @JvmOverloads
    fun launchUrl(context: Context, uri: Uri, customizer: LaunchCustomizer? = null) {
        launchUrl(context, uri) { customizer?.customize(this) }
    }

    interface LaunchCustomizer {
        fun customize(builder: CustomTabsIntent.Builder)
    }

    interface PreferredPackageSelector {
        fun orderByPreference(availablePackages: List<String>): List<String>
    }

    private fun launchUriInOtherApp(context: Context, uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    }

}
