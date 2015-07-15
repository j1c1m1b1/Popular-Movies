package com.udacity.jcmb.popularmovies.prefs;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * @author Julio Mendoza on 7/15/15.
 */
@SharedPref(SharedPref.Scope.UNIQUE)
public interface MyPrefs {

    @DefaultBoolean(false)
    boolean fromPopular();

    @DefaultBoolean(false)
    boolean fromRanking();

    @DefaultBoolean(false)
    boolean fromFavorites();

}
