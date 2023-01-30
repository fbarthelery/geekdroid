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
package com.geekorum.geekdroid.network;

import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

import javax.inject.Inject;

/**
 * A {@link WebViewClient} that intercepts background request to execute them with OkHttp.
 */
public class OkHttpWebViewClient extends WebViewClient {

    private final OkHttpClient okHttpClient;
    private static final String TAG = OkHttpWebViewClient.class.getSimpleName();

    @Inject
    public OkHttpWebViewClient(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        if (shouldInterceptRequest(request)) {
            Request req = new Request.Builder().get()
                    .url(request.getUrl().toString())
                    .headers(Headers.of(request.getRequestHeaders()))
                    .build();
            Call call = okHttpClient.newCall(req);
            try {
                Response response = call.execute();
                return new WebResourceResponse(response.header("Content-Type"), response.header("Content-Encoding"),
                        response.body().byteStream());
            } catch (IOException e) {
                Log.d(TAG, "error while intercepting request " + request.getUrl(), e);
            }
        }
        return null;
    }

    protected boolean shouldInterceptRequest(WebResourceRequest request) {
        return request.getMethod().equals("GET") && !request.isForMainFrame();
    }
}
