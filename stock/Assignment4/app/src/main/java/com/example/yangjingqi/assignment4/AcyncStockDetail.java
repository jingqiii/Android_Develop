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
import java.util.HashMap;

/**
 * Created by yangjingqi on 7/27/17.
 */

public class AcyncStockDetail extends AsyncTask<String, Void, String> {

    private MainActivity mainActivity;
    private HashMap<String, String> wData = new HashMap<>();

    private final String FinanceQueryURL = "http://finance.google.com/finance/info?client=ig";
    private static final String TAG = "AcyncStockDetail";

    public AcyncStockDetail (MainActivity ma) {
        mainActivity = ma;
    }

    @Override
    protected void onPreExecute() {
        Toast.makeText(mainActivity, "Loading Stock Detail...", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPostExecute(String s) {
        Stock stock = mainActivity.clickStock(wData);
        mainActivity.updateData(stock);
    }

    @Override
    protected String doInBackground(String... params) {

        Uri.Builder buildURL = Uri.parse(FinanceQueryURL).buildUpon();

        buildURL.appendQueryParameter("q", params[0]);

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

            Log.d(TAG, "doInBackground: " + sb.toString());

        } catch (Exception e) {
            Log.e(TAG, "doInBackground: ", e);
            return null;
        }
        parseJSON(sb.toString());
        return null;
    }

    private void parseJSON(String s) {

        try {
            String substr = s.substring(3);
            JSONArray stock = new JSONArray(substr);
            JSONObject jStock = (JSONObject) stock.get(0);

            wData.put("t", jStock.getString("t"));
            wData.put("l", jStock.getString("l"));
            wData.put("c", jStock.getString("c"));
            wData.put("cp", jStock.getString("cp"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
