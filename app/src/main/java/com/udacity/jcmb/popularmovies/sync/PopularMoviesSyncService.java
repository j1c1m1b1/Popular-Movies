package com.udacity.jcmb.popularmovies.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * @author Julio Mendoza on 7/22/15.
 */
public class PopularMoviesSyncService extends Service {

    private static final Object sSyncAdapterLock = new Object();
    private static PopularMoviesSyncAdapter syncAdapter = null;

    @Override
    public void onCreate() {
        Log.d(this.getClass().getSimpleName(), "onCreate - Popular Movies Sync Service");
        synchronized (sSyncAdapterLock) {
            if (syncAdapter == null) {
                syncAdapter = new PopularMoviesSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return syncAdapter.getSyncAdapterBinder();
    }
}
