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
package com.geekorum.geekdroid.gms

import com.google.android.gms.tasks.Task
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import com.google.android.play.core.tasks.Task as PlayCoreTask

/**
 * Await for the result of a [Task]
 */
@Deprecated("Use kotlinx-coroutines-play-services")
suspend fun <T> Task<T>.await(): T {
    try {
        if (isComplete) return result as T
    } catch (e: RuntimeException) {
        return suspendCancellableCoroutine {
            it.resumeWithException(e.cause ?: e)
        }
    }

    return suspendCancellableCoroutine { cont ->
        addOnCompleteListener { task ->
            if (task.isSuccessful) {
                cont.resume(task.result!!)
            } else {
                cont.resumeWithException(task.exception!!)
            }
        }
    }
}

/**
 * Await for the nullable result of a [Task]
 * As [Task] is a Java API without proper nullability annotations,
 * use this method if you know the task can returns null
 */
@Deprecated("Use kotlinx-coroutines-play-services")
suspend fun <T> Task<T>.awaitNullable(): T? {
    try {
        if (isComplete) return result
    } catch (e: RuntimeException) {
        return suspendCancellableCoroutine {
            it.resumeWithException(e.cause ?: e)
        }
    }

    return suspendCancellableCoroutine { cont ->
        addOnCompleteListener { task ->
            if (task.isSuccessful) {
                cont.resume(task.result)
            } else {
                cont.resumeWithException(task.exception!!)
            }
        }
    }
}


/**
 * Converts this task to an instance of [Deferred].
 */
fun <T> PlayCoreTask<T>.asDeferred(): Deferred<T> {
    if (isComplete) {
        val e = exception
        return if (e == null) {
            @Suppress("UNCHECKED_CAST")
            CompletableDeferred<T>().apply { complete(result as T) }
        } else {
            CompletableDeferred<T>().apply { completeExceptionally(e) }
        }
    }

    val result = CompletableDeferred<T>()
    addOnCompleteListener {
        val e = it.exception
        if (e == null) {
            @Suppress("UNCHECKED_CAST")
            result.complete(it.result as T)
        } else {
            result.completeExceptionally(e)
        }
    }
    return result
}

/**
 * Awaits for completion of the task without blocking a thread.
 *
 * If the [Job] of the current coroutine is cancelled or completed while this suspending function is waiting, this function
 * stops waiting for the completion stage and immediately resumes with [CancellationException].
 */
suspend fun <T> PlayCoreTask<T>.await(): T {
    // fast path
    if (isComplete) {
        val e = exception
        return if (e == null) {
            @Suppress("UNCHECKED_CAST")
            result as T
        } else {
            throw e
        }
    }

    return suspendCancellableCoroutine { cont ->
        addOnCompleteListener {
            val e = exception
            if (e == null) {
                @Suppress("UNCHECKED_CAST")
                cont.resume(result as T)
            } else {
                cont.resumeWithException(e)
            }
        }
    }
}

