package com.pushtorefresh.storio.sqlite.operations.put;

import org.junit.Test;

import rx.Observable;

public class PreparedPutContentValuesTest {

    @Test
    public void putContentValuesBlocking() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForOneContentValues();

        final PutResult putResult = putStub.storIOSQLite
                .put()
                .contentValues(putStub.contentValues.get(0))
                .withPutResolver(putStub.putResolver)
                .prepare()
                .executeAsBlocking();

        putStub.verifyBehaviorForOneContentValues(putResult);
    }

    @Test
    public void putContentValuesObservable() {
        final PutContentValuesStub putStub = PutContentValuesStub.newPutStubForOneContentValues();

        final Observable<PutResult> putResultObservable = putStub.storIOSQLite
                .put()
                .contentValues(putStub.contentValues.get(0))
                .withPutResolver(putStub.putResolver)
                .prepare()
                .createObservable();

        putStub.verifyBehaviorForOneContentValues(putResultObservable);
    }
}
