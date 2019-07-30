package com.example.yangjingqi.assignment4;

import android.net.Uri;
import android.os.AsyncTask;
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
 * Created by yangjingqi on 7/28/17.
 */

public class updateOldStock extends AsyncTask<ArrayList<Stock>, Void, String> {

    private MainActivity mainActivity;
    private HashMap<String, String> wData = new HashMap<>();
    private ArrayList<Stock> sList = new ArrayList<>();  // Main content is here
    private final String FinanceQueryURL = "http://finance.google.com/finance/info?client=ig";
    private static final String TAG = "updateOldStock";
    private String c;

    public updateOldStock (MainActivity ma) {
        mainActivity = ma;
    }

    @Override
    protected void onPreExecute() {
        Toast.makeText(mainActivity, "Updatinging Stocks ...", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPostExecute(String s) {
        mainActivity.updateStockList(sList);
    }

    @Override
    protected String doInBackground(ArrayList<Stock>... params) {

        for (int i=0; i<params[0].size(); i++) {
            Stock s = params[0].get(i);
            String temp =
            c = s.getCompany();
            Uri.Builder buildURL = Uri.parse(FinanceQueryURL).buildUpon();
            buildURL.appendQueryParameter("q", s.getSymbol().toString());
            String urlToUse = buildURL.build().toString();

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
            Stock es = mainActivity.update_old_Stock(wData);
            sList.add(es);
        }
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
            wData.put("company", c);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
