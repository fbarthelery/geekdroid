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
package com.geekorum.geekdroid.loaders;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import androidx.loader.content.CursorLoader;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * A {@link CursorLoader} that can load data from a cursor and map it into an Object instance.
 */
public class ObjectCursorLoader<T> extends CursorLoader {

    private final CursorMapper<T> cursorMapper;
    private List<T> items = Collections.emptyList();

    public ObjectCursorLoader(Context context, CursorMapper<T> cursorMapper) {
        super(context);
        this.cursorMapper = cursorMapper;
    }

    public ObjectCursorLoader(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, CursorMapper<T> cursorMapper) {
        super(context, uri, projection, selection, selectionArgs, sortOrder);
        this.cursorMapper = cursorMapper;
    }

    @Override
    public Cursor loadInBackground() {
        Cursor result;
        result = super.loadInBackground();
        loadItems(result);
        return result;
    }

    private void loadItems(Cursor cursor) {
        List<T> newItems = new LinkedList<>();
        while (cursor.moveToNext()) {
            newItems.add(cursorMapper.map(cursor));
        }
        this.items = newItems;
    }

    /**
     * Get the loaded items
     * @return the items
     */
    public List<T> getItems() {
        return items;
    }

    public interface CursorMapper<T> {
        T map(Cursor cursor);
    }

}
