package com.udacity.jcmb.popularmovies.interfaces;

import org.json.JSONObject;

/**
 * @author Julio Mendoza on 7/9/15.
 */
public interface ConnectionEventsListener {

    void onSuccess(JSONObject response);

    void onFail();
}
