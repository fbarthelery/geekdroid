/*
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
package com.geekorum.geekdroid.firebase

import androidx.lifecycle.LiveData
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.tasks.await
import timber.log.Timber


class FirestoreQueryLiveData<T> constructor(
    private val query: Query,
    private val clazz: Class<T>
) : LiveData<List<T>>() {

    private val TAG = FirestoreQueryLiveData::class.java.simpleName
    private var listenerRegistration: ListenerRegistration? = null


    override fun onActive() {
        listenerRegistration = query.addSnapshotListener { snapshot, firestoreException ->
            if (firestoreException != null) {
                Timber.e(firestoreException, "Error when listening to firestore")
            }
            value = snapshot?.toObjects(clazz) ?: emptyList()
        }

    }

    override fun onInactive() {
        super.onInactive()
        listenerRegistration?.remove()
    }
}

inline fun <reified T> Query.toLiveData() : LiveData<List<T>> =
    FirestoreQueryLiveData(this)

inline fun <reified T> FirestoreQueryLiveData(query: Query): FirestoreQueryLiveData<T> {
    return FirestoreQueryLiveData(query, T::class.java)
}

class FirestoreDocumentLiveData<T>(
    private val document: DocumentReference,
    private val clazz: Class<T>
) : LiveData<T>() {

    private val TAG = FirestoreDocumentLiveData::class.java.simpleName
    private var listenerRegistration: ListenerRegistration? = null


    override fun onActive() {
        listenerRegistration = document.addSnapshotListener { snapshot, firestoreException ->
            if (firestoreException != null) {
                Timber.e(firestoreException, "Error when listening to firestore")
            }
            value = snapshot?.toObject(clazz)
        }
    }

    override fun onInactive() {
        super.onInactive()
        listenerRegistration?.remove()
    }
}

inline fun <reified T> DocumentReference.toLiveData(): LiveData<T?> =
    FirestoreDocumentLiveData(this)

inline fun <reified T> FirestoreDocumentLiveData(document: DocumentReference): FirestoreDocumentLiveData<T> {
    return FirestoreDocumentLiveData(document, T::class.java)
}

@Deprecated("Use firebase-firestore-ktx", ReplaceWith("toObject()", imports = ["com.google.firebase.firestore.ktx.toObject"]))
inline fun <reified T> DocumentSnapshot.toObject(): T? = toObject()

@Deprecated("Use firebase-firestore-ktx", ReplaceWith("toObjects()", imports = ["com.google.firebase.firestore.ktx.toObjects"]))
inline fun <reified T: Any> QuerySnapshot.toObjects(): List<T> = toObjects()

/* suspend version of get(), set(), update(), delete() */
suspend fun DocumentReference.aSet(pojo: Any): Void = set(pojo).await()
suspend fun DocumentReference.aUpdate(data: Map<String, Any>): Void = update(data).await()
suspend fun DocumentReference.aDelete(): Void = delete().await()
suspend fun DocumentReference.aGet(): DocumentSnapshot = get().await()
suspend fun CollectionReference.aAdd(pojo: Any): DocumentReference = add(pojo).await()


suspend inline fun <reified T> DocumentReference.toObject(): T? {
    return get().await().toObject()
}
