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
package com.geekorum.geekdroid.dagger

import android.app.Application
import dagger.Module
import dagger.multibindings.Multibinds

/**
 * Add this module to your component to support dependency injection of [AppInitializer].
 *
 * You can provide [AppInitializer] by adding some bindings in your module.
```
@Binds
@IntoSet
abstract fun bindMyAppInitializer(myAppInitializer: MyAppInitializer): AppInitializer
```
 */
@Module
abstract class AppInitializersModule private constructor() {

    @Multibinds
    abstract fun appInitializers(): Set<AppInitializer>

}


/**
 * AppInitializers are meant to be run in [Application.onCreate]
 */
interface AppInitializer {
    fun initialize(app: Application)
}

fun Collection<AppInitializer>.initialize(app: Application) {
    forEach { it.initialize(app) }
}
