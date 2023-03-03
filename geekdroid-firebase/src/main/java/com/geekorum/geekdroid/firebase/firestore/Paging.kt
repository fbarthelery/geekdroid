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
package com.geekorum.geekdroid.firebase.firestore

import androidx.collection.SparseArrayCompat
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.*
import timber.log.Timber
import kotlin.coroutines.coroutineContext
import kotlin.reflect.KClass

fun <T: Any> QueryPagingSource(query: Query, type: KClass<T>): QueryPagingSource<T> {
    return QueryPagingSource(query,  documentMapper = {
        it.toObject(type.java)
    })
}

/**
 * You must provide a query with a valid OrderBy clause or a Collection
 */
class QueryPagingSource<T: Any>(
    private val query: Query,
    private val documentMapper: (DocumentSnapshot) -> T?
) : PagingSource<QueryPagingSource.Key, T>() {
    private val sourceScope = CoroutineScope(Job())

    private var lastNextKey: Key? = null
    private var cancelOnCompletion: DisposableHandle? = null

    init {
        registerInvalidatedCallback {
            sourceScope.cancel()
        }
    }

    override suspend fun load(params: LoadParams<Key>): LoadResult<Key, T> {
        if (cancelOnCompletion == null) {
            cancelOnCompletion = coroutineContext[Job]!!.invokeOnCompletion {
                sourceScope.cancel()
            }
        }
        var query = this.query
            .limit(params.loadSize.toLong())

        params.key?.let {
            query = when (params) {
                is LoadParams.Prepend -> {
                    (params.key as? Key.StartAtDocumentKey)?.documentSnapshot?.let {
                        query.startAfter(it)
                    } ?: query
                }
                is LoadParams.Append -> query.startAfter((params.key as Key.StartAtDocumentKey).documentSnapshot)
                is LoadParams.Refresh -> query
            }
        }
        if (params is LoadParams.Refresh) {
            lastNextKey = Key.InitialKey
        }

        // share the query between 2 coroutines
        val dataChannel: SharedFlow<SnapshotsOrError> = query.asDocumentFlow()
            .map {
                @Suppress("USELESS_CAST") // we need it to cast to correct type in catch
                SnapshotsOrError.Snapshots(it) as SnapshotsOrError
            }.catch {
                Timber.e(it, "Error while executing firestore query")
                emit(SnapshotsOrError.Error(it))
            }
            .shareIn(CoroutineScope(coroutineContext + sourceScope.coroutineContext), SharingStarted.Lazily)
        // first one wait for 2 updates and invalidate the source
        dataChannel.take(2)
            .onCompletion { invalidate() }
            .launchIn(CoroutineScope(coroutineContext + sourceScope.coroutineContext))
        // second one make the result
        val data = when (val it = dataChannel.first()) {
            is SnapshotsOrError.Error -> return LoadResult.Error(it.exception)
            is SnapshotsOrError.Snapshots -> it.snapshots
        }
        val objects = data.mapNotNull { documentMapper(it) }
        val prevKey = lastNextKey.takeIf { it !is Key.InitialKey }
        val nextKey = data.lastOrNull()?.let { Key.StartAtDocumentKey(it) }
        lastNextKey = nextKey
        return LoadResult.Page(
            data = objects,
            prevKey = prevKey,
            nextKey = nextKey
        ).also {
            Timber.v("load params $params prevkey ${it.prevKey} nextKey ${it.nextKey}")
        }
    }

    override fun getRefreshKey(state: PagingState<Key, T>): Key? = null

    sealed class Key {
        object InitialKey : Key()
        data class StartAtDocumentKey(val documentSnapshot: DocumentSnapshot) : Key()
    }
}


fun <T: Any> ConcatQueriesPagingSource(queries: List<Query>, type: KClass<T>): ConcatQueriesPagingSource<T> {
    return ConcatQueriesPagingSource(queries,  documentMapper = {
        it.toObject(type.java)
    })
}

/**
 * You must provide queries with a valid OrderBy clause or a Collection
 */
class ConcatQueriesPagingSource<T: Any>(
    private val queries: List<Query>,
    private val documentMapper: (DocumentSnapshot) -> T?
) : PagingSource<ConcatQueriesPagingSource.Key, T>() {
    private val sourceScope =
        CoroutineScope(Job())

    private val queriesNextKeys = SparseArrayCompat<MutableList<QueryKey>>()
    private var cancelOnCompletion: DisposableHandle? = null

    init {
        registerInvalidatedCallback {
            sourceScope.cancel()
        }
    }

    override suspend fun load(params: LoadParams<Key>): LoadResult<Key, T> {
        if (cancelOnCompletion == null) {
            cancelOnCompletion = coroutineContext[Job]!!.invokeOnCompletion {
                sourceScope.cancel()
            }
        }
        val currentQueryIdx = params.key?.queryIdx ?: 0
        if (params is LoadParams.Refresh) {
            queriesNextKeys.putIfAbsent(0, mutableListOf())
            val nextKeys = queriesNextKeys[0]!!
            nextKeys += QueryKey.InitialKey
        }

        val query = getQueryForParam(params)
        // share the query between 2 coroutines
        val dataChannel: SharedFlow<SnapshotsOrError> = query.asDocumentFlow()
            .map {
                @Suppress("USELESS_CAST") // we need it to cast to correct type in catch
                SnapshotsOrError.Snapshots(it) as SnapshotsOrError
            }.catch {
                Timber.e(it, "Error while executing firestore query")
                emit(SnapshotsOrError.Error(it))
            }
            .shareIn(CoroutineScope(coroutineContext + sourceScope.coroutineContext), SharingStarted.Lazily)
        // first one wait for 2 updates and invalidate the source
        dataChannel.take(2)
            .onCompletion { invalidate() }
            .launchIn(CoroutineScope(coroutineContext + sourceScope.coroutineContext))

        // second one make the result
        val data = when (val it = dataChannel.first()) {
            is SnapshotsOrError.Error -> return LoadResult.Error(it.exception)
            is SnapshotsOrError.Snapshots -> it.snapshots
        }
        // TODO filter unique?
        val objects = data.mapNotNull { documentMapper(it) }

        val prevKey = makePrevKey(params.key)
        val nextKey = makeNextKey(currentQueryIdx, data.lastOrNull())
        updateQueryNextKeys( nextKey)
        return LoadResult.Page(
            data = objects,
            prevKey = prevKey,
            nextKey = nextKey,
        ).also {
            Timber.v("load params $params prevkey ${it.prevKey} nextKey ${it.nextKey}")
        }
    }

    private fun getQueryForParam(params: LoadParams<Key>): Query {
        val resultQuery = when (params) {
            is LoadParams.Prepend -> {
                val queryIdx = params.key.queryIdx
                val query = queries[queryIdx]
                (params.key.queryKey as? QueryKey.StartAtDocumentKey)?.documentSnapshot?.let {
                    query.startAfter(it)
                } ?: query
            }
            is LoadParams.Append -> {
                val queryIdx = params.key.queryIdx
                val query = queries[queryIdx]
                (params.key.queryKey as? QueryKey.StartAtDocumentKey)?.documentSnapshot?.let {
                    query.startAfter(it)
                } ?: query
            }
            is LoadParams.Refresh -> {
                val queryIdx = params.key?.queryIdx ?: 0
                queries[queryIdx]
            }
        }
        return resultQuery.limit(params.loadSize.toLong())
    }

    private fun updateQueryNextKeys(nextKey: Key?) {
        if (nextKey != null) {
            queriesNextKeys.putIfAbsent(nextKey.queryIdx, mutableListOf())
            val nextQueryKeys = queriesNextKeys[nextKey.queryIdx]!!
            nextQueryKeys += nextKey.queryKey
            while (nextQueryKeys.size > 2 )
                nextQueryKeys.removeFirst()
        }
    }

    private fun makePrevKey(currentKey: Key?) : Key? {
        val currentQueryIdx = currentKey?.queryIdx ?: 0
        val nextKeys = queriesNextKeys[currentQueryIdx] ?: emptyList()
        val candidate = nextKeys.lastOrNull()

        return when {
            // refresh ? maybe explicitly check for refresh
            currentKey == null -> null
            // last of query but we have at least another one
            candidate == currentKey.queryKey && nextKeys.size >= 2 -> {
                Key(currentQueryIdx, nextKeys[nextKeys.size - 2])
            }
            // last of query get to previous query
            candidate == currentKey.queryKey && currentQueryIdx > 0 -> {
                val previousQueryKey = queriesNextKeys[currentQueryIdx - 1]!!.last()
                Key(currentQueryIdx - 1, previousQueryKey)
            }
            candidate != null -> Key(currentQueryIdx, candidate)
            else -> null
        }
    }

    private fun makeNextKey(currentQueryIdx: Int, documentSnapshot: DocumentSnapshot?) : Key? {
        val nextQueryIdx = currentQueryIdx + 1
        return when {
            documentSnapshot != null -> Key(currentQueryIdx, QueryKey.StartAtDocumentKey(documentSnapshot))
            nextQueryIdx < queries.size -> Key(nextQueryIdx, QueryKey.InitialKey)
            else -> null
        }
    }

    override fun getRefreshKey(state: PagingState<Key, T>): Key? = null

    data class Key(val queryIdx: Int, val queryKey: QueryKey)

    sealed class QueryKey {
        object InitialKey : QueryKey()
        data class StartAtDocumentKey(val documentSnapshot: DocumentSnapshot) : QueryKey()
    }
}


private sealed class SnapshotsOrError {
    data class Snapshots(val snapshots: List<DocumentSnapshot>) : SnapshotsOrError()
    data class Error(val exception: Throwable): SnapshotsOrError()
}


fun  <T : Any> Query.asFlow(type: KClass<T>) : Flow<List<T>> = callbackFlow {
    val registration = addSnapshotListener { snapshot, firestoreException ->
        if (firestoreException != null) {
            throw firestoreException
        }
        snapshot?.toObjects(type.java)?.let { objects ->
            trySendBlocking(objects).onFailure { throw it!! }
        }
    }
    awaitClose { registration.remove() }
}

fun Query.asDocumentFlow() : Flow<List<DocumentSnapshot>> = callbackFlow {
    val registration = addSnapshotListener { snapshot, firestoreException ->
        if (firestoreException != null) {
            close(firestoreException)
        }
        snapshot?.documents?.let {
            trySendBlocking(snapshot.documents).onFailure { throw it!! }
        }
    }
    awaitClose { registration.remove() }
}


inline fun  <reified T : Any> Query.asFlow() : Flow<List<T>> = when (T::class) {
    is DocumentSnapshot -> {
        @Suppress("UNCHECKED_CAST")
        asDocumentFlow() as Flow<List<T>>
    }
    else -> asFlow(T::class)
}


fun  <T : Any> DocumentReference.asObjectFlow(type: KClass<T>) : Flow<T?> = callbackFlow {
    val registration = addSnapshotListener { snapshot, firestoreException ->
        if (firestoreException != null) {
            close(firestoreException)
        }
        snapshot?.let {
            trySendBlocking(snapshot.toObject(type.java)).onFailure { throw it!! }
        }
    }
    awaitClose { registration.remove() }
}

fun  DocumentReference.asDocumentFlow() : Flow<DocumentSnapshot> = callbackFlow {
    val registration = addSnapshotListener { snapshot, firestoreException ->
        if (firestoreException != null) {
            close(firestoreException)
        }
        snapshot?.let {
            trySendBlocking(snapshot).onFailure { throw it!! }
        }
    }
    awaitClose { registration.remove() }
}

inline fun  <reified T : Any> DocumentReference.asFlow() : Flow<T?> = when (T::class) {
    is DocumentSnapshot -> {
        @Suppress("UNCHECKED_CAST")
        asDocumentFlow() as Flow<T?>
    }
    else -> asObjectFlow(T::class)
}

