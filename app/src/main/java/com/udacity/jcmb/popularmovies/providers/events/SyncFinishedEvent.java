package com.udacity.jcmb.popularmovies.providers.events;

import org.json.JSONObject;

/**
 * @author Julio Mendoza on 7/22/15.
 */
public class SyncFinishedEvent {

    private boolean successful;

    private JSONObject response;

    public SyncFinishedEvent(boolean successful, JSONObject response) {
        this.successful = successful;
        this.response = response;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public JSONObject getResponse() {
        return response;
    }
}
