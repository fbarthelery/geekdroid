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

import android.net.TrafficStats
import java.net.InetAddress
import java.net.Socket
import javax.net.SocketFactory

/**
 * A [SocketFactory] who tag its socket for [TrafficStats] analysis
 */
class TaggedSocketFactory(
    private val delegate: SocketFactory,
    private val tag: Int
) : SocketFactory() {
    override fun createSocket(host: String?, port: Int): Socket {
        return delegate.createSocket(host, port).also {
            configureSocket(it)
        }
    }


    override fun createSocket(host: String?, port: Int, localHost: InetAddress?, localPort: Int): Socket {
        return delegate.createSocket(host, port, localHost, localPort).also {
            configureSocket(it)
        }
    }

    override fun createSocket(host: InetAddress?, port: Int): Socket {
        return delegate.createSocket(host, port).also {
            configureSocket(it)
        }
    }

    override fun createSocket(address: InetAddress?, port: Int, localAddress: InetAddress?, localPort: Int): Socket {
        return delegate.createSocket(address, port, localAddress, localPort).also {
            configureSocket(it)
        }
    }

    override fun createSocket(): Socket {
        return delegate.createSocket().also {
            configureSocket(it)
        }
    }

    private fun configureSocket(socket: Socket) {
        TrafficStats.setThreadStatsTag(tag)
    }
}
