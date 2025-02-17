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
package com.geekorum.geekdroid.app.lifecycle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

/**
 *  Various LiveData transformations
 */

/**
 * Join two [LiveData] collections into a List
 */
fun <T> LiveData<out Collection<T>>.join(other: LiveData<out Collection<T>>): LiveData<List<T>> {
    return this.combine(other) {first, second -> first + second }
}

/**
 * Union of two [LiveData] collections into a List
 */
fun <T> LiveData<out Collection<T>>.union(other: LiveData<out Collection<T>>): LiveData<List<T>> {
    return this.combine(other) { first, second -> first.union(second).toList() }
}

private fun <T> LiveData<out Collection<T>>.combine(
    other: LiveData<out Collection<T>>,
    combinator: (first: Collection<T>, second: Collection<T>) -> List<T>
): LiveData<List<T>> {
    return MediatorLiveData<List<T>>().apply {
        var first: Collection<T> = emptyList()
        var second: Collection<T> = emptyList()

        addSource(this@combine) {
            first = it
            value = combinator(first, second)
        }
        addSource(other) {
            second = it
            value = combinator(first, second)
        }
    }
}

/**
 * Make this LiveData delivers new value only on new event
 */
fun <T> LiveData<T>.withRefreshEvent(event: LiveData<out Event<*>>): LiveData<T> = RefreshOnEventLiveData(this, event)


/**
 * A LiveData that delivers source new value only when event is received.
 */
private class RefreshOnEventLiveData<T>(
    source: LiveData<T>,
    event: LiveData<out Event<*>>
) : MediatorLiveData<T>() {
    private var hasDeliveredFirst = false
    private var lastData: T? = null

    init {
        addSource(source) {
            lastData = it
            if (!hasDeliveredFirst) {
                value = it
                hasDeliveredFirst = true
            }
        }
        addSource(event, EventObserver {
            value = lastData
        })
    }
}
