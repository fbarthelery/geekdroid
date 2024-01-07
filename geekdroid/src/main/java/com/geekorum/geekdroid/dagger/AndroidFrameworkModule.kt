/*
 * Geekdroid is a utility library for development on the Android
 * Platform.
 *
 * Copyright (C) 2017-2024 by Frederic-Charles Barthelery.
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
package com.geekorum.geekdroid.dagger

import android.accounts.AccountManager
import android.app.Application
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.PowerManager
import android.security.NetworkSecurityPolicy
import androidx.core.content.getSystemService
import dagger.Module
import dagger.Provides
import javax.inject.Scope
import javax.inject.Singleton

/**
 * Provides binding for some Android Framework services tied to the application context.
 */
@Module
class AndroidFrameworkModule {

    @Provides
    @Singleton
    fun providesNotificationManager(application: Application): NotificationManager {
        return application.getSystemService()!!
    }

    @Provides
    @Singleton
    fun providesConnectivityManager(application: Application): ConnectivityManager {
        return application.getSystemService()!!
    }

    @Provides
    @Singleton
    fun providesPackageManager(application: Application): PackageManager {
        return application.packageManager
    }

    @Provides
    @Singleton
    fun providesAccountManager(application: Application): AccountManager {
        return AccountManager.get(application)
    }

    @Provides
    @Singleton
    fun providesContentResolver(application: Application): ContentResolver {
        return application.contentResolver
    }

    @Provides
    @Singleton
    fun providesPowerManager(application: Application): PowerManager {
        return application.getSystemService()!!
    }

    @Provides
    @Singleton
    fun providesNetSecurityPolicy(): NetworkSecurityPolicy = NetworkSecurityPolicy.getInstance()
}


/**
 * Scope for object which should be instantiated once per Android component (Activity, Service,
 * BroadcastReceiver, ContentProvider)
 */
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Scope
annotation class PerAndroidComponent
