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
package com.geekorum.geekdroid.firebase.firestore

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await

/* suspend version of get(), set(), update(), delete() */
suspend fun DocumentReference.aSet(pojo: Any): Void = set(pojo).await()
suspend fun DocumentReference.aUpdate(data: Map<String, Any>): Void = update(data).await()
suspend fun DocumentReference.aDelete(): Void = delete().await()
suspend fun DocumentReference.aGet(): DocumentSnapshot = get().await()
suspend fun CollectionReference.aAdd(pojo: Any): DocumentReference = add(pojo).await()


suspend inline fun <reified T> DocumentReference.toObject(): T? {
    return get().await().toObject()
}
