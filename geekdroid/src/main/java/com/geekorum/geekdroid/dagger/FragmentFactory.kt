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

import androidx.fragment.app.Fragment
import dagger.MapKey
import dagger.Module
import dagger.multibindings.Multibinds
import javax.inject.Inject
import javax.inject.Provider
import kotlin.reflect.KClass

/**
 * MultiBinding key for the [DaggerDelegateFragmentFactory]
 */
@MapKey
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class FragmentKey(
    val value: KClass<out Fragment>
)

/**
 * Add this module to your component to be able to obtain a [DaggerDelegateFragmentFactory] that can create
 * Fragment using dagger dependency injection.
 *
 * You can provide [Fragment] by adding some bindings in your module.
 * The [DaggerDelegateFragmentFactory] will use the declared bindings.
```
@Binds
@IntoMap
@FragmentKey(MyFragment::class)
abstract fun bindMyFragment(fragment: MyFragment): Fragment
```
 */
@Module
abstract class FragmentFactoriesModule private constructor() {

    @Multibinds
    abstract fun fragmentFactories(): Map<Class<out Fragment>, Fragment>
}

/**
 * Factory that can creates the [Fragment] needed by application after injecting them.
 */
class DaggerDelegateFragmentFactory @Inject constructor(
    private val providers: Map<Class<out Fragment>, @JvmSuppressWildcards Provider<Fragment>>
) : androidx.fragment.app.FragmentFactory() {


    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        val key = loadFragmentClass(classLoader, className)
        return providers[key]?.get() ?: super.instantiate(classLoader, className)
    }

}

