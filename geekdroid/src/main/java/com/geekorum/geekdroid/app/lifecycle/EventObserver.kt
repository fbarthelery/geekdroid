/*
 * Geekdroid is a utility library for development on the Android
 * Platform.
 *
 * Copyright (C) 2017-2023 by Frederic-Charles Barthelery.
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

import androidx.lifecycle.Observer

/**
 * An Event of content T
 */
open class Event<out T>(
    private val content: T) {

    var hasBeenHandled = false
        private set //external read only

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    fun peekContent(): T = content
}

/**
 * An [Event] with no content.
 */
class EmptyEvent : Event<Any>(Any()) {
    companion object {
        @JvmStatic
        fun makeEmptyEvent() = EmptyEvent()
    }
}

/**
 * An event observer observe a LiveData<Event> an will be executed only once, when the event changed
 */
class EventObserver<T>(
    private val onEventUnhandled: (T) -> Unit
) : Observer<Event<T>> {

    override fun onChanged(event: Event<T>) {
        event.getContentIfNotHandled()?.let(onEventUnhandled)
    }
}
