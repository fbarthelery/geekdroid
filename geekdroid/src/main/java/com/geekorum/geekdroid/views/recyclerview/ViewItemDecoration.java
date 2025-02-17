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
package com.geekorum.geekdroid.views.recyclerview;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A {@link RecyclerView.ItemDecoration} that draws {@link View} for decoration.
 */
public class ViewItemDecoration extends RecyclerView.ItemDecoration {

    private final Rect bounds = new Rect();
    private FrameLayout root;
    private ViewPainter drawPainter;
    private ViewPainter drawOverPainter;

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (drawPainter == null) {
            return;
        }
        drawPainter.draw(c, parent, state);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (drawOverPainter == null) {
            return;
        }
        drawOverPainter.draw(c, parent, state);
    }

    protected void setDrawOverPainter(ViewPainter drawOverPainter) {
        this.drawOverPainter = drawOverPainter;
    }

    protected void setDrawPainter(ViewPainter viewPainter) {
        this.drawPainter = viewPainter;
    }

    /**
     * The artist that paint the view as a decoration.
     */
    protected abstract class ViewPainter {

        private void prepareRoot(RecyclerView parent) {
            if (root == null) {
                root = new FrameLayout(parent.getContext());
            }
            bounds.setEmpty();
            root.removeAllViews();
        }

        private void layoutRoot() {
            int widthSpec = View.MeasureSpec.makeMeasureSpec(bounds.width(), View.MeasureSpec.EXACTLY);
            int heightSpec = View.MeasureSpec.makeMeasureSpec(bounds.height(), View.MeasureSpec.EXACTLY);
            if (!root.canResolveLayoutDirection()) {
                // as this view is never attached to a window set it layout direction explicitly
                root.setLayoutDirection(View.LAYOUT_DIRECTION_LOCALE);
            }
            root.measure(widthSpec, heightSpec);
            root.layout(bounds.left, bounds.top, bounds.right, bounds.bottom);
        }

        private void draw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            c.save();
            View view = drawPainter.getView(parent, state);
            if (view != null) {
                prepareRoot(parent);
                root.addView(view, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                computeDrawingBounds(parent, state, bounds);
                layoutRoot();
                c.translate(bounds.left, bounds.top);
                root.draw(c);
            }
            c.restore();
        }

        /**
         * Compute the bounds of the drawing area.
         * @param parent RecyclerView this ItemDecoration is drawing into
         * @param state state of parent
         * @param outBounds output bounds
         */
        protected abstract void computeDrawingBounds(RecyclerView parent, RecyclerView.State state, Rect outBounds);

        /**
         * Get the view to draw as a decoration.
         * @param parent RecyclerView this ItemDecoration is drawing into
         * @param state state of parent
         * @return the view
         */
        protected abstract View getView(RecyclerView parent, RecyclerView.State state);

    }
}
