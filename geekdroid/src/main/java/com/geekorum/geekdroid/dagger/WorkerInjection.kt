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
@file:JvmName("DaggerWorkerInjection")

package com.geekorum.geekdroid.dagger

import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import dagger.MapKey
import dagger.Module
import dagger.Provides
import dagger.multibindings.ElementsIntoSet
import dagger.multibindings.Multibinds
import kotlin.annotation.AnnotationTarget.FUNCTION
import kotlin.reflect.KClass


@MapKey
@Target(FUNCTION)
annotation class WorkerKey(val value: KClass<out ListenableWorker>)

/**
 * Add this module to your component to support dependency injection of [ListenableWorker].
 *
 * You will get a multibinding Set<WorkerFactory> by adding some WorkerFactory in your module.
```
@Binds
@IntoSet
abstract fun bindMyWorkerFactory(workerFactory: MyWorkerFactory): WorkerFactory<out Worker>
```
 */
@Module
abstract class WorkerInjectionModule private constructor() {

    @Multibinds
    @Deprecated("use Set multibinding")
    abstract fun workerFactoriesMap(): Map<Class<out ListenableWorker>, WorkerFactory>

    @Multibinds
    abstract fun workerFactories(): Set<WorkerFactory>

    @Module
    companion object {
        @Provides
        @ElementsIntoSet
        @JvmStatic
        fun workersFactoriesMapAsSet(workerFactoriesMap: Map<Class<out ListenableWorker>, @JvmSuppressWildcards WorkerFactory>) : Set<WorkerFactory> {
            return workerFactoriesMap.values.toSet()
        }
    }

}
