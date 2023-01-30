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
package com.geekorum.geekdroid.views;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import androidx.core.view.MotionEventCompat;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

/**
 * A simple gesture detector that detect when the user stay in long click with 3 touches.
 */
public class MultipleLongClickGestureDetector {

    private static final int TRIPLE_LONG_PRESS = 0;
    private static final int LONGPRESS_TIMEOUT = ViewConfiguration.getLongPressTimeout();
    private static final int TAP_TIMEOUT = ViewConfiguration.getTapTimeout();

    private final Handler handler;
    private final OnGestureListener listener;
    private final int nbPointersTrigger;
    private int currentPointers;
    private MotionEvent multiplePointerEvent;

    private class GestureHandler extends Handler {
        GestureHandler() {
            super();
        }

        GestureHandler(Handler handler) {
            super(handler.getLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TRIPLE_LONG_PRESS:
                    dispatchTripleLongPress();
                    break;

                default:
                    throw new RuntimeException("Unknown message " + msg); //never
            }
        }
    }

    /**
     * Creates a {@link MultipleLongClickGestureDetector} with the supplied listener.
     * You may only use this constructor from a UI thread (this is the usual situation).
     *
     * @param context  the application's context
     * @param listener the listener invoked for all the callbacks, this must
     *                 not be null.
     * @param nbPointers the number of down pointers before sending a multiple long click event
     *
     * @throws NullPointerException if {@code listener} is null.
     * @see Handler#Handler()
     */
    public MultipleLongClickGestureDetector(Context context, OnGestureListener listener, int nbPointers) {
        this(context, listener, nbPointers, null);
    }


    /**
     * Creates a {@link MultipleLongClickGestureDetector} with the supplied listener.
     * You may only use this constructor from a UI thread (this is the usual situation).
     *
     * @param context  the application's context
     * @param listener the listener invoked for all the callbacks, this must
     *                 not be null.
     * @param handler  the handler to use
     *
     * @throws NullPointerException if {@code listener} is null.
     * @see Handler#Handler()
     */
    public MultipleLongClickGestureDetector(Context context, OnGestureListener listener, int nbPointers,
                                            Handler handler) {
        if (handler != null) {
            this.handler = new GestureHandler(handler);
        } else {
            this.handler = new GestureHandler();
        }
        this.listener = listener;
        this.nbPointersTrigger = nbPointers;
        init(context);
    }

    private void init(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null");
        }
        if (listener == null) {
            throw new IllegalArgumentException("OnGestureListener must not be null");
        }
    }

    /**
     * Analyzes the given motion event and if applicable triggers the
     * appropriate callbacks on the {@link android.view.GestureDetector.OnGestureListener} supplied.
     *
     * @param ev The current motion event.
     *
     * @return true if the {@link android.view.GestureDetector.OnGestureListener} consumed the event,
     * else false.
     */
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();

        boolean handled = false;
        switch (action & MotionEventCompat.ACTION_MASK) {
            case MotionEventCompat.ACTION_POINTER_DOWN:
                currentPointers++;
                if (currentPointers == nbPointersTrigger) {
                    multiplePointerEvent = ev;
                }
                if (currentPointers >= nbPointersTrigger) {
                    cancel();
                    handler.sendEmptyMessageDelayed(TRIPLE_LONG_PRESS, TAP_TIMEOUT + LONGPRESS_TIMEOUT);
                }
                break;

            case MotionEventCompat.ACTION_POINTER_UP:
                currentPointers--;
                if (currentPointers < nbPointersTrigger) {
                    cancel();
                }
                break;

            case MotionEvent.ACTION_DOWN:
                currentPointers = 1;
                handled = true;
                break;

            case MotionEvent.ACTION_UP:
                currentPointers--;
                cancel();
                break;

            case MotionEvent.ACTION_CANCEL:
                cancel();
                break;
            default:
                // do nothing we don't need the other events
                break;
        }

        return handled;
    }

    private void cancel() {
        handler.removeMessages(TRIPLE_LONG_PRESS);
    }

    private void dispatchTripleLongPress() {
        listener.onMultipleLongPressed(multiplePointerEvent);
    }

    public interface OnGestureListener {
        void onMultipleLongPressed(MotionEvent event);
    }
}
