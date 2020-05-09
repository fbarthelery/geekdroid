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
package com.geekorum.geekdroid.dagger

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import dagger.MapKey
import dagger.Module
import dagger.multibindings.Multibinds
import javax.inject.Inject
import javax.inject.Provider
import kotlin.annotation.AnnotationTarget.FUNCTION
import kotlin.reflect.KClass


/**
 * MultiBinding key for the [DaggerDelegateViewModelsFactory]
 */
@MapKey
@MustBeDocumented
@Target(FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ViewModelKey(
    val value: KClass<out ViewModel>
)

/**
 * Add this module to your component to be able to obtain a [DaggerDelegateViewModelsFactory] or a [DaggerDelegateSavedStateVMFactory]
 * that can create [ViewModel]s using dagger dependency injection.
 *
 * You can provide [ViewModel]s by binding a [ViewModel] or a [ViewModelAssistedFactory] with a [ViewModelKey].
 *
 * ```
 *  @Binds
 *  @IntoMap
 *  @ViewModelKey(MyViewModel::class)
 *  abstract fun bindMyViewModel(viewModel: MyViewModel): ViewModel
 *
 *  @Binds
 *  @IntoMap
 *  @ViewModelKey(MySavedStateViewModel::class)
 *  abstract fun bindMySavedStateViewModel(viewModel: MySavedStateViewModel.Factory): ViewModelAssistedFactory<out ViewModel>
 * ```
 * @see [DaggerDelegateSavedStateVMFactory]
 */
@Module(includes = [GeekdroidAssistedModule::class])
abstract class ViewModelsModule private constructor() {

    @Multibinds
    abstract fun viewModelsFactories(): Map<Class<out ViewModel>, ViewModel>

    @Multibinds
    abstract fun savedStateViewModelsFactories(): Map<Class<out ViewModel>, ViewModelAssistedFactory<out ViewModel>>
}


/**
 * Factory that can creates the [ViewModel] needed by application after injecting them.
 */
class DaggerDelegateViewModelsFactory @Inject constructor(
    private val providers: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        providers[modelClass]?.let {
            @Suppress("UNCHECKED_CAST")
            return it.get() as T
        }
        throw IllegalArgumentException("No ViewModel providers for class $modelClass")
    }
}

/**
 * Factory that can creates [ViewModel] who contribute to saved states via [SavedStateHandle]
 * and support assisted injection
 *
 * ViewModels must have an annotated [AssistedInject.Factory] factory interface inheriting [ViewModelAssistedFactory]
 * and an annotated [Assisted] state [SavedStateHandle] constructor parameter.
 *
 * ```
 * class MyViewModel @AssistedInject constructor(@Assisted private val state: SavedStateHandle,
 *                                               val otherDep: OtherDependency
 * ) : ViewModel() {
 *
 *      @AssistedInject.Factory
 *      interface Factory : ViewModelAssistedFactory<MyViewModel> {
 *          override fun create(state: SavedStateHandle): MyViewModel
 *      }
 * }
 * ```
 *
 * This class also support simple ViewModel injection like [DaggerDelegateViewModelsFactory]
 * TODO: documentation is highly AssistedInject focused. Maybe it can be used with other assisted injection framework
 */
class DaggerDelegateSavedStateVMFactory @AssistedInject constructor(
    private val simpleProviders: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>,
    private val savedStateFactoryProviders: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModelAssistedFactory<out ViewModel>>>,
    @Assisted val owner: SavedStateRegistryOwner,
    @Assisted val defaultArgs: Bundle?
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

    override fun <T : ViewModel> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
        // first try the SavedState version
        savedStateFactoryProviders[modelClass]?.let {
            @Suppress("UNCHECKED_CAST")
            return it.get().create(handle) as T
        }
        simpleProviders[modelClass]?.let {
            @Suppress("UNCHECKED_CAST")
            return it.get() as T
        }
        throw IllegalArgumentException("No ViewModel providers for key $key and class $modelClass")
    }

    @AssistedInject.Factory
    interface Creator {
        fun create(owner: SavedStateRegistryOwner, defaultArgs: Bundle? = null): DaggerDelegateSavedStateVMFactory
    }
}

/**
 * A Factory that can create ViewModel T with a SavedStateHandle
 */
interface ViewModelAssistedFactory<T: ViewModel> {
    fun create(state: SavedStateHandle) : T
}
