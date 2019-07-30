package com.example.yangjingqi.assignment4;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by yangjingqi on 7/26/17.
 */

public class AsyncStockLoader extends AsyncTask<String, Void, ArrayList<HashMap<String, String>>> {

    private MainActivity mainActivity;
    private ArrayList<HashMap<String, String>> symList = new ArrayList<>();

    int size = 0;

    private final String QueryURL = "http://d.yimg.com/aq/autoc?region=US&lang=en-US";
    private static final String TAG = "AsyncStockLoader";

    public AsyncStockLoader(MainActivity ma) {
        mainActivity = ma;
    }

    @Override
    protected void onPreExecute() {
        Toast.makeText(mainActivity, "Loading Stock Data...", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPostExecute(ArrayList<HashMap<String, String>> s) {
        mainActivity.addData(symList, size);
    }

    @Override
    protected ArrayList<HashMap<String, String>> doInBackground(String... params) {

        Uri.Builder buildURL = Uri.parse(QueryURL).buildUpon();

	    buildURL.appendQueryParameter("query", params[0]);

        String urlToUse = buildURL.build().toString();

        Log.d(TAG, "doInBackground: ******************************" + urlToUse);

        StringBuilder sb = new StringBuilder();

        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

        } catch (Exception e) {
            return null;
        }
        parseJSON(sb.toString());
        return null;
    }

    private void parseJSON(String s) {
        try {
            JSONObject jObjMain = new JSONObject(s);

            JSONObject jResultSet = jObjMain.getJSONObject("ResultSet");
//            sData.put("Query", jResultSet.getString("Query"));

            JSONArray result = jResultSet.getJSONArray("Result");

            for (int i=0; i<result.length(); i++) {
                HashMap<String, String> sData = new HashMap<>();

                JSONObject jStock = (JSONObject) result.get(i);
                sData.put("SYM", jStock.getString("symbol"));
                sData.put("NAME", jStock.getString("name"));
                sData.put("TYPE", jStock.getString("type"));
                size++;
                symList.add(sData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
