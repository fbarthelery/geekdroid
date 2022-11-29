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
package com.geekorum.geekdroid.network

import android.net.Uri
import com.squareup.picasso.Downloader
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import okhttp3.*
import java.io.IOException

/**
 * Quick and dirty implementation of Picasso [Downloader] which uses OkHttp3.
 * This will be enough until Picasso-3.0.0 is out.
 * TODO use OkHttp3Downloader from Picasso-3.0.0
 */
class PicassoOkHttp3Downloader(
    private val client: OkHttpClient
) : Downloader {

    private val cache: Cache? = client.cache
    private val sharedClient = true

    override fun shutdown() {
        if (!sharedClient) {
            cache.use {
                // simple way to close silently
            }
        }
    }

    override fun load(uri: Uri, networkPolicy: Int): Downloader.Response {
        val request = createRequest(uri, networkPolicy)
        val response = client.newCall(request).execute()
        return createResult(response, networkPolicy)
    }

    private fun createRequest(uri: Uri, networkPolicy: Int): Request {
        val cacheControl = buildCacheControl(networkPolicy)
        return Request.Builder()
            .url(uri.toString())
            .apply {
                if (cacheControl != null) {
                    cacheControl(cacheControl)
                }
            }.build()
    }

    private fun buildCacheControl(networkPolicy: Int): CacheControl? {
        return when {
            networkPolicy == 0 -> null
            NetworkPolicy.isOfflineOnly(networkPolicy) -> CacheControl.FORCE_CACHE
            else -> CacheControl.Builder().apply {
                if (NetworkPolicy.shouldReadFromDiskCache(networkPolicy)) {
                    noCache()
                }
                if (NetworkPolicy.shouldWriteToDiskCache(networkPolicy)) {
                    noStore()
                }
            }.build()
        }
    }

    private fun createResult(response: Response, networkPolicy: Int): Downloader.Response {
        val body = response.body!!
        val loadedFrom = if (response.cacheResponse == null) Picasso.LoadedFrom.NETWORK else Picasso.LoadedFrom.DISK
        return when {
            !response.isSuccessful -> {
                body.close()
                throw Downloader.ResponseException("Failed request", networkPolicy, response.code)
            }
            // Sometimes response content length is zero when requests are being replayed. Haven't found
            // root cause to this but retrying the request seems safe to do so.
            loadedFrom == Picasso.LoadedFrom.DISK && body.contentLength() == 0L -> {
                body.close()
                throw IOException("Received response with 0 content-length header")
            }
            else -> Downloader.Response(body.byteStream(), loadedFrom != Picasso.LoadedFrom.NETWORK,
                body.contentLength())
        }
    }

}
