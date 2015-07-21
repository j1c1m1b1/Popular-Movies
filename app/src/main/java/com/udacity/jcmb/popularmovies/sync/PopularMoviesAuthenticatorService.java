package com.udacity.jcmb.popularmovies.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * @author Julio Mendoza on 7/21/15.
 */
public class PopularMoviesAuthenticatorService extends Service {

    private PopularMoviesAuthenticator authenticator;

    @Override
    public void onCreate() {
        super.onCreate();
        authenticator = new PopularMoviesAuthenticator(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return authenticator.getIBinder();
    }
}
