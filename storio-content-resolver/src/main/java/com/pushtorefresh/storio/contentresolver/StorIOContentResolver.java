package com.pushtorefresh.storio.contentresolver;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.pushtorefresh.storio.contentresolver.operations.delete.PreparedDelete;
import com.pushtorefresh.storio.contentresolver.operations.get.PreparedGet;
import com.pushtorefresh.storio.contentresolver.operations.put.PreparedPut;
import com.pushtorefresh.storio.contentresolver.queries.DeleteQuery;
import com.pushtorefresh.storio.contentresolver.queries.InsertQuery;
import com.pushtorefresh.storio.contentresolver.queries.Query;
import com.pushtorefresh.storio.contentresolver.queries.UpdateQuery;

import java.util.Collections;
import java.util.Set;

import rx.Observable;

/**
 * Powerful abstraction over {@link android.content.ContentResolver}.
 */
public abstract class StorIOContentResolver {

    /**
     * Prepares "Get" Operation for {@link StorIOContentResolver}.
     * Allows you get information from {@link android.content.ContentProvider}.
     *
     * @return builder for {@link PreparedGet}.
     */
    @NonNull
    public PreparedGet.Builder get() {
        return new PreparedGet.Builder(this);
    }

    /**
     * Prepares "Put" Operation for {@link StorIOContentResolver}.
     * Allows you insert or update information in {@link android.content.ContentProvider}.
     *
     * @return builder for {@link PreparedPut}.
     */
    @NonNull
    public PreparedPut.Builder put() {
        return new PreparedPut.Builder(this);
    }

    /**
     * Prepares "Delete" Operation for {@link StorIOContentResolver}.
     * Allows you delete information from {@link android.content.ContentProvider}.
     *
     * @return builder for {@link PreparedDelete}.
     */
    @NonNull
    public PreparedDelete.Builder delete() {
        return new PreparedDelete.Builder(this);
    }

    /**
     * Subscribes to changes of required Uris.
     *
     * @param uris set of {@link Uri} that should be monitored.
     * @return {@link Observable} of {@link Changes} subscribed to changes of required Uris.
     * <p>Notice, that returned {@link Observable} is "Hot Observable", which means,
     * that you should manually unsubscribe from it to prevent memory leak.
     * Also, it can cause BackPressure problems.
     */
    @NonNull
    public abstract Observable<Changes> observeChangesOfUris(@NonNull Set<Uri> uris);

    /**
     * Subscribes to changes of required Uri.
     *
     * @param uri {@link Uri} that should be monitored.
     * @return {@link Observable} of {@link Changes} subscribed to changes of required Uri.
     * <p>Notice, that returned {@link Observable} is "Hot Observable", which means,
     * that you should manually unsubscribe from it to prevent memory leak.
     * Also, it can cause BackPressure problems.
     */
    @NonNull
    public Observable<Changes> observeChangesOfUri(@NonNull Uri uri) {
        return observeChangesOfUris(Collections.singleton(uri));
    }

    /**
     * Hides some internal operations of {@link StorIOContentResolver}
     * to make API of {@link StorIOContentResolver} clean and easy to understand.
     *
     * @return implementation of Internal operations for {@link StorIOContentResolver}.
     */
    @NonNull
    public abstract Internal internal();

    /**
     * Hides some internal operations of {@link StorIOContentResolver}
     * to make {@link StorIOContentResolver} API clean and easy to understand.
     */
    public static abstract class Internal {

        /**
         * Gets {@link ContentResolverTypeMapping} for required type.
         * <p>
         * Result can be {@code null}.
         *
         * @param type type.
         * @param <T>  type.
         * @return {@link ContentResolverTypeMapping} for required type or {@code null}.
         */
        @Nullable
        public abstract <T> ContentResolverTypeMapping<T> typeMapping(@NonNull Class<T> type);

        /**
         * Gets the data from {@link StorIOContentResolver}.
         *
         * @param query query.
         * @return cursor with result data or null.
         */
        @WorkerThread
        @NonNull
        public abstract Cursor query(@NonNull Query query);

        /**
         * Inserts the data to {@link StorIOContentResolver}.
         *
         * @param insertQuery   query.
         * @param contentValues data.
         * @return Uri for inserted data.
         */
        @WorkerThread
        @NonNull
        public abstract Uri insert(@NonNull InsertQuery insertQuery, @NonNull ContentValues contentValues);

        /**
         * Updates data in {@link StorIOContentResolver}.
         *
         * @param updateQuery   query.
         * @param contentValues data.
         * @return number of rows affected.
         */
        @WorkerThread
        public abstract int update(@NonNull UpdateQuery updateQuery, @NonNull ContentValues contentValues);

        /**
         * Deletes the data from {@link StorIOContentResolver}.
         *
         * @param deleteQuery query.
         * @return number of rows deleted.
         */
        @WorkerThread
        public abstract int delete(@NonNull DeleteQuery deleteQuery);
    }
}
