package com.example.charubhargava.tutorial1;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Charu Bhargava on 16-Mar-18.
 */

public class Recording {
    private static final String TAG = "RecordingClass";
    private static final String ID_KEY = "id";
    private static final String TITLE_KEY = "title";
    private static final String CREATOR_ID_KEY = "creatorId";
    private static final String STATION_KEY = "station";
    private static final String START_DATE_KEY = "startDate";
    private static final String END_DATE_KEY = "endDate";
    private static final String RECORDING_URL_KEY = "recordingUrl";
    private static final String PROGRESS_KEY = "progress";
    private static final String CREATED_AT_KEY = "createdAt";
    private static final String UPDATED_AT_KEY = "updatedAt";
    private static final String STATUS_KEY = "status";

    private String id;
    private String title;
    private String creatorId;
    private Station station;
    private long startDate;
    private long endDate;
    private String recordingUrl;
    private long Progress;
    private long createdAt;
    private long updatedAt;
    private String status;

    @Override
    public String toString() {
        return this.title + " (" + this.status + ") ";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Recording recording = (Recording) o;

        return id.equals(recording.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public String getId() {
        return id;
    }

    public Recording(JSONObject jsonObject){
        try {
            this.id = jsonObject.getString(ID_KEY);
            this.title = jsonObject.getString(TITLE_KEY);
            this.creatorId = jsonObject.getString(CREATOR_ID_KEY);
            this.station = new Station(jsonObject.getJSONObject(STATION_KEY));
            this.startDate = jsonObject.getLong(START_DATE_KEY);
            this.endDate = jsonObject.getLong(END_DATE_KEY);
            this.recordingUrl = jsonObject.getString(RECORDING_URL_KEY);
            this.Progress = jsonObject.getLong(PROGRESS_KEY);
            this.createdAt = jsonObject.getLong(CREATED_AT_KEY);
            this.updatedAt = jsonObject.getLong(UPDATED_AT_KEY);
            this.status = jsonObject.getString(STATUS_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public boolean isFinished(){
        return this.status.equals("FINISHED");
    }
}
