package com.pushtorefresh.storio.sqlite.operations.execute;

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.pushtorefresh.storio.operations.PreparedOperation;
import com.pushtorefresh.storio.operations.internal.OnSubscribeExecuteAsBlocking;
import com.pushtorefresh.storio.sqlite.Changes;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.queries.RawQuery;

import java.util.Set;

import rx.Observable;
import rx.schedulers.Schedulers;

import static com.pushtorefresh.storio.internal.Checks.checkNotNull;
import static com.pushtorefresh.storio.internal.Environment.throwExceptionIfRxJavaIsNotAvailable;

/**
 * Prepared Execute SQL Operation for {@link StorIOSQLite}.
 */
public final class PreparedExecuteSQL implements PreparedOperation<Object> {

    @NonNull
    private final StorIOSQLite storIOSQLite;

    @NonNull
    private final RawQuery rawQuery;

    PreparedExecuteSQL(@NonNull StorIOSQLite storIOSQLite, @NonNull RawQuery rawQuery) {
        this.storIOSQLite = storIOSQLite;
        this.rawQuery = rawQuery;
    }

    /**
     * Executes SQL Operation immediately in current thread.
     * <p>
     * Notice: This is blocking I/O operation that should not be executed on the Main Thread,
     * it can cause ANR (Activity Not Responding dialog), block the UI and drop animations frames.
     * So please, call this method on some background thread. See {@link WorkerThread}.
     *
     * @return just a new instance of {@link Object}, actually Execute SQL should return {@code void},
     * but we can not return instance of {@link Void} so we just return {@link Object}
     * and you don't have to deal with {@code null}.
     */
    @WorkerThread
    @NonNull
    @Override
    public Object executeAsBlocking() {
        storIOSQLite.internal().executeSQL(rawQuery);

        final Set<String> affectedTables = rawQuery.affectsTables();

        if (affectedTables.size() > 0) {
            storIOSQLite.internal().notifyAboutChanges(Changes.newInstance(affectedTables));
        }

        return new Object();
    }

    /**
     * Creates {@link Observable} which will perform Execute SQL Operation
     * and send result to observer.
     * <p>
     * Returned {@link Observable} will be "Cold Observable", which means that it performs
     * execution of SQL only after subscribing to it. Also, it emits the result once.
     * <p>
     * <dl>
     * <dt><b>Scheduler:</b></dt>
     * <dd>Operates on {@link Schedulers#io()}.</dd>
     * </dl>
     *
     * @return non-null {@link Observable} which will perform Delete Operation
     * and send result to observer. Result: just a new instance of {@link Object},
     * actually Execute SQL should return {@code void},
     * but we can not return instance of {@link Void} so we just return {@link Object}
     * and you don't have to deal with {@code null}.
     */
    @NonNull
    @Override
    public Observable<Object> createObservable() {
        throwExceptionIfRxJavaIsNotAvailable("createObservable()");

        return Observable
                .create(OnSubscribeExecuteAsBlocking.newInstance(this))
                .subscribeOn(Schedulers.io());
    }

    /**
     * Builder for {@link PreparedExecuteSQL}.
     */
    public static final class Builder {

        @NonNull
        private final StorIOSQLite storIOSQLite;

        public Builder(@NonNull StorIOSQLite storIOSQLite) {
            this.storIOSQLite = storIOSQLite;
        }

        /**
         * Required: Specifies query for ExecSql Operation.
         *
         * @param rawQuery any SQL query that you want to execute, but please, be careful with it.
         *                 Don't forget that you can set affected tables to the {@link RawQuery},
         *                 so ExecSQL operation will send notification about changes in that tables.
         * @return builder.
         */
        @NonNull
        public CompleteBuilder withQuery(@NonNull RawQuery rawQuery) {
            checkNotNull(rawQuery, "Please set query object");
            return new CompleteBuilder(storIOSQLite, rawQuery);
        }
    }

    /**
     * Compile-time safe part of {@link Builder}.
     */
    public static final class CompleteBuilder {

        @NonNull
        private final StorIOSQLite storIOSQLite;

        @NonNull
        private final RawQuery rawQuery;

        CompleteBuilder(@NonNull StorIOSQLite storIOSQLite, @NonNull RawQuery rawQuery) {
            this.storIOSQLite = storIOSQLite;
            this.rawQuery = rawQuery;
        }

        /**
         * Prepares ExecSql Operation.
         *
         * @return {@link PreparedExecuteSQL} instance.
         */
        @NonNull
        public PreparedExecuteSQL prepare() {
            return new PreparedExecuteSQL(
                    storIOSQLite,
                    rawQuery
            );
        }
    }
}
