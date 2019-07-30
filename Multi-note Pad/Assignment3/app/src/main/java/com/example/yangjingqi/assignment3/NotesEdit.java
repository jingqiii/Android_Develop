package com.example.yangjingqi.assignment3;

import android.annotation.TargetApi;
import android.icu.text.SimpleDateFormat;
import android.os.Build;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by yangjingqi on 7/18/17.
 */

@TargetApi(Build.VERSION_CODES.N)
public class NotesEdit implements Serializable {
    private String notes_title;
    private String time;
    private String content;
    private static final String TAG = "NotesEdit";
    SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");

    public NotesEdit(String title, String t, String ct){
        notes_title=title;
        time=t;
        content=ct;
    }

    public String getNotes_title() {
        return notes_title;
    }

    public String getTime() {
        return time;
    }

    public String getContent() {
        return content;
    }


    // write into json
    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("MULTINOTES_TITLE", getNotes_title());
        json.put("MULTINOTES_TIME", getTime());
        json.put("MULTINOTES_CONTENT", getContent());

        return json;
    }

    // read json
    public NotesEdit(JSONObject jsonObject) throws JSONException{
        notes_title = jsonObject.getString("MULTINOTES_TITLE");
        time = jsonObject.getString("MULTINOTES_TIME");
        content = jsonObject.getString("MULTINOTES_CONTENT");
    }


}

