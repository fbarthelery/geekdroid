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
package com.geekorum.geekdroid.views.recyclerview;

import android.graphics.Rect;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

/**
 * {@link RecyclerView.ItemDecoration} that applies a
 * vertical and horizontal spacing between items of the target
 * {@link RecyclerView}.
 */
public class SpacingItemDecoration extends RecyclerView.ItemDecoration {
    private final int verticalSpacing;
    private final int horizontalSpacing;

    public SpacingItemDecoration(int verticalSpacing, int horizontalSpacing) {
        this.verticalSpacing = verticalSpacing;
        this.horizontalSpacing = horizontalSpacing;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.bottom = verticalSpacing / 2;
        outRect.top = verticalSpacing / 2;
        outRect.left = horizontalSpacing / 2;
        outRect.right = horizontalSpacing / 2;
    }

    public int getVerticalSpacing() {
        return verticalSpacing;
    }

    public int getHorizontalSpacing() {
        return horizontalSpacing;
    }
}
