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
package com.geekorum.geekdroid.views.recyclerview;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Helper class to implement a swipe behavior on a RecyclerView.
 */
public abstract class ItemSwiper {
    private final Callback callback;
    private final ItemTouchHelper itemTouchHelper;

    @IntDef(flag = true, value = {
            ItemTouchHelper.START,
            ItemTouchHelper.END,
            ItemTouchHelper.LEFT,
            ItemTouchHelper.RIGHT,
            ItemTouchHelper.UP,
            ItemTouchHelper.DOWN,
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface SwipeDirsFlags {
    }

    public ItemSwiper(@SwipeDirsFlags int swipeDirs) {
        callback = new Callback(swipeDirs);
        itemTouchHelper = new ItemTouchHelper(callback);
    }

    protected void setOnSwipingListener(Callback.OnSwipingItemListener listener) {
        callback.setOnSwipingItemListener(listener);
    }

    public void attachToRecyclerView(RecyclerView recyclerView) {
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    /**
     * Called when a ViewHolder is swiped by the user.
     *
     * @see ItemTouchHelper.Callback#onSwiped(RecyclerView.ViewHolder, int)
     */
    public abstract void onSwiped(RecyclerView.ViewHolder viewHolder, int direction);

    /**
     * @see ItemTouchHelper.Callback#getSwipeThreshold(RecyclerView.ViewHolder)
     */
    public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
        return 0.5f;
    }

    /**
     * Returns false if this viewHolder is not allowed to swipe.
     * Default is true
     */
    protected boolean isViewAllowedToSwipe(@NonNull RecyclerView.ViewHolder viewHolder) {
        return true;
    }

    private class Callback extends SingleItemSwipedCallback {

        Callback(int swipeDirs) {
            super(swipeDirs);
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            ItemSwiper.this.onSwiped(viewHolder, direction);
        }

        @Override
        public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
            return ItemSwiper.this.getSwipeThreshold(viewHolder);
        }

        @Override
        public int getSwipeDirs(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            if (isViewAllowedToSwipe(viewHolder)) {
                return super.getSwipeDirs(recyclerView, viewHolder);
            }
            return 0;
        }
    }
}
