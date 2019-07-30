package com.example.yangjingqi.assignment3;

import android.content.Context;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by yangjingqi on 7/22/17.
 */

public class JSONTask extends AsyncTask<String, Integer, String> {
    //  <Parameter, Progress, Result>

    private MainActivity mainActivity;
    private int count;
    private Context mContext;
    private String mFilename;


    private static final String TAG = "JSONTask";

    public JSONTask(MainActivity ma, Context c, String f){
        mContext = c;
        mFilename = f;
        mainActivity = ma;
    }

    public JSONTask(MainActivity ma) {
        mainActivity = ma;
    }
    @Override
    protected void onPreExecute() {
        Toast.makeText(mainActivity, "Loading Notes Data...", Toast.LENGTH_SHORT).show();
        super.onPreExecute();
    }

    //background process
    @Override
    protected String doInBackground(String... params) {
        String readJson = null;
        String name = null;
        Log.d(TAG, "doInBackground: Loading JSON File");
        StringBuilder sb = new StringBuilder();
        try {
            FileInputStream fis = mContext.openFileInput(mainActivity.getFile());
            InputStreamReader isr = new InputStreamReader(fis);
            fis.read(readJson.getBytes());
            JSONObject storedJson = new JSONObject(readJson);
            JSONObject idJson = storedJson.getJSONObject("NOTES");
            JsonReader reader = new JsonReader(new InputStreamReader(fis, mainActivity.getCode()));
            String line;
            reader.beginObject();
            while (reader.hasNext()) {
                line = reader.nextName();
                sb.append(line).append('\n');
            }
            reader.endObject();

        } catch (FileNotFoundException e) {

        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    // close dialog and give msg
    @Override
    protected void onPostExecute(String s) {
        ArrayList<NotesEdit> notesList = new ArrayList<NotesEdit>();
            Toast.makeText(mainActivity, "Loaded Notes from JSON.", Toast.LENGTH_SHORT).show();
            mainActivity.updateData(notesList);
//        }
    }

    protected void onProgressUpdate(Integer... values) {
//        super.onProgressUpdate(values);
//        progressBar.setProgress(values[0]);
    }

    private ArrayList<NotesEdit> parseJSON(String s) {
        ArrayList<NotesEdit> notesList = new ArrayList<>();
        try {
            JSONArray jObjMain = new JSONArray(s);
            count = jObjMain.length();

            for (int i = 0; i < jObjMain.length(); i++) {
                JSONObject jNotes = (JSONObject) jObjMain.get(i);
                String title = jNotes.getString("MULTINOTES_TITLE");
                String time = jNotes.getString("MULTINOTES_TIME");
                String content = jNotes.getString("MULTINOTES_CONTENT");
                notesList.add(new NotesEdit(title,time,content));
            }
            return notesList;
        } catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<NotesEdit> loadNotes() throws JSONException, IOException {
        Log.d(TAG, "loadNotes: ***********************************************");
        ArrayList<NotesEdit> notesList= new ArrayList<>();
        BufferedReader reader = null;
        //open file and read to StringBuilder
        try {
            //open file
            InputStream in = mContext.openFileInput(mFilename);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line;
            while ( (line=reader.readLine()) != null ) {
                jsonString.append(line);
            }
            //use JSONTokener parsing jsonString to JSONArray
            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
            //using JSONObject build note arraylist
            for (int i=0;i<array.length();i++) {
                //jsonObject
                NotesEdit n = new NotesEdit(array.getJSONObject(i));
                notesList.add(n);
            }
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found！");
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return notesList;
    }

}
